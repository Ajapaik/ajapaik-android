package ee.ajapaik.android;

import ee.ajapaik.android.fragment.ViewPhotoFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class ViewPhotoActivity extends FragmentActivity {
	public final static String EXTRA_ID = "ee.ajapaik.android.extra.ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.drawable.bg);
		setContentView(R.layout.main);
		
		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.mainContainer, ViewPhotoFragment.newInstance(), ViewPhotoFragment.TAG);
			ft.commit();
		}
	}
	
	public static void start(Context context, int id) {
		Intent i = new Intent(context, ViewPhotoActivity.class);
		i.putExtra(EXTRA_ID, id);
		context.startActivity(i);
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
