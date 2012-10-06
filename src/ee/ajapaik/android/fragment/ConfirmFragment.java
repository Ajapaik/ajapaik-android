package ee.ajapaik.android.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ee.ajapaik.android.ConfirmActivity;
import ee.ajapaik.android.R;

public class ConfirmFragment extends Fragment {

	public static final String TAG = "ConfirmFragment";

	public static Fragment newInstance() {
		return new ConfirmFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		Log.d(TAG, "ID=" + getActivity().getIntent().getIntExtra(ConfirmActivity.EXTRA_ID, -1));
		Log.d(TAG, "File=" + getActivity().getIntent().getStringExtra(ConfirmActivity.EXTRA_FILE));
		
		View v = inflater.inflate(R.layout.confirm, null);
		
		// grab the file. ui thread, don't care.
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		Bitmap bmp = BitmapFactory.decodeFile(getActivity().getIntent().getStringExtra(ConfirmActivity.EXTRA_FILE), opts);
		
		((ImageView) v.findViewById(R.id.newPic)).setImageBitmap(bmp);
		
		return v;
	}

}
