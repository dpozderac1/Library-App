package ba.unsa.etf.rma.damir.spirala1;

import java.util.ArrayList;

/**
 * Created by cico on 5/13/2018.
 */

public class Autor {
    String imeiPrezime;
    ArrayList<String> knjige=new ArrayList<String>();


    public Autor(String imeiPrezime, String id) {
        this.imeiPrezime = imeiPrezime;
        this.knjige.add(id);
    }

    public String getImeiPrezime() {
        return imeiPrezime;
    }

    public void setImeiPrezime(String imeiPrezime) {
        this.imeiPrezime = imeiPrezime;
    }

    public ArrayList<String> getKnjige() {
        return knjige;
    }

    public void setKnjige(ArrayList<String> knjige) {
        this.knjige = knjige;
    }

    public void dodajKnjigu(String id){
        boolean postoji=false;
        for(int i=0;i<knjige.size();i++){
            if(knjige.get(i).equals(id)) {
                postoji = true;
                break;
            }
        }
        if(!postoji) knjige.add(id);
    }
}
