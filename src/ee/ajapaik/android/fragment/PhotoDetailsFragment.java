package ee.ajapaik.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ee.ajapaik.android.external.bitmaputil.ImageFetcher;

import ee.ajapaik.android.CameraActivity;
import ee.ajapaik.android.DetailsActivity;
import ee.ajapaik.android.R;
import ee.ajapaik.android.ViewPhotoActivity;

public class PhotoDetailsFragment extends Fragment {
	
	public static final String TAG = "PhotoDetailsFragment";
	private ImageFetcher imageFetcher;

	public static Fragment newInstance() {
		return new PhotoDetailsFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		
		int w = getResources().getDisplayMetrics().widthPixels;
		int h = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240.0f, getResources().getDisplayMetrics());
		imageFetcher = new ImageFetcher(getActivity(), w, h, false);
		
		View v = inflater.inflate(R.layout.details, null);
		
		((TextView) v.findViewById(R.id.desc)).setText(getActivity().getIntent().getStringExtra(DetailsActivity.EXTRA_DESC));
		
		final int id = getActivity().getIntent().getIntExtra(DetailsActivity.EXTRA_ID, -1);
		
		imageFetcher.loadImage(String.format("http://www.ajapaik.ee/foto_url/%d/", id), (ImageView) v.findViewById(R.id.oldPic));
		
		v.findViewById(R.id.oldPic).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewPhotoActivity.start(getActivity(), id);
			}
		});
		
		v.findViewById(R.id.btnRephoto).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraActivity.start(getActivity(), id);
			}
		});
		return v;
	}
}
