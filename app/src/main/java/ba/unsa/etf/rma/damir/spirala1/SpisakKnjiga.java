package ba.unsa.etf.rma.damir.spirala1;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cico on 3/27/2018.
 */

public class SpisakKnjiga {

    ArrayList<Knjiga> allKnjige;

    public SpisakKnjiga() {
        this.allKnjige = new ArrayList<>();
        /*allKnjige.add(new Knjiga(null,"Ivo Andric","Na Drini cuprija","haha"));
        allKnjige.add(new Knjiga(null,"Lav Tolstoj","Ana Karenjina","roman"));
        allKnjige.add(new Knjiga(null,"Agata Kristi","Hercule Poirot","krimi"));
        allKnjige.add(new Knjiga(null,"Lav Tolstoj","Rat i mir","roman"));*/
    }

    public ArrayList<Knjiga> getAllKnjige() {
        return allKnjige;
    }

    public void setAllKnjige(ArrayList<Knjiga> allKnjige) {
        this.allKnjige = allKnjige;
    }

    public void dodajKnjigu(Knjiga knjiga) {
        allKnjige.add(knjiga);
    }

    public Knjiga dajKnjiguNaPoziciji(int pozicija) {
        return allKnjige.get(pozicija);
    }

    public int velicina(){
        return allKnjige.size();
    }


}
