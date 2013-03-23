package ee.ajapaik.android.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import ee.ajapaik.android.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ee.ajapaik.android.R;
import android.content.Context;
import android.util.Log;

public class PhotoListLoader extends CachingAsyncLoader<MarkerOptions[]> {
	
	private static final String TAG = "PHOTOLOADER";
	public static final String ARG_LATITUDE = "latitude";
	public static final String ARG_LONGITUDE = "longitude";

	private final double latitude, longitude;
	private BitmapDescriptor dRephotoed, dNotRephotoed;

	public PhotoListLoader(Context context, double latitude, double longitude) {
		super(context);
		this.latitude = latitude;
		this.longitude = longitude;
		dRephotoed = BitmapDescriptorFactory.fromResource(R.drawable.icon_camera_hot);
		dNotRephotoed = BitmapDescriptorFactory.fromResource(R.drawable.icon_camera);
	}

	@Override
	public MarkerOptions[] loadInBackground() {
		InputStream is = null;
		try {
			URL url = new URL(String.format("http://%s/api-v1.php?action=photo&latitude=%.6f&longitude=%.6f", Constants.API_HOST, latitude, longitude));
			Log.d(TAG, "Firing request to " + url);
			is = url.openStream();
			is = new BufferedInputStream(is, 8096);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			StringBuilder sb = new StringBuilder();
			
			char[] buf = new char[4096];
			int read = 0;
			while ((read = isr.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}
			JSONObject result = new JSONObject(sb.toString());
			JSONArray items = result.getJSONArray("result");
			MarkerOptions[] ret = new MarkerOptions[items.length()];
			for (int i = 0; i < items.length(); i++) {
				JSONObject obj = items.getJSONObject(i);
				int id = Integer.parseInt(obj.optString("id"));
				double longitude = Double.parseDouble(obj.optString("lon"));
				double latitude = Double.parseDouble(obj.optString("lat"));
				String description = obj.optString("description");
				boolean rephotoed = Integer.valueOf(obj.optString("rephoto_count")) != 0;
				ret[i] = new MarkerOptions()
					.icon(rephotoed ? dRephotoed : dNotRephotoed)
					.position(new LatLng(latitude, longitude))
					.title(description).snippet(String.valueOf(id)); // oh man, a hack
			}
			return ret;
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
		return new MarkerOptions[0];
	}

}
