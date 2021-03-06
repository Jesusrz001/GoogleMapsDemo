package mobile.jesus.com.banklocator.dialogs;

/**
 * Created by jr02815 on 7/21/2017.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;
import mobile.jesus.com.banklocator.R;

/**
 * A dialog that displays a permission denied message.
 */
public class PermissionDeniedDialog extends DialogFragment {

    private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

    private boolean mFinishActivity = false;

    /**
     * Creates a new instance of this dialog and optionally finishes the calling Activity
     * when the 'Ok' button is clicked.
     */
    public static PermissionDeniedDialog newInstance(boolean finishActivity) {
        Bundle arguments = new Bundle();
        arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);

        PermissionDeniedDialog dialog = new PermissionDeniedDialog();
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mFinishActivity = getArguments().getBoolean(ARGUMENT_FINISH_ACTIVITY);

        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.location_permission_denied)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mFinishActivity) {
            Toast.makeText(getActivity(), R.string.permission_required_toast,
                    Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }
}
