package ee.ajapaik.android;

import ee.ajapaik.android.fragment.ConfirmFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class ConfirmActivity extends FragmentActivity {
	public final static String EXTRA_ID = "ee.ajapaik.android.extra.ID";
	public final static String EXTRA_FILE = "ee.ajapaik.android.extra.FILE";
	public static final String EXTRA_SCALE_FACTOR = "ee.ajapaik.android.extra.SCALE_FACTOR";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.drawable.bg);
		setContentView(R.layout.main);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.mainContainer, ConfirmFragment.newInstance(), ConfirmFragment.TAG);
			ft.commit();
		}
	}

	public static void start(Context context, int id, String filePath, float scaleFactor) {
		Intent i = new Intent(context, ConfirmActivity.class);
		i.putExtra(EXTRA_ID, id);
		i.putExtra(EXTRA_FILE, filePath);
		i.putExtra(EXTRA_SCALE_FACTOR, scaleFactor);
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
