package ee.ajapaik.android.network;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class ProgressMultipartEntity extends MultipartEntity {
	private final Listener listener;
	
	public ProgressMultipartEntity(HttpMultipartMode mode, Listener listener) {
		super(mode);
		this.listener = listener;
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException {
		super.writeTo(new ProgressOutputStream(out, listener));
	}
	
	public static interface Listener {
		public void onProgress(long progress);
	}
	
	private static class ProgressOutputStream extends FilterOutputStream {
		private Listener listener;
		private long progress;
		public ProgressOutputStream(OutputStream out, Listener listener) {
			super(out);
			this.listener = listener;
			progress = 0;
		}
		
		@Override
		public void write(byte[] buffer, int offset, int length) throws IOException {
			out.write(buffer, offset, length);
			progress += length;
			listener.onProgress(progress);
		}
		
		@Override
		public void write(int oneByte) throws IOException {
			out.write(oneByte);
			progress++;
			listener.onProgress(progress);
		}
	}
}
