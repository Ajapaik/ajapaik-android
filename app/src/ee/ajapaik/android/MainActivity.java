package ee.ajapaik.android;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.facebook.Session;
import com.facebook.SessionState;
import ee.ajapaik.android.fragment.MainFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		Fragment frag = getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
		if (frag == null || !frag.isInLayout()) {
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
