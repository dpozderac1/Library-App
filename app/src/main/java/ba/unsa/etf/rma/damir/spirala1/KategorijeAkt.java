package ba.unsa.etf.rma.damir.spirala1;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KategorijeAkt extends AppCompatActivity {

    public static SpisakKnjiga knjige = new SpisakKnjiga();
    public static ArrayList<String> kategorije = new ArrayList<String>() {{
        /*add("roman");
        add("krimi");
        add("putopis");
        add("novela");
        add("sci-fi");*/
    }};

    public static Boolean siriL = false;
    public static Context kontekst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kategorije_akt);

        kontekst = getApplicationContext();

        //dodatak za bazu
        Stetho.initializeWithDefaults(this);

        uradiUpdate();

        FragmentManager fm = getFragmentManager();
        FrameLayout knjigeFrame = (FrameLayout) findViewById(R.id.mjestoF2);
        FrameLayout dodavanjeKnjigeFrame = (FrameLayout) findViewById(R.id.mjestoF3);
        if (knjigeFrame != null) {
            siriL = true;
            FragmentTransaction transakcija = fm.beginTransaction();
            KnjigeFragment kf = new KnjigeFragment();
            dodavanjeKnjigeFrame.setVisibility(View.INVISIBLE);
            transakcija.replace(R.id.mjestoF2, kf);
            ListeFragment lf = new ListeFragment();
            transakcija.replace(R.id.mjestoF1, lf, "ALista");
            transakcija.commit();
        } else {
            siriL = false;
            ListeFragment fl = (ListeFragment) fm.findFragmentByTag("ALista");
            if (fl == null) {
                fl = new ListeFragment();
                fm.beginTransaction().replace(R.id.mjestoF1, fl, "ALista").commit();
            } else {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    public void uradiUpdate() {
        BazaOpenHelper BOP = new BazaOpenHelper(kontekst, BazaOpenHelper.DATABASE_NAME, null, BazaOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();

        /*db.execSQL("delete from " + BazaOpenHelper.DATABASE_TABLE);
        db.execSQL("delete from " + BazaOpenHelper.DATABASE_TABLE_1);
        db.execSQL("delete from " + BazaOpenHelper.DATABASE_TABLE_2);
        db.execSQL("delete from " + BazaOpenHelper.DATABASE_TABLE_3);*/

        Cursor cursor = db.rawQuery("select * from " + BazaOpenHelper.DATABASE_TABLE, null);
        ArrayList<Knjiga> noveKnjige = new ArrayList<Knjiga>();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int idKategorije = cursor.getInt(cursor.getColumnIndex(BazaOpenHelper.KATEGORIJA_ID));
                    noveKnjige = BOP.knjigeKategorije(idKategorije);
                    for (int i = 0; i < noveKnjige.size(); i++)
                        knjige.dodajKnjigu(noveKnjige.get(i));
                    noveKnjige.clear();
                    cursor.moveToNext();
                }
            }
        }
    }
}
