package com.primaryscommunity.native_modules.fingerprint_auth_module;

import com.primaryscommunity.R;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;


public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;
    FingerPrintContains fingerPrintContains = new FingerPrintContains();

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startFingerIDAuthen(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        String errorMessage = fingerPrintContains.getErrorMessage(errMsgId);
        this.updateStatus(errorMessage, false, "ERROR");

    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        String helpMessage = fingerPrintContains.getHelpMessage(helpMsgId);
        this.updateStatus(helpMessage, false, "HELP");
    }


    @Override
    public void onAuthenticationFailed() {
        this.updateStatus(fingerPrintContains.FINGERPRINT_FAILED, false, "FAILED");
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "SUCCESSED");
        ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
        ((Activity) context).finish();
    }

    public void updateStatus(String message, Boolean isSuccess, String status) {
        TextView txtError = (TextView) ((Activity) context).findViewById(R.id.txtError);
        ImageView imgFingerPrintIcon = (ImageView) ((Activity) context).findViewById(R.id.imgFingerPrintIcon);
        txtError.setText(message);
        if (!isSuccess) {
            txtError.setTextColor(Color.parseColor(status.equalsIgnoreCase("HELP") ? "#477eff" : "#e60000"));
            imgFingerPrintIcon.setImageResource(
                    status.equalsIgnoreCase("HELP")
                            ? R.drawable.ic_fingerprint_help_24dp
                            : R.drawable.ic_fingerprint_error_24dp
            );
        } else {
            imgFingerPrintIcon.setImageResource(R.drawable.ic_fingerprint_success_24dp);
        }
    }
}
