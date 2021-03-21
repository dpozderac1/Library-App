package ba.unsa.etf.rma.damir.spirala1;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.app.Fragment;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cico on 4/4/2018.
 */

public class ListeFragment extends Fragment implements TextWatcher {
    CustomAdapter ca;
    ArrayList<String> jedinstveniAutori = new ArrayList<>();
    ArrayList<String> brojP = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.liste_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkAndRequestPermissions();
        final Button dodajKategorijuDugme = (Button) getView().findViewById(R.id.dDodajKategoriju);
        dodajKategorijuDugme.setEnabled(false);

        final EditText et = (EditText) getView().findViewById(R.id.tekstPretraga);
        final ListView list = (ListView) getView().findViewById(R.id.listaKategorija);
        et.addTextChangedListener(this);

        //vrsi pretragu knjiga, tj. filtriranje (dio 1)
        final Button dugmePretraga = (Button) getView().findViewById(R.id.dPretraga);


        //dodavanje kategorije pritiskom na dDodajKategoriju(dio 1)
        dodajKategorijuDugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KategorijeAkt.kategorije.add(et.getText().toString());
                ContentValues novi = new ContentValues ();
                BazaOpenHelper BOP = new BazaOpenHelper(getActivity(), BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
                long vraceno=BOP.dodajKategoriju(et.getText().toString());
                ca.notifyDataSetChanged();
                et.setText("");
                dugmePretraga.performClick();
            }
        });

        //otvara DodavanjeKnjigeAkt (dio 2)
        Button dodajKnjiguDugme = (Button) getView().findViewById(R.id.dDodajKnjigu);
        dodajKnjiguDugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DodavanjeKnjigeFragment nextFrag = new DodavanjeKnjigeFragment();
                if (KategorijeAkt.siriL) {
                    getActivity().findViewById(R.id.mjestoF1).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.mjestoF2).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.mjestoF3).setVisibility(View.VISIBLE);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.mjestoF3, nextFrag)
                            .commit();
                } else getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.mjestoF1, nextFrag)
                        .commit();
            }

        });

        //DRUGA SPIRALA
        final Context kontekst = getActivity().getApplicationContext();
        final Button dugmeKategorije = (Button) getView().findViewById(R.id.dKategorije);
        dugmeKategorije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popuni kategorije iz baze

                KategorijeAkt.kategorije.clear();
                BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
                SQLiteDatabase db = BOP.getWritableDatabase();

                Cursor cursor=db.rawQuery("select * from "+BazaOpenHelper.DATABASE_TABLE,null);


                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        KategorijeAkt.kategorije.add(cursor.getString(cursor.getColumnIndex(BazaOpenHelper.KATEGORIJA_NAZIV)));
                        cursor.moveToNext();
                    }
                }



                ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, KategorijeAkt.kategorije);
                list.setAdapter(adapter);

                dugmePretraga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText tekst = (EditText) getView().findViewById(R.id.tekstPretraga);
                        ca.getFilter().filter(tekst.getText());






                        Boolean pronadjen = false;
                        for (int i = 0; i < KategorijeAkt.kategorije.size(); i++) {
                            pronadjen = false;
                            if (KategorijeAkt.kategorije.get(i).toUpperCase().contains(tekst.getText().toString().toUpperCase()))
                                pronadjen = true;
                            if (pronadjen) {
                                dodajKategorijuDugme.setEnabled(false);
                                break;
                            }
                        }
                        if (!pronadjen) {
                            dodajKategorijuDugme.setEnabled(true);
                        }
                    }
                });
                //otvara aktivnost na klik liste (dio 3)
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String trazenaKategorija = list.getItemAtPosition(position).toString().toUpperCase();
                        trazenaKategorija = "0" + trazenaKategorija;

                        Bundle bundle = new Bundle();
                        bundle.putString("trazenaKategorija", trazenaKategorija);

                        KnjigeFragment nextFrag = new KnjigeFragment();
                        nextFrag.setArguments(bundle);
                        if (KategorijeAkt.siriL)
                            getFragmentManager().beginTransaction().replace(R.id.mjestoF2, nextFrag).commit();
                        else
                            getFragmentManager().beginTransaction().replace(R.id.mjestoF1, nextFrag).commit();
                    }
                });
                ca = new CustomAdapter(kontekst, KategorijeAkt.kategorije);
                list.setAdapter(ca);
                dugmePretraga.setVisibility(View.VISIBLE);
                dodajKategorijuDugme.setVisibility(View.VISIBLE);
                et.setVisibility(View.VISIBLE);
            }
        });

        final Button dugmeAutori = (Button) getView().findViewById(R.id.dAutori);
        dugmeAutori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> Autori = new ArrayList<>();

                for (int i = 0; i < KategorijeAkt.knjige.velicina(); i++) {
                    if(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getAutori().isEmpty()) {
                        Autori.add(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getImeAutora());
                    }
                    else{
                        for(int j=0;j<KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getAutori().size();j++){
                            Autori.add(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getAutori().get(j).getImeiPrezime());
                        }
                    }
                }
                //jedinstvene vrijednosti
                Set<String> hs = new HashSet<>();
                hs.addAll(Autori);
                jedinstveniAutori.clear();
                jedinstveniAutori.addAll(hs);

                //broj ponavljanja
                ArrayList<Integer> ponavljanja = new ArrayList<>();
                for (int i = 0; i < jedinstveniAutori.size(); i++) ponavljanja.add(0);
                for (int i = 0; i < jedinstveniAutori.size(); i++) {
                    for (int j = 0; j < Autori.size(); j++) {
                        if (Autori.get(j).equals(jedinstveniAutori.get(i)))
                            ponavljanja.set(i, ponavljanja.get(i) + 1);
                    }
                }

                for (int i = 0; i < jedinstveniAutori.size(); i++)
                    brojP.add("(" + ponavljanja.get(i).toString() + " knjjiga(e))");

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String trazenaKategorija = list.getItemAtPosition(position).toString().toUpperCase();
                        trazenaKategorija = "1" + trazenaKategorija;


                        Bundle bundle = new Bundle();
                        bundle.putString("trazenaKategorija", trazenaKategorija);

                        KnjigeFragment nextFrag = new KnjigeFragment();
                        nextFrag.setArguments(bundle);
                        FragmentManager fm = getFragmentManager();
                        if (KategorijeAkt.siriL)
                            fm.beginTransaction().replace(R.id.mjestoF2, nextFrag).commit();
                        else fm.beginTransaction().replace(R.id.mjestoF1, nextFrag).commit();
                    }
                });

                AutoriAdapter adapt = new AutoriAdapter();
                list.setAdapter(adapt);

                dugmePretraga.setVisibility(View.GONE);
                dodajKategorijuDugme.setVisibility(View.GONE);
                et.setVisibility(View.GONE);
            }
        });





        //treca Spirala dodajOnline
        Button dodajOnline=(Button) getActivity().findViewById(R.id.dDodajOnline);
        dodajOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentOnline nextFrag = new FragmentOnline();
                if(KategorijeAkt.siriL) {
                    getActivity().findViewById(R.id.mjestoF1).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.mjestoF2).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.mjestoF3).setVisibility(View.VISIBLE);
                    fm.beginTransaction().replace(R.id.mjestoF3, nextFrag).commit();
                }
                else fm.beginTransaction().replace(R.id.mjestoF1,nextFrag).commit();
            }
        });
    }

    public class AutoriAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return jedinstveniAutori.size();
        }

        @Override
        public String getItem(int i) {
            return jedinstveniAutori.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.element_autori, null);

            TextView naziv = (TextView) v.findViewById(R.id.ime);
            TextView broj = (TextView) v.findViewById(R.id.brojKnjiga);


            naziv.setText(jedinstveniAutori.get(i));
            broj.setText(brojP.get(i));

            return v;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //this.ca.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private  boolean checkAndRequestPermissions() {
        int permissionReadContacts = ContextCompat.checkSelfPermission(KategorijeAkt.kontekst,Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionReadContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
