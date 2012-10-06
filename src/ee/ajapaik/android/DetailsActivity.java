package ee.ajapaik.android;

import ee.ajapaik.android.fragment.PhotoDetailsFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class DetailsActivity extends FragmentActivity {
	public final static String EXTRA_ID = "ee.ajapaik.android.extra.ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.mainContainer, PhotoDetailsFragment.newInstance(), PhotoDetailsFragment.TAG);
			ft.commit();
		}
	}
	
	public static void start(Context context, int id) {
		Intent i = new Intent(context, DetailsActivity.class);
		i.putExtra(EXTRA_ID, id);
		context.startActivity(i);
	}
}
