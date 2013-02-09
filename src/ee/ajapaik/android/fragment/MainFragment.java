package ee.ajapaik.android.fragment;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import ee.ajapaik.android.DetailsActivity;
import ee.ajapaik.android.loader.PhotoListLoader;

public class MainFragment extends SupportMapFragment implements LoaderCallbacks<MarkerOptions[]>/*, PhotoItemsOverlay.Listener */{
	public static MainFragment newInstance() {
		return new MainFragment();
	}
	
	public static final String TAG = "MainFragment";

	private GoogleMap map;

	private Location currentPhotoLoc = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, viewGroup, savedInstanceState);
		map = getMap();
		map.setMyLocationEnabled(true);

		if (savedInstanceState == null) {
			CameraUpdate[] updates = new CameraUpdate[] {
				CameraUpdateFactory.newLatLng(new LatLng(58.378195d, 26.714388d)),
				CameraUpdateFactory.zoomTo(15.0f)
			};
			for (CameraUpdate u : updates) {
				map.moveCamera(u);
			}

			LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition cameraPosition) {
					Location loc = new Location(LocationManager.GPS_PROVIDER); // gps provider doesn't mean anything
					loc.setLatitude(cameraPosition.target.latitude);
					loc.setLongitude(cameraPosition.target.longitude);
					loadPhotoList(loc);
				}
			});
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_MEDIUM);
			lm.requestSingleUpdate(crit, new LocationListener() {
				@Override
				public void onLocationChanged(Location loc) {
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
				}
				public void onStatusChanged(String provider, int status, Bundle extras) {}
				public void onProviderEnabled(String provider) {}
				public void onProviderDisabled(String provider) {}
			}, null);
		}

		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				DetailsActivity.start(getActivity(), Integer.valueOf(marker.getSnippet()), marker.getTitle());
				return true;
			}
		});

		return v;
	}

	private void loadPhotoList(final Location loc) {
		if (currentPhotoLoc == null || currentPhotoLoc.distanceTo(loc) > 150.0f) {
			currentPhotoLoc = loc;
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!loc.equals(currentPhotoLoc)) {
						return;
					}
					Log.d(TAG, "Loading photo list for location{lat=" + loc.getLatitude() + ", lng=" + loc.getLongitude());
					Bundle args = new Bundle();
					args.putDouble(PhotoListLoader.ARG_LATITUDE, loc.getLatitude());
					args.putDouble(PhotoListLoader.ARG_LONGITUDE, loc.getLongitude());
					getLoaderManager().restartLoader(0, args, MainFragment.this);
				}
			}, 1000L);
		}
	}

	@Override
	public Loader<MarkerOptions[]> onCreateLoader(int id, Bundle args) {
		return new PhotoListLoader(getActivity(), args.getDouble(PhotoListLoader.ARG_LATITUDE), args.getDouble(PhotoListLoader.ARG_LONGITUDE));
	}

	@Override
	public void onLoadFinished(Loader<MarkerOptions[]> loader, MarkerOptions[] results) {
		map.clear();
		for (MarkerOptions mo : results) {
			map.addMarker(mo);
		}
	}

	@Override
	public void onLoaderReset(Loader<MarkerOptions[]> loader) {
		// wut
	}
}
