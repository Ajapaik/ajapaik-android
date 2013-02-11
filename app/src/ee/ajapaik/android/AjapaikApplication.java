package ee.ajapaik.android;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.crittercism.app.Crittercism;

public class AjapaikApplication extends Application implements LocationListener, SensorEventListener {
	
	// reference-counted gpslock. I am lazy.
	// no need for volatile, increase/decrease will be called on UI thread anyway
	private static int gpsClients = 0;
	private static AjapaikApplication self = null;
	private static Handler uiHandler = null;
	public static Location loc = null;

	private float[] accel = null;
	private float[] mag = null;

	@Override
	public void onCreate() {
		super.onCreate();
		self = this;
		uiHandler = new Handler(Looper.getMainLooper());
		Crittercism.init(getApplicationContext(), getString(R.string.crittercism_id));
	}
	
	
	public static void increaseGpsRefCount(Context context) {
		if (gpsClients == 0) {
			LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			lm.requestLocationUpdates(1000L, 3.0f, crit, self, Looper.getMainLooper());
			SensorManager sm = (SensorManager) context.getSystemService(SENSOR_SERVICE);
			sm.registerListener(self, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 5000); // 5 sec update
			sm.registerListener(self, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 5000); // 5 sec update
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			mag = event.values;
			break;
		case Sensor.TYPE_ACCELEROMETER:
			accel = event.values;
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public static float[] getOrientation() {
		if (self.accel == null || self.mag == null) {
			return null;
		}

		float[] rot = new float[9];
		if (SensorManager.getRotationMatrix(rot, null, self.accel, self.mag)) {
			float[] orientation = new float[3];
			return SensorManager.getOrientation(rot, orientation);
		}
		return null;
	}
}
