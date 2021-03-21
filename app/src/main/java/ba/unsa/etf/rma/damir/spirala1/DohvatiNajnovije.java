package ba.unsa.etf.rma.damir.spirala1;

import android.os.AsyncTask;

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
 * Created by cico on 5/16/2018.
 */

public class DohvatiNajnovije extends AsyncTask<String,Integer,Void> {

    @Override
    protected Void doInBackground(String... strings) {
        String query = null;
        try {
            query = URLEncoder.encode(strings[0], "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url1 = "https://www.googleapis.com/books/v1/volumes?q=inauthor:" + query + "&orderBy=newest&maxResults=5";
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
                    rez.add(new Knjiga(id, title, authors, opis, publishedDate, image, pageCount));
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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

    public interface IDohvatiNajnovijeDone {
        public void onNajnovijeDone(ArrayList<Knjiga> rez);
    }

    ArrayList<Knjiga> rez = new ArrayList<Knjiga>();
    private IDohvatiNajnovijeDone pozivatelj;

    public DohvatiNajnovije(IDohvatiNajnovijeDone p) {
        pozivatelj = p;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pozivatelj.onNajnovijeDone(rez);
    }
}
