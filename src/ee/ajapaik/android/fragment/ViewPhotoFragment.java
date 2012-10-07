package ee.ajapaik.android.fragment;

import com.example.touch.TouchImageView2;

import ee.ajapaik.android.R;
import ee.ajapaik.android.ViewPhotoActivity;
import ee.ajapaik.android.loader.BitmapLoader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ViewPhotoFragment extends Fragment implements LoaderCallbacks<Bitmap> {

	public static final String TAG = "ViewPhotoFragment";

	public static Fragment newInstance() {
		return new ViewPhotoFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		// FIRE UP THE LOADERS
		int id = getActivity().getIntent().getIntExtra(ViewPhotoActivity.EXTRA_ID, -1);
		getLoaderManager().initLoader(0, BitmapLoader.args(id, true), this);
		return inflater.inflate(R.layout.view_photo, null);
	}
	
	@Override
	public Loader<Bitmap> onCreateLoader(int arg0, Bundle arg1) {
		return BitmapLoader.newInstance(getActivity(), arg1);
	}

	@Override
	public void onLoadFinished(Loader<Bitmap> arg0, Bitmap arg1) {
		TouchImageView2 tiv = new TouchImageView2(getActivity());
		tiv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		tiv.setImageBitmap(arg1);
		((FrameLayout) getView().findViewById(R.id.touchViewContainer)).addView(tiv);
	}

	@Override
	public void onLoaderReset(Loader<Bitmap> arg0) {
		// TODO Auto-generated method stub
	}
}
