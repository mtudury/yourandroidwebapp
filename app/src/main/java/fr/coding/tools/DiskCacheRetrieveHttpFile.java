package fr.coding.tools;

import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DiskCacheRetrieveHttpFile extends RetrieveHttpFile {
    protected Exception exceptioncache;

    protected byte[] doInBackground(String... urls) {
        try {
            File CacheSubDir = new File("ImgCache");
            CacheSubDir.mkdir();

            byte[] encoded_name = Base64.encode(urls[0].getBytes(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            String key_name = new String(encoded_name);

            File cache_file = new File(key_name, CacheSubDir.getAbsolutePath());
            if (cache_file.exists()&&cache_file.length()>0) {
                InputStream in = new BufferedInputStream(new FileInputStream(cache_file));
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int r = in.read(buffer);
                    if (r == -1) break;
                    out.write(buffer, 0, r);
                }
                in.close();

                return out.toByteArray();
            }

            byte[] bytes = super.doInBackground(urls);
            if (bytes.length>0) {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cache_file));
                bos.write(bytes);
                bos.flush();
                bos.close();
            }

            return bytes;

        } catch (Exception e) {
            this.exceptioncache = e;
            return null;
        }
    }

    protected void onPostExecute(byte[] feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
