package ee.ajapaik.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import ee.ajapaik.android.fragment.CameraFragment;

public class CameraActivity extends FragmentActivity {
	public final static String EXTRA_ID = "ee.ajapaik.android.extra.ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawable(new ColorDrawable(0xff000000));
		setContentView(R.layout.main_noactionbar);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.mainContainer, CameraFragment.newInstance(), CameraFragment.TAG);
			ft.commit();
		}
	}

	public static void start(Context context, int id) {
		Intent i = new Intent(context, CameraActivity.class);
		i.putExtra(EXTRA_ID, id);
		context.startActivity(i);
	}
}
