package mobile.jesus.com.banklocator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * Created by jr02815 on 7/21/2017.
 */

public class PermissionDialog extends DialogFragment {

    private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

    private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

    private boolean finishActivity = false;

    /**
     * Creates a new instance of a dialog displaying the rationale for the use of the location
     * permission.
     * <p>
     * The permission is requested after clicking 'ok'.
     *
     * @param requestCode    Id of the request that is used to request the permission. It is
     *                       returned to the
     *                       {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
     * @param finishActivity Whether the calling Activity should be finished if the dialog is
     *                       cancelled.
     */
    public static PermissionDialog newInstance(int requestCode, boolean finishActivity) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
        arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
        PermissionDialog dialog = new PermissionDialog();
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
        finishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY);

        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.permission_location)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // After click on Ok, request the permission.
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                                requestCode);
                        // Do not finish the Activity while requesting permission.
                        finishActivity = false;
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (finishActivity) {
            Toast.makeText(getActivity(),
                    R.string.permission_required_toast,
                    Toast.LENGTH_SHORT)
                    .show();
            getActivity().finish();
        }
    }
}
