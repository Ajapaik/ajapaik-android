package ee.ajapaik.android.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import ee.ajapaik.android.R;
import ee.ajapaik.android.map.PhotoItem;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class PhotoListLoader extends CachingAsyncLoader<PhotoItem[]> {
	
	private static final String TAG = "PHOTOLOADER";
	
	private Drawable dRephotoed, dNotRephotoed;

	public PhotoListLoader(Context context) {
		super(context);
		dRephotoed = context.getResources().getDrawable(R.drawable.icon_camera_hot);
		dNotRephotoed = context.getResources().getDrawable(R.drawable.icon_camera);
	}

	@Override
	public PhotoItem[] loadInBackground() {
		InputStream is = null;
		try {
			is = (new URL("http://www.ajapaik.ee/kaart/?city=2")).openStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			String s = reader.readLine();
			while (s != null) {
				if (s.contains("geotagged_photos=")) {
					int start = s.indexOf('=') + 1;
					int end = s.lastIndexOf(';') + 1;
					JSONArray arr = new JSONArray(s.substring(start, end));
					PhotoItem[] ret = new PhotoItem[arr.length()];
					for (int i = 0; i < ret.length; i++) {
						JSONArray photoArr = arr.getJSONArray(i);
						int id = photoArr.getInt(0);
						double longitude = photoArr.getDouble(1);
						double latitude = photoArr.getDouble(2);
						boolean rephotoed = photoArr.getBoolean(3);
						ret[i] = new PhotoItem(latitude, longitude, id, rephotoed ? dRephotoed : dNotRephotoed);
					}
					return ret;
				}
				s = reader.readLine();
			}
		} catch (IOException ioe) {
			Log.w(TAG, "blerg", ioe);
		} catch (JSONException e) {
			Log.w(TAG, "blerg", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {}
			}
		}
		return new PhotoItem[0];
	}

}
