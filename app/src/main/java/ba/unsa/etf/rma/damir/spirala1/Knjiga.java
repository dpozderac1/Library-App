package ba.unsa.etf.rma.damir.spirala1;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by cico on 3/25/2018.
 */

public class Knjiga implements Serializable,Parcelable{
    Bitmap naslovnaStrana;
    String imeAutora="";
    String nazivKnjige;
    String kategorija;
    Boolean obojeno = false;

    String id;
    String naziv;
    ArrayList<Autor> autori;
    String opis;
    String datumObjavljivanja;
    URL slika;
    int brojStranica;


    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, URL slika, int brojStranica) {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.slika = slika;
        this.brojStranica = brojStranica;
        this.naslovnaStrana=null;
        this.obojeno=false;
        this.nazivKnjige=naziv;
        this.imeAutora="";
        for(int i=0;i<autori.size();i++){
            if(!autori.get(i).getImeiPrezime().equals("null"))
            this.imeAutora+=autori.get(i).getImeiPrezime();
            if(i<autori.size()-1) this.imeAutora+=",";
        }
    }

    public Knjiga(Bitmap slika, String imeAutora, String nazivKnjige, String kategorija) {
        this.naslovnaStrana = slika;
        this.imeAutora = imeAutora;
        this.nazivKnjige = nazivKnjige;
        this.kategorija = kategorija;
        obojeno = false;
        this.id="0";
        this.naziv=nazivKnjige;
        this.autori=new ArrayList<>();
        String ime="";
        for(int i=0;i<imeAutora.length();i++){
                while(i<imeAutora.length() && imeAutora.charAt(i)!=','){
                    ime+=imeAutora.charAt(i);
                    i++;
                }
                if(i<ime.length() && imeAutora.charAt(i)==',') i++;
                autori.add(new Autor(ime,id));
                ime="";
        }
        this.opis="";
        this.datumObjavljivanja="";
        this.brojStranica=0;
    }

    protected Knjiga(Parcel in) {
        naslovnaStrana = in.readParcelable(Bitmap.class.getClassLoader());
        imeAutora = in.readString();
        nazivKnjige = in.readString();
        kategorija = in.readString();
        byte tmpObojeno = in.readByte();
        obojeno = tmpObojeno == 0 ? null : tmpObojeno == 1;
        id = in.readString();
        naziv = in.readString();
        opis = in.readString();
        datumObjavljivanja = in.readString();
        brojStranica = in.readInt();
    }

    public static final Creator<Knjiga> CREATOR = new Creator<Knjiga>() {
        @Override
        public Knjiga createFromParcel(Parcel in) {
            return new Knjiga(in);
        }

        @Override
        public Knjiga[] newArray(int size) {
            return new Knjiga[size];
        }
    };

    public Bitmap getNaslovnaStrana() {
        return naslovnaStrana;
    }

    public String getImeAutora() {
        return imeAutora;
    }

    public String getNazivKnjige() {
        return nazivKnjige;
    }

    public String getKategorija() {
        return kategorija;
    }

    public void setNaslovnaStrana(Bitmap naslovnaStrana) {
        this.naslovnaStrana = naslovnaStrana;
    }

    public void setImeAutora(String imeAutora) {
        this.imeAutora = imeAutora;
    }

    public void setNazivKnjige(String nazivKnjige) {
        this.nazivKnjige = nazivKnjige;
    }

    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    public Boolean getObojeno() {
        return obojeno;
    }

    public void setObojeno(Boolean obojeno) {
        this.obojeno = obojeno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Autor> getAutori() {
        return autori;
    }

    public void setAutori(ArrayList<Autor> autori) {
        this.autori = autori;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getDatumObjavljivanja() {
        return datumObjavljivanja;
    }

    public void setDatumObjavljivanja(String datumObjavljivanja) {
        this.datumObjavljivanja = datumObjavljivanja;
    }

    public URL getSlika() {
        return slika;
    }

    public void setSlika(URL slika) {
        this.slika = slika;
    }

    public int getBrojStranica() {
        return brojStranica;
    }

    public void setBrojStranica(int brojStranica) {
        this.brojStranica = brojStranica;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(naslovnaStrana, i);
        parcel.writeString(imeAutora);
        parcel.writeString(nazivKnjige);
        parcel.writeString(kategorija);
        parcel.writeByte((byte) (obojeno == null ? 0 : obojeno ? 1 : 2));
        parcel.writeString(id);
        parcel.writeString(naziv);
        parcel.writeString(opis);
        parcel.writeString(datumObjavljivanja);
        parcel.writeInt(brojStranica);
    }
}