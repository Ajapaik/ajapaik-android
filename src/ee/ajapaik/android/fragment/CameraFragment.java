package ee.ajapaik.android.fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.touch.TouchImageView;

import ee.ajapaik.android.R;
import ee.ajapaik.android.camera.CameraPreview;

public class CameraFragment extends Fragment implements Camera.ShutterCallback, Camera.PictureCallback {

	public static String TAG = "CameraFragment";
	private Camera camera;
	private FrameLayout previewSurfaceContainer;
	private SimpleDateFormat format;

	public static Fragment newInstance() {
		return new CameraFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		format = new SimpleDateFormat();
		format.applyPattern("dM_kms");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.camera, null);
		previewSurfaceContainer = (FrameLayout) v.findViewById(R.id.cameraPreview);
		v.findViewById(R.id.photoButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				camera.takePicture(CameraFragment.this, null, CameraFragment.this);
			}
		});
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

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		String filename = format.format(new Date());
		File f = new File(Environment.DIRECTORY_PICTURES + File.separator + "ajapaik");
		if (f.exists()) {
			Log.w(TAG, "Going to overwrite picture at " + filename);
		}
		f.mkdirs();
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(f.getAbsolutePath() + File.separator + filename + ".jpg");
			bos = new BufferedOutputStream(fos, 8096); // 8K buffer
			
			int totalWritten = 0;
			int written = 0;
			while (bos.write(data))
		} catch (Exception e) {
			Log.w(TAG, "Saving file failed :(");
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {}
				try {
					bos.close();
				} catch (IOException ioe) {}
				}
			}
		}
	}

	@Override
	public void onShutter() {
		// make the shutter sound
	}

}
