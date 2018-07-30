package fr.coding.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import fr.coding.yourandroidwebapp.R;

public class DiskCacheImageViewUrl extends AsyncTask<String, Void, Bitmap> {
    RetrieveHttpFile filer;
    ImageView bmImage;
    Context context;
    Bitmap basebm;

    public DiskCacheImageViewUrl(Context lcontext, ImageView bmImage) {
        this.bmImage = bmImage;
        this.context = lcontext;
        this.basebm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    protected Bitmap doInBackground(String... urls) {
       Bitmap mIcon11 = null;
        try {

            byte[] encoded_name = Base64.encode(urls[0].getBytes(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            String key_name = new String(encoded_name);

            File cache_file = new File(context.getCacheDir(), key_name);
            if (cache_file.exists()&&cache_file.length()>0) {
                mIcon11 = BitmapFactory.decodeStream(new FileInputStream(cache_file));
                return mIcon11;
            }

            filer = new RetrieveHttpFile();
            byte[] bytes = filer.doInBackground(urls);

            Bitmap scaledBitmap = basebm;
            if (bytes.length > 0)
                scaledBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), 384, 384, true);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cache_file));
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bos.flush();
            bos.close();
            Log.i("DiskCacheImageViewUrl",urls[0]+ " added to disk cache");

            mIcon11 = scaledBitmap;

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
