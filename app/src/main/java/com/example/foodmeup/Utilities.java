package com.example.foodmeup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class Utilities {

    /**
     * Setup the custom Layout in the privacy policy Alert Dialog
     * Selected Intent.CATEGORY_BROWSABLE category so it opens directly on a browser
     * @param activity The activity that hosts the Alert Dialog
     * @return The custom Layout (View) for the privacy policy Alert Dialog
     */
    static View setPrivacyDialogLayout(final Activity activity) {
        @SuppressLint("InflateParams")
        View customLayout = activity.getLayoutInflater().inflate(R.layout.dialog_layout, null);

        TextView dialogTextTextView = customLayout.findViewById(R.id.dialog_text);
        dialogTextTextView.setText(activity.getResources().getString(R.string.privacy_dialog_text));

        if (dialogTextTextView.getText().toString().contains(activity.getResources().
                getString(R.string.privacy_dialog_privacy_policy_hyperlink_text))) {
            Utilities.setClickableHighlightedText(dialogTextTextView, activity.getResources().
                            getString(R.string.privacy_dialog_privacy_policy_hyperlink_text)
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(activity.getResources().
                                    getString(R.string.privacy_dialog_hyperlink)));
                            activity.getApplicationContext().startActivity(intent);
                        }
                    });
        }
        return customLayout;
    }

    /**
     * The action that happens when the user clicks the hyperlink in privacy policy Alert Dialog
     * Highlights a selected text as hyperlink.
     * @param tv the TextView that contains the hyperlink
     * @param textToHighlight the TextView that will be highlighted
     * @param onClickListener the Callback listener when the user click the hyperlink
     */
    private static void setClickableHighlightedText(final TextView tv, String textToHighlight,
                                                    final View.OnClickListener onClickListener) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                if (onClickListener != null) onClickListener.onClick(textView);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(tv.getContext().getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        };

        SpannableString wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                wordToSpan.setSpan(clickableSpan, ofe, ofe +
                        textToHighlight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }


    /**
     * Register the Broadcast Receivers for the MapActivity
     * @param mapsActivity The Activity that register the receivers
     * @param connectionStateReceiver Instance of the Broadcast Receiver
     */
    public static void registerReceivers(MapsActivity mapsActivity, BroadcastReceiver connectionStateReceiver) {

        IntentFilter filterGps = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filterGps.addAction(Intent.ACTION_PROVIDER_CHANGED);
        mapsActivity.registerReceiver(connectionStateReceiver, filterGps);

        IntentFilter filterNetwork = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filterNetwork.addAction(Intent.ACTION_PROVIDER_CHANGED);
        mapsActivity.registerReceiver(connectionStateReceiver, filterNetwork);
    }
}