package ee.ajapaik.android.fragment;

import com.example.android.bitmapfun.util.ImageFetcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ee.ajapaik.android.ConfirmActivity;
import ee.ajapaik.android.DetailsActivity;
import ee.ajapaik.android.R;

public class ConfirmFragment extends Fragment {

	public static final String TAG = "ConfirmFragment";
	private ImageFetcher imageFetcher;

	public static Fragment newInstance() {
		return new ConfirmFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		Log.d(TAG, "ID=" + getActivity().getIntent().getIntExtra(ConfirmActivity.EXTRA_ID, -1));
		Log.d(TAG, "File=" + getActivity().getIntent().getStringExtra(ConfirmActivity.EXTRA_FILE));
		
		int w = getResources().getDisplayMetrics().widthPixels;
		int h = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240.0f, getResources().getDisplayMetrics());
		imageFetcher = new ImageFetcher(getActivity(), w, h, false);
		
		final int id = getActivity().getIntent().getIntExtra(DetailsActivity.EXTRA_ID, -1);
		
		View v = inflater.inflate(R.layout.confirm, null);
		
		imageFetcher.loadImage(String.format("http://www.ajapaik.ee/foto_url/%d/", id), (ImageView) v.findViewById(R.id.oldPic));
		
		// grab the file. ui thread, don't care.
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		Bitmap bmp = BitmapFactory.decodeFile(getActivity().getIntent().getStringExtra(ConfirmActivity.EXTRA_FILE), opts);
		
		((ImageView) v.findViewById(R.id.newPic)).setImageBitmap(bmp);
		
		return v;
	}

}
