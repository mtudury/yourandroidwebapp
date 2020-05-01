package fr.coding.tools;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class RetrieveHttpFile extends AsyncTask<String, Void, byte[]> {

    private Exception exception;

    protected byte[] doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int r = in.read(buffer);
                if (r == -1) break;
                out.write(buffer, 0, r);
            }
            in.close();

            return out.toByteArray();
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(byte[] feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
