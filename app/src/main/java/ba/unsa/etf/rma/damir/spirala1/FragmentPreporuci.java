package ba.unsa.etf.rma.damir.spirala1;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AtomicFile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cico on 5/23/2018.
 */

public class FragmentPreporuci extends Fragment {


    String idKnjige = "";
    ArrayList<String> imena = new ArrayList<String>();
    ArrayList<String> eMailovi = new ArrayList<String>();
    private static Context kontekst;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preporuci, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idKnjige = bundle.getString("idKnjige");
        }

        kontekst = getActivity().getApplicationContext();

        //checkAndRequestPermissions();
        dodajKontakte();

        TextView knjigaText=(TextView) getActivity().findViewById(R.id.knjigaText);
        TextView autoriText=(TextView) getActivity().findViewById(R.id.autoriText);

        Knjiga izabranaKnjiga1=null;
        for (int i = 0; i < KnjigeFragment.knjigePoKategoriji.size(); i++) {
            if (idKnjige.equals(KnjigeFragment.knjigePoKategoriji.get(i).getId())) {
                izabranaKnjiga1 = KnjigeFragment.knjigePoKategoriji.get(i);
            }
        }

        knjigaText.setText(izabranaKnjiga1.getNaziv());
        autoriText.setText(izabranaKnjiga1.getImeAutora());
        final Knjiga izabranaKnjiga=izabranaKnjiga1;

        if (!imena.isEmpty()) {
            final Spinner s = (Spinner) getView().findViewById(R.id.sKontakti);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, imena);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s.setAdapter(adapter);


            Button send = (Button) getActivity().findViewById(R.id.dPosalji);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final int pozicijaIzabranogImena = s.getSelectedItemPosition();

                    String naziv="";
                    String imeAutora="";


                    if (!eMailovi.get(pozicijaIzabranogImena).equals("")) {
                        String uriText = "mailto:" +
                                eMailovi.get(pozicijaIzabranogImena)
                                + "?subject=" +
                                Uri.encode("Android Proba") +
                                "&body=" + Uri.encode("Zdravo " +
                                imena.get(pozicijaIzabranogImena) +
                                ",\n" + "Pročitaj knjigu " +
                                izabranaKnjiga.getNaziv() + " od "
                                + izabranaKnjiga.getImeAutora() + "!");

                        Uri uri = Uri.parse(uriText);

                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                        sendIntent.setData(uri);
                        startActivity(Intent.createChooser(sendIntent, "Posalji mail"));
                    } else {
                        Toast.makeText(getActivity(), "Korisnik nema mail u kontaktima", Toast.LENGTH_SHORT);
                    }
                }
            });
        } else {
            Toast.makeText(kontekst, "Kontakti su prazni", Toast.LENGTH_SHORT).show();
        }
    }

    private void dodajKontakte() {
        imena.clear();
        eMailovi.clear();

        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            ContentResolver cr = getActivity().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (cur1.moveToNext()) {
                        String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        imena.add(name);

                        String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

                        if (email != null) {
                            eMailovi.add(email);
                        } else {
                            eMailovi.add("");
                        }
                    }
                    cur1.close();
                }
            }
        }
    }


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private  boolean checkAndRequestPermissions() {
        int permissionReadContacts = ContextCompat.checkSelfPermission(kontekst,Manifest.permission.READ_CONTACTS);
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