package ee.ajapaik.android;

import ee.ajapaik.android.R;
import ee.ajapaik.android.fragment.MapFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.mainContainer, MapFragment.newInstance(), MapFragment.TAG);
			ft.commit();
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
