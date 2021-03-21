package ba.unsa.etf.rma.damir.spirala1;

import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by cico on 5/25/2018.
 */

public class KategorijaCursorAdapter extends ResourceCursorAdapter {

    public KategorijaCursorAdapter ( Context context , int layout , Cursor c , int flags ) {
        super ( context , layout , c , flags );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        /*TextView naziv = (TextView) view.findViewById(R.id.eNaziv);
        TextView autor = (TextView) view.findViewById(R.id.eAutor);

        ImageView naslovna = (ImageView) view.findViewById(R.id.eNaslovna);

        //naziv.setText(knjigePoKategoriji.get(i).getNazivKnjige());
        //autor.setText(knjigePoKategoriji.get(i).getImeAutora());

        ArrayList<Knjiga> knjigePoKategoriji=new ArrayList<>();

        if(knjigePoKategoriji.get(i).getSlika()!=null){
            //ucitajSlikuIzURL(knjigePoKategoriji.get(i).getSlika().toString());
            Picasso.with(KategorijeAkt.kontekst).load(knjigePoKategoriji.get(i).getSlika().toString()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(naslovna,new com.squareup.picasso.Callback(){

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
        }
        else{
            naslovna.setImageBitmap(knjigePoKategoriji.get(i).getNaslovnaStrana());
        }

        TextView datumObjavljivanja=(TextView) view.findViewById(R.id.eDatumObjavljivanja);
        TextView opis=(TextView) view.findViewById(R.id.eOpis);
        TextView brojStranica=(TextView) view.findViewById(R.id.eBrojStranica);

        datumObjavljivanja.setText(knjigePoKategoriji.get(i).getDatumObjavljivanja());
        opis.setText(knjigePoKategoriji.get(i).getOpis());
        brojStranica.setText(String.valueOf(knjigePoKategoriji.get(i).getBrojStranica()));

        if (knjigePoKategoriji.get(i).getObojeno()) {
            view.setBackgroundResource(R.color.svijetloPlava);
        }

        final int j=i;

        Button dPreporuci=(Button) v.findViewById(R.id.dPreporuci);
        dPreporuci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("idKnjige", knjigePoKategoriji.get(j).getId());

                FragmentManager fm = getFragmentManager();
                FragmentPreporuci nextFrag = new FragmentPreporuci();
                nextFrag.setArguments(bundle);
                if(KategorijeAkt.siriL) {
                    getActivity().findViewById(R.id.mjestoF1).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.mjestoF2).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.mjestoF3).setVisibility(View.VISIBLE);
                    fm.beginTransaction().replace(R.id.mjestoF3, nextFrag).commit();
                }
                else fm.beginTransaction().replace(R.id.mjestoF1,nextFrag).commit();


            }
        });
        return v;*/
    }
}
