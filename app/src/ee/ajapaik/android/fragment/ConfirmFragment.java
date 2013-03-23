package ee.ajapaik.android.fragment;

import java.io.File;
import java.util.Date;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.facebook.Session;
import ee.ajapaik.android.Constants;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import ee.ajapaik.android.external.bitmaputil.ImageFetcher;

import ee.ajapaik.android.AjapaikApplication;
import ee.ajapaik.android.ConfirmActivity;
import ee.ajapaik.android.DetailsActivity;
import ee.ajapaik.android.MainActivity;
import ee.ajapaik.android.R;
import ee.ajapaik.android.network.ProgressMultipartEntity;
import ee.ajapaik.android.network.ProgressMultipartEntity.Listener;

public class ConfirmFragment extends Fragment {
	
	private int id;
	private String filePath;

	public static final String TAG = "ConfirmFragment";
	private ImageFetcher imageFetcher;

	public static Fragment newInstance() {
		return new ConfirmFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		Log.d(TAG, "ID=" + getActivity().getIntent().getIntExtra(ConfirmActivity.EXTRA_ID, -1));
		Log.d(TAG, "File=" + getActivity().getIntent().getStringExtra(ConfirmActivity.EXTRA_FILE));
		Log.d(TAG, "SF=" + getActivity().getIntent().getFloatExtra(ConfirmActivity.EXTRA_SCALE_FACTOR, 1.0f));
		
		int w = getResources().getDisplayMetrics().widthPixels;
		int h = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240.0f, getResources().getDisplayMetrics());
		imageFetcher = new ImageFetcher(getActivity(), w, h, false);
		
		id = getActivity().getIntent().getIntExtra(DetailsActivity.EXTRA_ID, -1);
		filePath = getActivity().getIntent().getStringExtra(ConfirmActivity.EXTRA_FILE);
		
		View v = inflater.inflate(R.layout.confirm, null);
		
		imageFetcher.loadImage(String.format("http://%s/foto_url/%d/", Constants.BACKEND_DOMAIN, id), (ImageView) v.findViewById(R.id.oldPic));
		
		// grab the file. ui thread, don't care.
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 4;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);
		
		((ImageView) v.findViewById(R.id.newPic)).setImageBitmap(bmp);
		
		v.findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO show diag
				((ConfirmActivity) getActivity()).fbLogin();
			}
		});
		
		return v;
	}

	public void onFbLoginComplete() {
		Uploader uploader = new Uploader();
		uploader.execute();
	}
	
	private class Uploader extends AsyncTask<Void, Long, Void> implements Listener {
		ProgressDialogFragment frag = null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			frag = new ProgressDialogFragment((int)(new File(filePath).length()));
			frag.show(getFragmentManager(), "ProgressDialogFragment");
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			frag.dismissAllowingStateLoss();
			frag = null;
			Toast.makeText(getActivity(), "Ülepildistus saadetud", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getActivity(), MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		
		@Override
		protected void onProgressUpdate(Long... values) {
			super.onProgressUpdate(values);
			frag.getDialog().setProgress(values[0].intValue());
		}
		
		@Override
		protected Void doInBackground(Void... nothing) {
			try {
				HttpParams params = new BasicHttpParams();
				params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				DefaultHttpClient httpClient = new DefaultHttpClient(params);
				HttpPost post = new HttpPost(String.format("http://%s/foto/%d/upload/", Constants.BACKEND_DOMAIN, id));
				
				MultipartEntity entity = new ProgressMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, this);
				
				Date date = new Date();
				
				entity.addPart("user_file[]", new FileBody(new File(filePath)));
				entity.addPart("year", new StringBody(String.valueOf(date.getYear())));
				entity.addPart("month", new StringBody(String.valueOf(date.getMonth())));
				entity.addPart("day", new StringBody(String.valueOf(date.getDay())));

				float sf = getActivity().getIntent().getFloatExtra(ConfirmActivity.EXTRA_SCALE_FACTOR, 1.0f);
				entity.addPart("scale_factor", new StringBody(String.valueOf(sf)));
				Log.d(TAG, "scale factor:" + sf);
				
				// lets hope its recent enough?
				Location loc = AjapaikApplication.loc;
				if (loc != null && System.currentTimeMillis() - loc.getTime() < 30000L) {
					entity.addPart("lat", new StringBody(String.valueOf(loc.getLatitude())));
					entity.addPart("lon", new StringBody(String.valueOf(loc.getLongitude())));
				}

				float[] orientation = AjapaikApplication.getOrientation();
				if (orientation != null) {
					entity.addPart("yaw", new StringBody(String.valueOf(orientation[0])));
					entity.addPart("pitch", new StringBody(String.valueOf(orientation[1])));
					entity.addPart("roll", new StringBody(String.valueOf(orientation[2])));
					Log.d(TAG, "yaw: " + orientation[0]);
					Log.d(TAG, "pitch: " + orientation[1]);
					Log.d(TAG, "roll: " + orientation[2]);
				}

				Session sess = Session.getActiveSession();
				if (sess != null && sess.isOpened()) {
					entity.addPart("fb_access_token", new StringBody(sess.getAccessToken()));
					entity.addPart("fb_application_id", new StringBody(sess.getApplicationId()));
				}

				post.setEntity(entity);
				httpClient.execute(post);
			} catch (Exception e) {
				Log.e(TAG, "Upload failed", e);
			}
			return null;
		}

		@Override
		public void onProgress(long progress) {
			publishProgress(progress);
		}
	}
}
