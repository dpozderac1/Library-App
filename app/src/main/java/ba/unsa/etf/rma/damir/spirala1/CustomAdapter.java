package ba.unsa.etf.rma.damir.spirala1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by cico on 3/25/2018.
 */

public class CustomAdapter extends BaseAdapter implements Filterable{
    Context c;
    ArrayList<String> originalnaLista;
    ArrayList<String> filtriranaLista;
    ItemFilter iFilter;

    public CustomAdapter(Context c, ArrayList<String> arr){
        this.c=c;
        this.originalnaLista=arr;
        this.filtriranaLista=arr;
    }

    @Override
    public Object getItem(int i){
        return originalnaLista.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        LayoutInflater lInflater=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View red=lInflater.inflate(R.layout.element_liste,null);

        TextView et=(TextView) red.findViewById(R.id.editText);

        et.setText(originalnaLista.get(i));


        return red;
    }

    @Override
    public int getCount(){
        return originalnaLista.size();
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public Filter getFilter() {
        if(iFilter==null){
            iFilter=new ItemFilter();
        }
        return iFilter;
    }

    private class ItemFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults rezultati=new FilterResults();

            if(charSequence!=null && charSequence.length()>0) {
                charSequence = charSequence.toString().toUpperCase();


                ArrayList<String> filteri = new ArrayList<>();
                for (int i = 0; i < filtriranaLista.size(); i++) {
                    if (filtriranaLista.get(i).toUpperCase().contains(charSequence)) {
                        filteri.add(filtriranaLista.get(i));
                    }
                }
                rezultati.count=filteri.size();
                rezultati.values=filteri;
            }
            else{
                rezultati.count=filtriranaLista.size();
                rezultati.values=filtriranaLista;
            }


            return rezultati;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            originalnaLista=(ArrayList<String>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
