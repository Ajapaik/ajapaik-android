package ee.ajapaik.android.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.touch.TouchImageView;

import ee.ajapaik.android.R;
import ee.ajapaik.android.camera.CameraPreview;

public class CameraFragment extends Fragment {

	public static String TAG = "CameraFragment";
	private Camera camera;
	private FrameLayout previewSurfaceContainer;

	public static Fragment newInstance() {
		return new CameraFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.camera, null);
		previewSurfaceContainer = (FrameLayout) v.findViewById(R.id.cameraPreview);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		camera = Camera.open();
		setCameraDisplayOrientation(getActivity(), 0, camera);
		CameraPreview prev = new CameraPreview(getActivity(), camera);
		
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

		TouchImageView tiv = (TouchImageView) getView().findViewById(R.id.tiv);
		
//		System.out.println("rotation=" + rotation + " width=" + width + " height=" + height);
		if (width < height) {
			// port
			prev.setLayoutParams(new FrameLayout.LayoutParams(width, (int)(1.333f * width)));
			tiv.setLayoutParams(new FrameLayout.LayoutParams(width, (int)(1.333f * width)));
		} else {
			// land
			prev.setLayoutParams(new FrameLayout.LayoutParams((int)(1.333f * height), height));
			tiv.setLayoutParams(new FrameLayout.LayoutParams((int)(1.333f * height), height));
		}
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
		if (bmp.getWidth() < bmp.getHeight()) {
			// image port
			if (width > height) {
				// container land, rotate
				bmp = rotate(bmp, 90.0f);
			}
		} else {
			// image port
			if (width < height) {
				// container port, rotate
				bmp = rotate(bmp, 90.0f);
			}
		}
		tiv.setImageBitmap(bmp);
		
		previewSurfaceContainer.addView(prev);
	}

	private Bitmap rotate(Bitmap bmp, float f) {
		Matrix mat = new Matrix();
		mat.preRotate(90.0f);
		Bitmap newbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, false);
		if (bmp != newbmp) {
			bmp.recycle();
			bmp = newbmp;
		}
		return bmp;
	}

	@Override
	public void onPause() {
		super.onPause();
		camera.stopPreview();
		previewSurfaceContainer.removeAllViews();
		camera.release();
	}

	public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

}
