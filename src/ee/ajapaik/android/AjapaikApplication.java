package ee.ajapaik.android;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class AjapaikApplication extends Application implements LocationListener {
	
	// reference-counted gpslock. I am lazy.
	// no need for volatile, increase/decrease will be called on UI thread anyway
	private static int gpsClients = 0;
	private static AjapaikApplication self = null;
	private static Handler uiHandler = null;
	public static Location loc = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		self = this;
		uiHandler = new Handler(Looper.getMainLooper());
	}
	
	
	public static void increaseGpsRefCount(Context context) {
		if (gpsClients == 0) {
			LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			lm.requestLocationUpdates(1000L, 3.0f, crit, self, Looper.getMainLooper());
		}
		gpsClients++;
	}
	
	public static void decreaseGpsRefCount(final Context context) {
		final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		uiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				gpsClients--;
				if (gpsClients == 0) {
					lm.removeUpdates(self);
				}
			}
		}, 500L); // give it 500ms to start another activity
	}


	@Override
	public void onLocationChanged(Location location) {
		loc = location;
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	@Override
	public void onProviderEnabled(String provider) {}
	
	@Override
	public void onProviderDisabled(String provider) {}
}
