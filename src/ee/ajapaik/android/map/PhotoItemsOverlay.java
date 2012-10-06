package ee.ajapaik.android.map;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;


public class PhotoItemsOverlay extends ItemizedOverlay<PhotoItem> {
	private PhotoItem[] items;
	private Listener listener;
	
	public PhotoItemsOverlay(Listener listener, PhotoItem[] items) {
		super(null);
		this.listener = listener;
		this.items = items;
		populate();
	}

	@Override
	protected PhotoItem createItem(int pos) {
		return items[pos];
	}

	@Override
	public int size() {
		return items.length;
	}
	
	public static Drawable bind(Drawable drawable) {
		return boundCenterBottom(drawable);
	}
	
	@Override
	protected boolean onTap(int pos) {
		listener.onTapPhoto(items[pos].getId());
		return true;
	}
	
	public interface Listener {
		public void onTapPhoto(int id);
	}
}
