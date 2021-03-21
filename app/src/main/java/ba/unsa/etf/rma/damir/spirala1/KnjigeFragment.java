package ba.unsa.etf.rma.damir.spirala1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by cico on 4/6/2018.
 */

public class KnjigeFragment extends Fragment {
    public static ArrayList<Knjiga> knjigePoKategoriji = new ArrayList<>();
    String trazeno = "";
    private static Context kontekst;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.knjige_fragment, container, false);
        kontekst = getActivity().getApplicationContext();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            trazeno = bundle.getString("trazenaKategorija");
        }


        final ListView lista = (ListView) getView().findViewById(R.id.listaKnjiga);

        Button dugmePovratak = (Button) getView().findViewById(R.id.dPovratak);
        dugmePovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista.setAdapter(null);
                ListeFragment lf = new ListeFragment();
                getFragmentManager().beginTransaction().replace(R.id.mjestoF1, lf).commit();
            }
        });

        char prvoSlovo1 = ' ';
        if (trazeno.length() > 0) {
            prvoSlovo1 = trazeno.charAt(0);
        }

        final char prvoSlovo = prvoSlovo1;

        azuriraj(prvoSlovo, 0);


        final KnjigaAdapter adapter = new KnjigaAdapter();
        lista.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        Bitmap slika;

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
                BOP.oboji(knjigePoKategoriji.get(position));
                azuriraj(prvoSlovo, 1);
                /*if (!knjigePoKategoriji.get(position).getId().equals("0")) {

                    for (int i = 0; i < KategorijeAkt.knjige.velicina(); i++) {

                        if (knjigePoKategoriji.get(position).getId().toUpperCase().equals(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getId().toUpperCase()))
                            KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).setObojeno(true);
                    }
                } else {

                    for (int i = 0; i < KategorijeAkt.knjige.velicina(); i++) {

                        if (knjigePoKategoriji.get(position).getNazivKnjige().toUpperCase().equals(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getNazivKnjige().toUpperCase()))
                            KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).setObojeno(true);
                    }
                }*/
                lista.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public class KnjigaAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return knjigePoKategoriji.size();
        }

        @Override
        public Knjiga getItem(int i) {
            return knjigePoKategoriji.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.element_knjige, null);

            TextView naziv = (TextView) v.findViewById(R.id.eNaziv);
            TextView autor = (TextView) v.findViewById(R.id.eAutor);

            ImageView naslovna = (ImageView) v.findViewById(R.id.eNaslovna);

            naziv.setText(knjigePoKategoriji.get(i).getNazivKnjige());
            autor.setText(knjigePoKategoriji.get(i).getImeAutora());

            if (knjigePoKategoriji.get(i).getSlika() != null) {
                Picasso.with(kontekst).load(knjigePoKategoriji.get(i).getSlika().toString()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(naslovna, new com.squareup.picasso.Callback() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
            } else {
                naslovna.setImageBitmap(knjigePoKategoriji.get(i).getNaslovnaStrana());
            }

            TextView datumObjavljivanja = (TextView) v.findViewById(R.id.eDatumObjavljivanja);
            TextView opis = (TextView) v.findViewById(R.id.eOpis);
            TextView brojStranica = (TextView) v.findViewById(R.id.eBrojStranica);

            datumObjavljivanja.setText(knjigePoKategoriji.get(i).getDatumObjavljivanja());
            opis.setText(knjigePoKategoriji.get(i).getOpis());
            brojStranica.setText(String.valueOf(knjigePoKategoriji.get(i).getBrojStranica()));

            BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);

            if (knjigePoKategoriji.get(i).getObojeno()) {
                v.setBackgroundResource(R.color.svijetloPlava);
            }

            final int j = i;

            Button dPreporuci = (Button) v.findViewById(R.id.dPreporuci);
            dPreporuci.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("idKnjige", knjigePoKategoriji.get(j).getId());

                    FragmentManager fm = getFragmentManager();
                    FragmentPreporuci nextFrag = new FragmentPreporuci();
                    nextFrag.setArguments(bundle);
                    if (KategorijeAkt.siriL) {
                        getActivity().findViewById(R.id.mjestoF1).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.mjestoF2).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.mjestoF3).setVisibility(View.VISIBLE);
                        fm.beginTransaction().replace(R.id.mjestoF3, nextFrag).commit();
                    } else fm.beginTransaction().replace(R.id.mjestoF1, nextFrag).commit();


                }
            });
            return v;
        }
    }


    public void azuriraj(char prvoSlovo, int indikator) {
        if (prvoSlovo == '0') {
            if (indikator == 0 && trazeno.length() > 0) {
                trazeno = trazeno.substring(1);
            }
            /*for (int i = 0; i < KategorijeAkt.knjige.velicina(); i++) {
                String dataKategorija = KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getKategorija().toUpperCase();
                if (dataKategorija.equals(trazeno))
                    knjigePoKategoriji.add(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i));
            }*/
            knjigePoKategoriji.clear();
            BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
            SQLiteDatabase db = BOP.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + BazaOpenHelper.DATABASE_TABLE + " where " + BazaOpenHelper.KATEGORIJA_NAZIV + "=\'" + trazeno.toLowerCase() + "\'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                long kategorija = cursor.getInt(cursor.getColumnIndex(BazaOpenHelper.KATEGORIJA_ID));
                knjigePoKategoriji = BOP.knjigeKategorije(kategorija);
            }
            cursor.close();
        }

        if (prvoSlovo == '1') {
            if (indikator == 0 && trazeno.length() > 0) {
                trazeno = trazeno.substring(1);
            }
            /*for (int i = 0; i < KategorijeAkt.knjige.velicina(); i++) {
                if (KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getAutori().isEmpty()) {
                    String datiAutor = KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getImeAutora().toUpperCase();
                    if (datiAutor.equals(trazeno))
                        knjigePoKategoriji.add(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i));
                } else {
                    for (int j = 0; j < KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getAutori().size(); j++) {
                        String datiAutor = KategorijeAkt.knjige.dajKnjiguNaPoziciji(i).getAutori().get(j).getImeiPrezime().toUpperCase();
                        if (datiAutor.equals(trazeno))
                            knjigePoKategoriji.add(KategorijeAkt.knjige.dajKnjiguNaPoziciji(i));
                    }
                }
            }*/
            knjigePoKategoriji.clear();
            BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
            SQLiteDatabase db = BOP.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + BazaOpenHelper.DATABASE_TABLE_2 + " where lower(" + BazaOpenHelper.AUTOR_IME + ")" + "=\'" + trazeno.toLowerCase() + "\'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                long autorId = cursor.getInt(cursor.getColumnIndex(BazaOpenHelper.AUTOR_ID));
                knjigePoKategoriji = BOP.knjigeAutora(autorId);
            }
            cursor.close();
        }
    }
}