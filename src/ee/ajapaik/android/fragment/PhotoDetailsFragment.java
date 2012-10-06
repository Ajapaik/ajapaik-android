package ee.ajapaik.android.fragment;

import ee.ajapaik.android.CameraActivity;
import ee.ajapaik.android.DetailsActivity;
import ee.ajapaik.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class PhotoDetailsFragment extends Fragment {
	
	public static final String TAG = "PhotoDetailsFragment";

	public static Fragment newInstance() {
		return new PhotoDetailsFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.details, null);
		final int id = getActivity().getIntent().getIntExtra(DetailsActivity.EXTRA_ID, -1);
		v.findViewById(R.id.btnRephoto).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraActivity.start(getActivity(), id);
			}
		});
		return v;
	}
}
