package ba.unsa.etf.rma.damir.spirala1;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by cico on 5/18/2018.
 */

public class KnjigePoznanika extends IntentService {

    public static int STATUS_START = 0;
    public static int STATUS_FINISH = 1;
    public static int STATUS_ERROR = 2;


    ArrayList<String> listaId=new ArrayList<String>();

    public KnjigePoznanika() {
        super(null);
    }

    public KnjigePoznanika(String autor) {
        super(autor);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }



    ArrayList<Knjiga> results = new ArrayList<Knjiga>();


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        final ResultReceiver reciever = intent.getParcelableExtra("receiver");
        final String idKorisnika = intent.getStringExtra("naziv");
        Bundle bundle = new Bundle();
        try {
            reciever.send(STATUS_START, Bundle.EMPTY);

            String url1 = "https://www.googleapis.com/books/v1/users/" + idKorisnika + "/bookshelves";
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConection.getInputStream());
                String rezultat = convertStreamToString(in);
                JSONObject jo = new JSONObject(rezultat);
                if (jo.has("items")) {
                    JSONArray items = jo.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject knjige = items.getJSONObject(i);

                        if (knjige.has("id")) {
                            listaId.add(knjige.getString("id"));
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int k = 0; k < listaId.size(); k++) {
                url1 = "https://www.googleapis.com/books/v1/users/" + idKorisnika + "/bookshelves/" + listaId.get(k) + "/volumes";


                try {
                    URL url = new URL(url1);
                    HttpURLConnection urlConection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConection.getInputStream());
                    String rezultat = convertStreamToString(in);
                    JSONObject jo = new JSONObject(rezultat);
                    if (jo.has("items")) {
                        JSONArray items = jo.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject knjige = items.getJSONObject(i);
                            String id = "", title = "", opis = "", publishedDate = "";
                            int pageCount = 0;
                            ArrayList<Autor> authors = new ArrayList<Autor>();
                            URL image = new URL("http://books.google.com/books/content?id=XguUtECOsM0C&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api");
                            if (knjige.has("id")) {
                                id = knjige.getString("id");
                            }
                            JSONObject volumeInfo = knjige.getJSONObject("volumeInfo");

                            if (volumeInfo.has("title")) {
                                title = volumeInfo.getString("title");
                            }
                            if (volumeInfo.has("authors")) {
                                JSONArray autori = volumeInfo.getJSONArray("authors");
                                for (int j = 0; j < autori.length(); j++)
                                    authors.add(new Autor(autori.getString(j), ""));
                            }
                            if (volumeInfo.has("description")) {
                                opis = volumeInfo.getString("description");
                            }
                            if (volumeInfo.has("publishedDate")) {
                                publishedDate = volumeInfo.getString("publishedDate");
                            }
                            if (volumeInfo.has("imageLinks")) {
                                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                                if (imageLinks.has("thumbnail")) {
                                    image = new URL(imageLinks.getString("thumbnail"));
                                }
                            }
                            if (volumeInfo.has("pageCount")) {
                                pageCount = Integer.parseInt(volumeInfo.getString("pageCount"));
                            }
                            results.add(new Knjiga(id, title, authors, opis, publishedDate, image, pageCount));
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            bundle.putParcelableArrayList("result", results);
            reciever.send(1, bundle);
        } catch (Exception e) {
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            reciever.send(STATUS_ERROR, bundle);
        }
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
            }
        }
        return sb.toString();
    }
}