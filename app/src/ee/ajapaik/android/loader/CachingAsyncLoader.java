package ee.ajapaik.android.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class CachingAsyncLoader<T> extends AsyncTaskLoader<T> {

	private T cache;

	public CachingAsyncLoader(Context context) {
		super(context);
	}
	
	@Override
	public abstract T loadInBackground();
	
	public void deliverResult(T result) {
		if (isReset()) {
			cache = null;
		}
		if (isStarted()) {
			super.deliverResult(result);
		}
	}
	
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (cache != null) {
			deliverResult(cache);
		} else {
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		cancelLoad();
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
	}

}
