package com.example.foodmeup.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;

import com.example.foodmeup.R;

public class AlertDialogUtils {

    private static final String LOCATION = "location";
    private static final String NETWORK = "network";

    /**
     * Checks if GPS or Network is available, if not shows the respective dialog,
     * if yes, hides the dialog
     * @param lastAlertDialog The Alert Dialog that exists in the Activity and it's either
     *                        showing or hiding.
     *                        The new Alert Dialog will be assigned to this and it will be
     *                        returned to the Activity.
     * @param activity The activity that hosts the Alert Dialog
     * @return An Alert Dialog according to the location or network connectivity.
     *         If both are connected, hides the last Alert Dialog.
     */
    public static android.app.AlertDialog alertCheckConnectivityAvailability(
            AlertDialog lastAlertDialog, final Activity activity) {

        if(!ConnectivityCheck.checkLocationConnectivity(activity))
            (lastAlertDialog = alertNoConnectivity(activity, LOCATION)).show();
        else if(!ConnectivityCheck.checkNetworkConnectivity(activity)){
            (lastAlertDialog = alertNoConnectivity(activity, NETWORK)).show();
        }
        else {
            if(lastAlertDialog != null && lastAlertDialog.isShowing())
                lastAlertDialog.dismiss();
        }

        return lastAlertDialog;
    }

    /**
     * Builds an AlertDialog with information about disabled connections.
     * It starts - location or network - source settings on positive button
     * @param activity The Activity where the dialog will be hosted
     * @param connectivity The type of connectivity to which the Dialog will be shown
     * @return Returns an alert dialog according to the connectivity
     */
    private static android.app.AlertDialog alertNoConnectivity(
            final Activity activity, String connectivity) {

        int alertMessage;
        int alertTitle;
        final String action;

        if (connectivity.equals(LOCATION)){
            alertMessage = R.string.error_gps_disabled_message;
            alertTitle = R.string.error_gps_disabled_title;
            action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        }else{
            alertMessage = R.string.error_network_message;
            alertTitle = R.string.error_network_title;
            action = Settings.ACTION_WIFI_SETTINGS;
        }

        return new android.app.AlertDialog.Builder(activity).setMessage(alertMessage)
                .setTitle(alertTitle)
                .setCancelable(false)
                .setPositiveButton(R.string.error_accept, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    activity.startActivity(new Intent(action));
                })
                .setNegativeButton(R.string.all_cancel, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                }).create();
    }

}
