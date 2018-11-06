package cs371m.bowen.somepawtytolove;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.Random;

/**
 * Created by witchel on 1/15/17.
 * Taken from Homework 5: RedFetch.
 */

public class Net {
    private RequestQueue mRequestQueue;
    private Context context;
    private RequestOptions glideOptions;
    private Random random;

    private static class NetHolder {
        public static Net helper = new Net();
    }

    public static Net getInstance() {
        return NetHolder.helper;
    }

    public static synchronized void init(Context _context) {
        // XXX You will die here if you do not call Net.init(...) in your MainActivity
        NetHolder.helper.context = _context;
        NetHolder.helper.mRequestQueue = Volley.newRequestQueue(_context.getApplicationContext());
        NetHolder.helper.glideOptions = new RequestOptions()
                // Options like CenterCrop are possible, but I like this one best
                .fitCenter()
                // A placeholder image for when the network is slow
                .placeholder(R.drawable.ic_cloud_download_black_24dp)
                // Rounded corners are so lovely.
                .transform(
                        new RoundedCorners(20))
                // If we can't fetch, give the user an indication  maybe it should
                // say "network error"
                .error(new ColorDrawable(Color.RED));
        NetHolder.helper.random = new Random();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }
    void glideFetch(String urlString, ImageView imageView) {
        GlideApp.with(context)
                .asBitmap()
                .load(urlString)
                .apply(glideOptions)
                .into(imageView);
    }
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
