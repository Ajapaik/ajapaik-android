package ee.ajapaik.android;

import ee.ajapaik.android.fragment.MainFragment;

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
			ft.add(R.id.mainContainer, MainFragment.newInstance(), MainFragment.TAG);
			ft.commit();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AjapaikApplication.increaseGpsRefCount(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		AjapaikApplication.decreaseGpsRefCount(this);
	}
}
