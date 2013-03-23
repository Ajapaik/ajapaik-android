package ee.ajapaik.android.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import ee.ajapaik.android.Constants;

public class BitmapLoader extends CachingAsyncLoader<Bitmap> {
	
	private static final String ARGS_PHOTO_ID = "ee.ajapaik.android.args.PHOTO_ID";
	private static final String ARGS_CONTAINER_LAND = "ee.ajapaik.android.args.ARGS_CONTAINER_LAND";
	
	private int id = -1;
	private boolean containerLand = false;

	public static Bundle args(int id, boolean containerLand) {
		Bundle args = new Bundle();
		args.putInt(ARGS_PHOTO_ID, id);
		args.putBoolean(ARGS_CONTAINER_LAND, containerLand);
		return args;
	}
	
	public static BitmapLoader newInstance(Context context, Bundle args) {
		return new BitmapLoader(context, args.getInt(ARGS_PHOTO_ID, 0), args.getBoolean(ARGS_CONTAINER_LAND, false));
	}

	public BitmapLoader(Context context, int id, boolean containerLand) {
		super(context);
		this.id = id;
		this.containerLand = containerLand;
	}

	@Override
	public Bitmap loadInBackground() {
		InputStream is = null;
		try {
			URL url = new URL(String.format("http://%s/foto_url/%d/", Constants.BACKEND_DOMAIN, id));
			is = url.openStream();
			Bitmap bmp = BitmapFactory.decodeStream(is);
//			if (containerLand ^ bmp.getWidth() > bmp.getHeight()) {
//				bmp = rotate(bmp, 90.0f);
//			}
			return bmp;
		} catch (IOException ioe) {
			
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {}
			}
		}
		return null;
	}

	private static Bitmap rotate(Bitmap bmp, float f) {
		Matrix mat = new Matrix();
		mat.preRotate(90.0f);
		Bitmap newbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, false);
		if (bmp != newbmp) {
			bmp.recycle();
			bmp = newbmp;
		}
		return bmp;
	}

}
