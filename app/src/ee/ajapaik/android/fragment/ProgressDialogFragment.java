package ee.ajapaik.android.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	
	private int max = 0;
	
	public ProgressDialogFragment() {
		this(0);
	}
	
	public ProgressDialogFragment(int max) {
		this.max = max;
	}
	
	@Override
	public ProgressDialog onCreateDialog(Bundle savedInstanceState) {
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Hetk...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.setMax(max);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		return dialog;
	}
	
	@Override
	public ProgressDialog getDialog() {
		// TODO Auto-generated method stub
		return (ProgressDialog) super.getDialog();
	}
}
