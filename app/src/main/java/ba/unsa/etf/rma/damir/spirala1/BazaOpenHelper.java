package ba.unsa.etf.rma.damir.spirala1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.CursorAdapter;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by cico on 5/24/2018.
 */

public class BazaOpenHelper extends SQLiteOpenHelper {

    Context kontekst=null;

    public static final String DATABASE_NAME="mojaBaza.db";
    public static final int DATABASE_VERSION=1;

    //tabela Kategorija
    public static final String DATABASE_TABLE="Kategorija";
    public static final String KATEGORIJA_ID="_id";
    public static final String KATEGORIJA_NAZIV="naziv";

    //tabela Knjiga
    public static final String DATABASE_TABLE_1="Knjiga";
    public static final String KNJIGA_ID="_id";
    public static final String KNJIGA_NAZIV="naziv";
    public static final String KNJIGA_OPIS="opis";
    public static final String KNJIGA_DATUM_OBJAVLJIVANJA="datumObjavljivanja";
    public static final String KNJIGA_BROJ_STRANICA="brojStranica";
    public static final String KNJIGA_ID_WEB_SERVIS="idWebServis";
    public static final String KNJIGA_ID_KATEGORIJE="idkategorije";
    public static final String KNJIGA_SLIKA="slika";
    public static final String KNJIGA_PREGLEDANA="pregledana";

    //tabela Autor
    public static final String DATABASE_TABLE_2="Autor";
    public static final String AUTOR_ID="_id";
    public static final String AUTOR_IME="ime";

    //tabela Autorstvo
    public static final String DATABASE_TABLE_3="Autorstvo";
    public static final String AUTORSTVO_ID="_id";
    public static final String AUTORSTVO_ID_AUTORA="idautora";
    public static final String AUTORSTVO_ID_KNJIGE="idknjige";

    private static final String DATABASE_CREATE="create table "+DATABASE_TABLE+" ("+KATEGORIJA_ID+" integer primary key autoincrement, "+KATEGORIJA_NAZIV+" text not null);";
    private static final String DATABASE_CREATE_1="create table "+DATABASE_TABLE_1+" ("+KNJIGA_ID+" integer primary key autoincrement, "+KNJIGA_NAZIV+" text not null, "+KNJIGA_OPIS+" text not null, "+ KNJIGA_DATUM_OBJAVLJIVANJA+" text not null, "
            +KNJIGA_BROJ_STRANICA+" integer, "+KNJIGA_ID_WEB_SERVIS+ " text not null, " +KNJIGA_ID_KATEGORIJE+" integer, "+KNJIGA_SLIKA+" text not null, "+KNJIGA_PREGLEDANA+" integer);";
    private static final String DATABASE_CREATE_2="create table "+DATABASE_TABLE_2+" ("+AUTOR_ID+" integer primary key autoincrement, "+AUTOR_IME+" text not null);";
    private static final String DATABASE_CREATE_3="create table "+DATABASE_TABLE_3+" ("+AUTORSTVO_ID+" integer primary key autoincrement, "+AUTORSTVO_ID_AUTORA+" integer, "+AUTORSTVO_ID_KNJIGE+" integer);";



    public BazaOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_1);
        sqLiteDatabase.execSQL(DATABASE_CREATE_2);
        sqLiteDatabase.execSQL(DATABASE_CREATE_3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_2);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_3);
        onCreate(sqLiteDatabase);
    }


    long dodajKategoriju(String naziv) {
        BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+DATABASE_TABLE,null);

        long broj=-1;
        String ime="";
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if(cursor.getString(cursor.getColumnIndex(KATEGORIJA_NAZIV)).equals(naziv)) {
                    ime += cursor.getString(cursor.getColumnIndex(KATEGORIJA_NAZIV));
                }
                cursor.moveToNext();
            }
        }
        if(ime.equals("")){
            ContentValues novi = new ContentValues();
            novi.put(KATEGORIJA_NAZIV, naziv);
            broj=db.insert(DATABASE_TABLE,null,novi);
        }

        return broj;
    }

    long dodajKnjigu(Knjiga knjiga){
        BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from "+DATABASE_TABLE_1,null);

        long broj=-1;
        String imeId="";
        String imeIme="";
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if(!knjiga.getId().equals("0")) {
                    if (cursor.getString(cursor.getColumnIndex(KNJIGA_ID_WEB_SERVIS)).equals(knjiga.getId())) {
                        imeId += cursor.getString(cursor.getColumnIndex(KNJIGA_ID_WEB_SERVIS));
                    }
                }
                else {
                    if(cursor.getString(cursor.getColumnIndex(KNJIGA_NAZIV)).equals(knjiga.getNaziv())) {
                        imeIme += cursor.getString(cursor.getColumnIndex(KNJIGA_NAZIV));
                    }
                }
                cursor.moveToNext();
            }
        }
        if(imeId.equals("") && imeIme.equals("")){
            ContentValues novi = new ContentValues();
            novi.put(KNJIGA_NAZIV, knjiga.naziv);
            novi.put(KNJIGA_OPIS, knjiga.opis);
            novi.put(KNJIGA_DATUM_OBJAVLJIVANJA,knjiga.getDatumObjavljivanja());
            novi.put(KNJIGA_BROJ_STRANICA,knjiga.getBrojStranica());
            novi.put(KNJIGA_ID_WEB_SERVIS,knjiga.getId());

            //pretrazi kategorije
            Cursor kursor1=db.rawQuery("select * from "+DATABASE_TABLE+" where "+KATEGORIJA_NAZIV+"=\'"+knjiga.getKategorija()+"\'",null);
            if(kursor1.getCount()>0){
                kursor1.moveToFirst();
                novi.put(KNJIGA_ID_KATEGORIJE,kursor1.getString(kursor1.getColumnIndexOrThrow(KATEGORIJA_ID)));
            }

            if(knjiga.getSlika()!=null) {
                novi.put(KNJIGA_SLIKA, knjiga.getSlika().toString());
            }
            else if(knjiga.getNaslovnaStrana()==null){
                novi.put(KNJIGA_SLIKA,"prazno");
            }
            else{
                String kodirani=pretvoriUString(knjiga.getNaslovnaStrana());
                novi.put(KNJIGA_SLIKA, kodirani);
            }
            novi.put(KNJIGA_PREGLEDANA,String.valueOf(knjiga.getObojeno()? 1 : 0));

            broj=db.insert(DATABASE_TABLE_1,null,novi);


            //pretrazi Autore i Autorstvo
            for(int i=0;i<knjiga.getAutori().size();i++){
                Cursor kursor2=db.rawQuery("select * from "+DATABASE_TABLE_2+" where "+AUTOR_IME+"=\'"+knjiga.getAutori().get(i).getImeiPrezime()+"\'",null);
                if(kursor2.getCount()<=0){
                    ContentValues novi1=new ContentValues();
                    novi1.put(AUTOR_IME,knjiga.getAutori().get(i).getImeiPrezime());
                    long idAutora=db.insert(DATABASE_TABLE_2,null,novi1);
                    ContentValues novi2=new ContentValues();
                    novi2.put(AUTORSTVO_ID_AUTORA,idAutora);
                    novi2.put(AUTORSTVO_ID_KNJIGE,broj);
                    db.insert(DATABASE_TABLE_3,null,novi2);
                }
                else{
                    kursor2.moveToFirst();
                    long idAutora=kursor2.getLong(kursor2.getColumnIndex(AUTOR_ID));
                    ContentValues novi2=new ContentValues();
                    novi2.put(AUTORSTVO_ID_AUTORA,idAutora);
                    novi2.put(AUTORSTVO_ID_KNJIGE,broj);
                    db.insert(DATABASE_TABLE_3,null,novi2);
                }
                kursor2.close();
            }
            cursor.close();
            kursor1.close();
        }

        return broj;
    }

    ArrayList<Knjiga> knjigeKategorije(long idKategorije){
        BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();

        ArrayList<Knjiga> sveKnjige=new ArrayList<Knjiga>();
        //kategorija
        String kategorija="";
        Cursor kursor1=db.rawQuery("select * from "+DATABASE_TABLE+" where "+KATEGORIJA_ID+"=\'"+String.valueOf(idKategorije)+"\'",null);
        if(kursor1.getCount()>0){
            kursor1.moveToFirst();
            kategorija=kursor1.getString(kursor1.getColumnIndex(KATEGORIJA_NAZIV));
        }

        Cursor cursor=db.rawQuery("select * from "+DATABASE_TABLE_1+" where "+KNJIGA_ID_KATEGORIJE+"=\'"+String.valueOf(idKategorije)+"\'",null);
        if(cursor.getCount()>0){
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id=cursor.getString(cursor.getColumnIndex(KNJIGA_ID_WEB_SERVIS));
                    String naziv=cursor.getString(cursor.getColumnIndex(KNJIGA_NAZIV));

                    //nadji Autore
                    long idUBazi=cursor.getInt(cursor.getColumnIndex(KNJIGA_ID));
                    ArrayList<Autor> autori=new ArrayList<Autor>();
                    Cursor kursor2=db.rawQuery("select * from "+DATABASE_TABLE_3+" where "+AUTORSTVO_ID_KNJIGE+"=\'"+String.valueOf(idUBazi)+"\'",null);
                    if(kursor2.getCount()>0){
                        if(kursor2.moveToFirst()){
                            while(!kursor2.isAfterLast()){
                                long idAutora=kursor2.getInt(kursor2.getColumnIndex(AUTORSTVO_ID_AUTORA));
                                Cursor kursor3=db.rawQuery("select * from "+DATABASE_TABLE_2+" where "+AUTOR_ID+"=\'"+String.valueOf(idAutora)+"\'",null);
                                if(kursor3.getCount()>0){
                                    if(kursor3.moveToFirst()){
                                        while(!kursor3.isAfterLast()){
                                            autori.add(new Autor(kursor3.getString(kursor3.getColumnIndex(AUTOR_IME)),String.valueOf(idUBazi)));
                                            kursor3.moveToNext();
                                        }
                                    }
                                }
                                kursor2.moveToNext();
                                kursor3.close();
                            }
                        }
                    }
                    kursor2.close();

                    String opis=cursor.getString(cursor.getColumnIndex(KNJIGA_OPIS));
                    String datumObjavljivanja=cursor.getString(cursor.getColumnIndex(KNJIGA_DATUM_OBJAVLJIVANJA));
                    String slika=cursor.getString(cursor.getColumnIndex(KNJIGA_SLIKA));
                    String brojStranica=cursor.getString(cursor.getColumnIndex(KNJIGA_BROJ_STRANICA));
                    String obojeno=cursor.getString(cursor.getColumnIndex(KNJIGA_PREGLEDANA));
                    if(opis.equals("") && Integer.parseInt(brojStranica)==0 && datumObjavljivanja.equals("") && id.equals("0")){
                        String aut="";
                        for(int i=0;i<autori.size();i++){
                            aut+=autori.get(i).getImeiPrezime();
                            if(i<autori.size()-1) aut+=',';
                        }
                        if(slika.equals("prazno")) {
                            sveKnjige.add(new Knjiga(null,aut,naziv,kategorija));
                            sveKnjige.get(sveKnjige.size() - 1).setObojeno(Integer.parseInt(obojeno) != 0);
                        }
                        else {
                            sveKnjige.add(new Knjiga(pretvoriUBitampu(slika), aut, naziv, kategorija));
                            sveKnjige.get(sveKnjige.size() - 1).setObojeno(Integer.parseInt(obojeno) != 0);
                        }
                    }
                    else {
                        try {
                            sveKnjige.add(new Knjiga(id, naziv, autori, opis, datumObjavljivanja, new URL(slika), Integer.parseInt(brojStranica)));
                            sveKnjige.get(sveKnjige.size() - 1).setObojeno(Integer.parseInt(obojeno) != 0);
                            sveKnjige.get(sveKnjige.size() - 1).setKategorija(kategorija);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    cursor.moveToNext();
                }
            }

        }
        kursor1.close();
        cursor.close();
        return sveKnjige;
    }

    ArrayList<Knjiga> knjigeAutora(long idAutora){
        ArrayList<Knjiga> sveKnjige=new ArrayList<Knjiga>();
        BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+DATABASE_TABLE_3+" where "+AUTORSTVO_ID_AUTORA+"=\'"+String.valueOf(idAutora)+"\'",null);
        if(cursor.getCount()>0){
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    String idKnjige=cursor.getString(cursor.getColumnIndex(AUTORSTVO_ID_KNJIGE));
                    Cursor kursor1=db.rawQuery("select * from "+DATABASE_TABLE_1+" where "+KNJIGA_ID+"=\'"+idKnjige+"\'",null);
                    if(kursor1.getCount()>0) {
                        kursor1.moveToFirst();

                        String id = kursor1.getString(kursor1.getColumnIndex(KNJIGA_ID_WEB_SERVIS));
                        String naziv = kursor1.getString(kursor1.getColumnIndex(KNJIGA_NAZIV));
                        ArrayList<Autor> autori = new ArrayList<Autor>();

                        long idKategorije = kursor1.getInt(kursor1.getColumnIndex(KNJIGA_ID_KATEGORIJE));
                        String kategorija = "";
                        Cursor kursor2 = db.rawQuery("select * from " + DATABASE_TABLE + " where " + KATEGORIJA_ID + "=\'" + idKategorije + "\'", null);
                        if (kursor2.getCount() > 0) {
                            kursor2.moveToFirst();
                            kategorija = kursor2.getString(kursor2.getColumnIndex(KATEGORIJA_NAZIV));
                        }
                        kursor2.close();
                        //autori
                        Cursor kursor3 = db.rawQuery("select * from " + DATABASE_TABLE_2 + " where " + AUTOR_ID + "=\'" + String.valueOf(idAutora) + "\'", null);
                        if (kursor3.getCount() > 0) {
                            if (kursor3.moveToFirst()) {
                                while (!kursor3.isAfterLast()) {
                                    autori.add(new Autor(kursor3.getString(kursor3.getColumnIndex(AUTOR_IME)), idKnjige));
                                    kursor3.moveToNext();
                                }
                            }
                        }
                        kursor3.close();
                        String opis = kursor1.getString(kursor1.getColumnIndex(KNJIGA_OPIS));
                        String datumObjavljivanja = kursor1.getString(kursor1.getColumnIndex(KNJIGA_DATUM_OBJAVLJIVANJA));
                        String slika = kursor1.getString(kursor1.getColumnIndex(KNJIGA_SLIKA));
                        String brojStranica = kursor1.getString(kursor1.getColumnIndex(KNJIGA_BROJ_STRANICA));
                        String obojeno = kursor1.getString(kursor1.getColumnIndex(KNJIGA_PREGLEDANA));

                        if(opis.equals("") && Integer.parseInt(brojStranica)==0 && datumObjavljivanja.equals("") && id.equals("0")){
                            String aut="";
                            for(int i=0;i<autori.size();i++){
                                aut+=autori.get(i).getImeiPrezime();
                                if(i<autori.size()-1) aut+=',';
                            }
                            if(slika.equals("prazno")) {
                                sveKnjige.add(new Knjiga(null,aut,naziv,kategorija));
                                sveKnjige.get(sveKnjige.size() - 1).setObojeno(Integer.parseInt(obojeno) != 0);
                            }
                            else {
                                sveKnjige.add(new Knjiga(pretvoriUBitampu(slika), aut, naziv, kategorija));
                                sveKnjige.get(sveKnjige.size() - 1).setObojeno(Integer.parseInt(obojeno) != 0);
                            }
                        }
                        else {
                            try {
                                sveKnjige.add(new Knjiga(id, naziv, autori, opis, datumObjavljivanja, new URL(slika), Integer.parseInt(brojStranica)));
                                sveKnjige.get(sveKnjige.size() - 1).setObojeno(Integer.parseInt(obojeno) != 0);
                                sveKnjige.get(sveKnjige.size() - 1).setKategorija(kategorija);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    kursor1.close();
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        return sveKnjige;
    }


    private Bitmap pretvoriUBitampu(String encodedString){
        try{
            byte[] imageAsBytes = Base64.decode(encodedString.getBytes(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    private String pretvoriUString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public int provjeriObojenost(Knjiga knjiga){
        int broj=0;
        BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();
        if(knjiga.getId().equals("0")){
            Cursor cursor=db.rawQuery("select * from "+DATABASE_TABLE_1+" where "+KNJIGA_NAZIV+"=\'"+knjiga.getNaziv()+"\'",null);
            if(cursor.getCount()>0){
                if(cursor.moveToFirst()){
                    broj=Integer.parseInt(cursor.getString(cursor.getColumnIndex(KNJIGA_PREGLEDANA)));
                }
            }
        }
        else {
            Cursor cursor = db.rawQuery("select * from " + DATABASE_TABLE_1 + " where " + KNJIGA_ID_WEB_SERVIS + "=\'" + knjiga.getId() + "\'", null);
            if(cursor.getCount()>0){
                if(cursor.moveToFirst()){
                    broj=Integer.parseInt(cursor.getString(cursor.getColumnIndex(KNJIGA_PREGLEDANA)));
                }
            }
        }
        return broj;
    }

    public void oboji(Knjiga knjiga){
        BazaOpenHelper BOP = new BazaOpenHelper(KategorijeAkt.kontekst, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = BOP.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KNJIGA_PREGLEDANA,1); //These Fields should be your String values of actual column names
        if(knjiga.getId().equals("0")){
            db.update(DATABASE_TABLE_1, cv, KNJIGA_NAZIV+"=\'"+knjiga.getNaziv()+"\'", null);
        }
        else {
            db.update(DATABASE_TABLE_1, cv, KNJIGA_ID_WEB_SERVIS+"=\'"+knjiga.getId()+"\'", null);
        }
    }
}
