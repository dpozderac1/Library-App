package ba.unsa.etf.rma.damir.spirala1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cico on 4/4/2018.
 */

public class DodavanjeKnjigeFragment extends Fragment{
    private static int PICK_IMAGE = 1;

    Bitmap naslovna=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dodavanje_knjige_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //deklarisanje spinnera
        Spinner s = (Spinner) getView().findViewById(R.id.sKategorijaKnjige);
        ArrayList<String> listaKategorija=new ArrayList<String>();
        final BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+BazaOpenHelper.DATABASE_TABLE,null);
        if(cursor.getCount()>0){
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    listaKategorija.add(cursor.getString(cursor.getColumnIndex(BazaOpenHelper.KATEGORIJA_NAZIV)));
                    cursor.moveToNext();
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listaKategorija);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        //ponisti dugme
        Button ponistiDugme = (Button) getView().findViewById(R.id.dPonisti);
        ponistiDugme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ListeFragment lf = new ListeFragment();

                if(KategorijeAkt.siriL){
                    getActivity().findViewById(R.id.mjestoF1).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.mjestoF2).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.mjestoF3).setVisibility(View.GONE);
                }

                getFragmentManager().beginTransaction().replace(R.id.mjestoF1, lf).commit();
            }

        });

        //nalazenje slike
        Button nadjiSLikuDugme = (Button) getView().findViewById(R.id.dNadjiSliku);
        nadjiSLikuDugme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Ne postoji aplikacija koja moze obaviti datu akciju!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ImageView slika = (ImageView) getView().findViewById(R.id.naslovnaStr);
        final EditText autor = (EditText) getView().findViewById(R.id.imeAutora);
        final EditText naziv = (EditText) getView().findViewById(R.id.nazivKnjige);

        //slika.buildDrawingCache();

        final Spinner spin = (Spinner) getView().findViewById(R.id.sKategorijaKnjige);

        //upisivanje knjige
        Button upisiKnjiguDugme = (Button) getView().findViewById(R.id.dUpisiKnjigu);
        upisiKnjiguDugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spin!=null && spin.getSelectedItem()!=null) {
                    String izabrano = spin.getSelectedItem().toString();
                    long broj=BOP.dodajKnjigu(new Knjiga(naslovna, autor.getText().toString(), naziv.getText().toString(), izabrano));
                    if (broj == -1) {
                        Toast.makeText(getActivity().getApplicationContext(), "Knjiga se ne moze dodati jer vec postoji.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        KategorijeAkt.knjige.dodajKnjigu(new Knjiga(naslovna, autor.getText().toString(), naziv.getText().toString(), izabrano));
                        Toast.makeText(getActivity().getApplicationContext(), "Knjiga je upisana.", Toast.LENGTH_SHORT).show();
                        autor.setText("");
                        naziv.setText("");
                        slika.setImageBitmap(null);
                        naslovna = null;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && null != data && data.getData() != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap slika1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                naslovna = slika1;
                ImageView imageview = (ImageView) getActivity().findViewById(R.id.naslovnaStr);
                imageview.setImageBitmap(slika1);
            } catch (IOException izuzetak) {
                izuzetak.printStackTrace();
            }
        }
    }
}
