package ba.unsa.etf.rma.damir.spirala1;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.midi.MidiOutputPort;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by cico on 5/13/2018.
 */

public class FragmentOnline extends Fragment implements DohvatiKnjige.IDohvatiKnjigeDone, DohvatiNajnovije.IDohvatiNajnovijeDone, MojResultReceiver.Receiver {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Spinner s = (Spinner) getView().findViewById(R.id.sKategorije);

        ArrayList<String> kat=new ArrayList<String>();

        BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+BazaOpenHelper.DATABASE_TABLE,null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                kat.add(cursor.getString(cursor.getColumnIndex(BazaOpenHelper.KATEGORIJA_NAZIV)));
                cursor.moveToNext();
            }
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kat);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        Button dPovratak = (Button) getActivity().findViewById(R.id.dPovratak);
        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListeFragment lf = new ListeFragment();

                if (KategorijeAkt.siriL) {
                    getActivity().findViewById(R.id.mjestoF1).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.mjestoF2).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.mjestoF3).setVisibility(View.GONE);
                }

                getFragmentManager().beginTransaction().replace(R.id.mjestoF1, lf).commit();
            }
        });


        final Button dAdd = (Button) getActivity().findViewById(R.id.dAdd);
        dAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Spinner spin = (Spinner) getView().findViewById(R.id.sRezultat);
                if(spin!=null && spin.getSelectedItem()!=null) {


                    String izabranaKnjiga = spin.getSelectedItem().toString();
                    int pozicijaKnjige = spin.getSelectedItemPosition();

                    Spinner spinspin = (Spinner) getView().findViewById(R.id.sKategorije);
                    String izabranaKategorija = spinspin.getSelectedItem().toString();


                    BazaOpenHelper BOP = new BazaOpenHelper(getActivity(), BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
                    vraceneKnjige.get(pozicijaKnjige).setKategorija(izabranaKategorija);
                    long vraceno=BOP.dodajKnjigu(vraceneKnjige.get(pozicijaKnjige));

                    boolean postoji=false;
                    for(int i=0;i<KategorijeAkt.knjige.velicina();i++){
                        if(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getId().equals(vraceneKnjige.get(pozicijaKnjige).getId())){
                            postoji=true;
                        }
                    }

                    if(postoji) Toast.makeText(getActivity().getApplicationContext(), "Knjigu ne mozete dodati jer vec postoji.", Toast.LENGTH_SHORT).show();
                    else {
                        KategorijeAkt.knjige.dodajKnjigu(vraceneKnjige.get(pozicijaKnjige));
                        KategorijeAkt.knjige.dajKnjiguNaPoziciji(KategorijeAkt.knjige.velicina() - 1).setKategorija(izabranaKategorija);

                        Toast.makeText(getActivity().getApplicationContext(), "Knjiga je upisana.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        final EditText tekstUpit = (EditText) getActivity().findViewById(R.id.tekstUpit);
        Button dugmeRun = (Button) getActivity().findViewById(R.id.dRun);
        dugmeRun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int brojRijeci = prebrojRijeci(tekstUpit.getText().toString());
                brojKnjiga = brojRijeci;


                boolean sadrziAutor = tekstUpit.getText().toString().toLowerCase().contains("autor:");
                int pozicija = tekstUpit.getText().toString().toLowerCase().indexOf("autor:");
                boolean autorJe = false;
                if (sadrziAutor && pozicija == 0) {
                    autorJe = true;
                }


                boolean sadrziKorisnik = tekstUpit.getText().toString().toLowerCase().contains("korisnik:");
                pozicija = tekstUpit.getText().toString().toLowerCase().indexOf("korisnik:");
                boolean korisnikJe = false;
                if (sadrziKorisnik && pozicija == 0) {
                    korisnikJe = true;
                }

                if (autorJe) {
                    isprazni();
                    dAdd.setEnabled(true);
                    if (tekstUpit.getText().toString().length() > 6) {
                        String trazeniAutor = "";
                        for (int i = 6; i < tekstUpit.getText().toString().length(); i++)
                            trazeniAutor += tekstUpit.getText().toString().charAt(i);
                        new DohvatiNajnovije((DohvatiNajnovije.IDohvatiNajnovijeDone) FragmentOnline.this).execute(trazeniAutor);
                    }
                } else if (korisnikJe) {
                    isprazni();
                    if (tekstUpit.getText().toString().length() > 9) {
                        String trazeniKorisnik = "";
                        for (int i = 9; i < tekstUpit.getText().toString().length(); i++)
                            trazeniKorisnik += tekstUpit.getText().toString().charAt(i);
                        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), KnjigePoznanika.class);
                        MojResultReceiver mReceiver = new MojResultReceiver(new Handler());
                        mReceiver.setReceiver(FragmentOnline.this);
                        intent.putExtra("naziv", trazeniKorisnik);
                        intent.putExtra("receiver", mReceiver);
                        getActivity().startService(intent);
                    }
                } else {
                    dAdd.setEnabled(true);
                    if (brojRijeci == 1) {
                        isprazni();
                        new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone) FragmentOnline.this).execute(tekstUpit.getText().toString());
                    } else if (brojRijeci > 1) {
                        isprazni();
                        String tekst = tekstUpit.getText().toString();
                        String s1 = "";
                        for (int i = 0; i < tekst.length(); i++) {
                            if (tekst.charAt(i) == ';') {
                                new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone) FragmentOnline.this).execute(s1);
                                s1 = "";
                            } else if (i == tekst.length() - 1) {
                                s1 += tekst.charAt(i);
                                new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone) FragmentOnline.this).execute(s1);
                                s1 = "";
                            } else {
                                s1 += tekst.charAt(i);
                            }
                        }
                    }
                }
            }
        });
    }

    public static int prebrojRijeci(String s) {

        int brojRijeci = 0;
        if (!s.isEmpty()) brojRijeci++;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ';') brojRijeci++;
        }
        return brojRijeci;
    }

    void isprazni() {
        lista.clear();
        vraceneKnjige.clear();
    }


    ArrayList<Knjiga> vraceneKnjige = new ArrayList<Knjiga>();
    ArrayList<String> lista = new ArrayList<String>();
    int brojKnjiga = 0;

    @Override
    public void onDohvatiDone(ArrayList<Knjiga> rez) {
        for (int i = 0; i < rez.size(); i++) {
            lista.add(rez.get(i).getNaziv());
            vraceneKnjige.add(rez.get(i));
        }
        Spinner s = (Spinner) getActivity().findViewById(R.id.sRezultat);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }


    @Override
    public void onNajnovijeDone(ArrayList<Knjiga> rez) {
        for (int i = 0; i < rez.size(); i++) {
            lista.add(rez.get(i).getNaziv());
            vraceneKnjige.add(rez.get(i));
        }
        Spinner s = (Spinner) getActivity().findViewById(R.id.sRezultat);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Button dAdd = (Button) getActivity().findViewById(R.id.dAdd);
        if (resultCode == KnjigePoznanika.STATUS_START) {
            dAdd.setEnabled(false);
            Toast.makeText(getActivity(), "Pokrenut je servis KnjigaPoznanika", Toast.LENGTH_SHORT);
        } else if (resultCode == KnjigePoznanika.STATUS_FINISH) {
            dAdd.setEnabled(true);
            ArrayList<Knjiga> results = resultData.getParcelableArrayList("result");
            for (int i = 0; i < results.size(); i++) {
                lista.add(results.get(i).getNaziv());
                vraceneKnjige.add(results.get(i));
            }
            Spinner s = (Spinner) getActivity().findViewById(R.id.sRezultat);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lista);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s.setAdapter(adapter);
        } else if (resultCode == KnjigePoznanika.STATUS_ERROR) {
            dAdd.setEnabled(false);
            String error = resultData.getString(Intent.EXTRA_TEXT);
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }
}