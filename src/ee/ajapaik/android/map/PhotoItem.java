package ee.ajapaik.android.map;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PhotoItem extends OverlayItem {
	private int id;
	
	public PhotoItem(double latitude, double longitude, int id, Drawable marker) {
		super(new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6)), null, null);
		this.id = id;
		setMarker(PhotoItemsOverlay.bind(marker));
	}
	
	public int getId() {
		return id;
	}
}
