package ee.ajapaik.android.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.RotateDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.touch.TouchImageView;

import ee.ajapaik.android.CameraActivity;
import ee.ajapaik.android.ConfirmActivity;
import ee.ajapaik.android.R;
import ee.ajapaik.android.camera.CameraPreview;
import ee.ajapaik.android.loader.BitmapLoader;

public class CameraFragment extends Fragment implements Camera.ShutterCallback, Camera.PictureCallback, LoaderCallbacks<Bitmap> {
	public static String TAG = "CameraFragment";
	private Camera camera;
	private FrameLayout previewSurfaceContainer;
	private SimpleDateFormat format;
	
	private boolean containerLand = true;
	private boolean rotated = false;
	private ImageFetcher imageFetcher;

	public static Fragment newInstance() {
		return new CameraFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		format = new SimpleDateFormat();
		format.applyPattern("ddMM_kkmmss");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.camera, null);
		previewSurfaceContainer = (FrameLayout) v.findViewById(R.id.cameraPreview);
		v.findViewById(R.id.photoButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (camera == null) {
					return;
				}
				camera.takePicture(CameraFragment.this, null, CameraFragment.this);
			}
		});
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		camera = Camera.open();
		if (camera == null) {
			return;
		}
		Camera.Parameters params = camera.getParameters();
		params.setJpegQuality(90);
		Log.d(TAG, "imageFormat=" + params.getPictureFormat());
		for (Camera.Size sz : params.getSupportedPictureSizes()) {
			Camera.Size currentSz = params.getPictureSize();
			float ratio = (float)(currentSz.width) / (float)(currentSz.height);
			if (sz.height * sz.width > currentSz.height * currentSz.width) {
				if (Math.abs(ratio - 1.333f) < 0.01f || Math.abs(ratio - 0.75f) < 0.01f) {
					params.setPictureSize(sz.width, sz.height);
				}
			}
		}
		Log.d(TAG, "Settled for " + params.getPictureSize().width + "x" + params.getPictureSize().height);
		camera.setParameters(params);
		setCameraDisplayOrientation(getActivity(), 0, camera);
		CameraPreview prev = new CameraPreview(getActivity(), camera);
		
//		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

		TouchImageView tiv = (TouchImageView) getView().findViewById(R.id.tiv);
		
//		System.out.println("rotation=" + rotation + " width=" + width + " height=" + height);
		if (width < height) {
			// port
			prev.setLayoutParams(new FrameLayout.LayoutParams(width, (int)(1.333f * width)));
			tiv.setLayoutParams(new FrameLayout.LayoutParams(width, (int)(1.333f * width)));
			imageFetcher = new ImageFetcher(getActivity(), width, (int)(1.333f * width), true);
		} else {
			// land
			prev.setLayoutParams(new FrameLayout.LayoutParams((int)(1.333f * height), height));
			tiv.setLayoutParams(new FrameLayout.LayoutParams((int)(1.333f * height), height));
			imageFetcher = new ImageFetcher(getActivity(), (int)(1.333f * height), height, true);
		}
		
		final int id = getActivity().getIntent().getIntExtra(CameraActivity.EXTRA_ID, -1);
		getLoaderManager().initLoader(0, BitmapLoader.args(id, width > height), this);
		
		containerLand = width > height;
		
//		final int id = getActivity().getIntent().getIntExtra(CameraActivity.EXTRA_ID, -1);
		
//		imageFetcher.loadImage(String.format("http://www.ajapaik.ee/foto_url/%d/", id), tiv);
//		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
//		if (bmp.getWidth() < bmp.getHeight()) {
//			// image port
//			if (width > height) {
//				// container land, rotate
//				bmp = rotate(bmp, 90.0f);
//			}
//		} else {
//			// image land
//			if (width < height) {
//				// container port, rotate
//				bmp = rotate(bmp, 90.0f);
//			}
//		}
//		tiv.setImageBitmap(bmp);
		
		previewSurfaceContainer.addView(prev);
	}

	private static Bitmap rotate(Bitmap bmp, float f) {
		Matrix mat = new Matrix();
		mat.preRotate(90.0f);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, false);
//		if (bmp != newbmp) {
//			bmp.recycle();
//			bmp = newbmp;
//		}
//		return bmp;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (camera == null) {
			return;
		}
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
		File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "ajapaik");
		if (f.exists()) {
			Log.w(TAG, "Going to overwrite picture at " + filename);
		}
		f.mkdirs();
		FileOutputStream fos = null;
		String filePath = f.getAbsolutePath() + File.separator + filename + ".jpg";
		try {
			fos = new FileOutputStream(filePath);
			fos.write(data);
			Log.d(TAG, "Image written to " + filePath);
			
			if (!rotated) {
				// we need to rotate the port pics
				Bitmap bmp = BitmapFactory.decodeFile(filePath);
				bmp = rotate(bmp, 90.0f);
				fos = new FileOutputStream(filePath);
				bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				bmp.recycle();
			}
		} catch (Exception e) {
			Log.w(TAG, "Saving file failed :(");
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {}
			}
		}
		ConfirmActivity.start(getActivity(), getActivity().getIntent().getIntExtra(CameraActivity.EXTRA_ID, -1), filePath);
	}

	@Override
	public void onShutter() {
		// make the shutter sound
	}

	@Override
	public Loader<Bitmap> onCreateLoader(int arg0, Bundle arg1) {
		return BitmapLoader.newInstance(getActivity(), arg1);
	}

	@Override
	public void onLoadFinished(Loader<Bitmap> arg0, Bitmap bmp) {
		rotated = (containerLand ^ (bmp.getHeight() < bmp.getWidth()));
		if (rotated) {
			bmp = rotate(bmp, 90.0f);
		}
		TouchImageView tiv = (TouchImageView) getView().findViewById(R.id.tiv);
		if (tiv != null) {
			tiv.setImageBitmap(bmp);
		}
		ImageView photoButton = (ImageView) getView().findViewById(R.id.photoButton);
		photoButton.setImageResource(rotated ? R.drawable.btn_camera_rotated : R.drawable.btn_camera);
	}

	@Override
	public void onLoaderReset(Loader<Bitmap> arg0) {
		// TODO Auto-generated method stub
		
	}

}
