package com.faceidnative.faceidapplication;

import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.Activity;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactMethod;
import android.support.v4.app.ActivityCompat;

import android.hardware.fingerprint.FingerprintManager;
import android.app.KeyguardManager;
import android.util.Base64;
import com.facebook.react.bridge.Callback;


import java.util.Map;
import java.util.HashMap;

public class FingerPrintAuthModule extends ReactContextBaseJavaModule{
    Context mContext;
    private boolean isAppActive;
    public String errorText;
    public final int AUTHEN_SUCCESSED = 1;
    public Callback reactErrorCallback;
    public Callback reactSuccessCallback;
    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

      @Override
      public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode == AUTHEN_SUCCESSED) {
                  if(resultCode == Activity.RESULT_OK){
                    byte[] result=intent.getByteArrayExtra("result");
                      if(result != null){
                        String imageEncoded = Base64.encodeToString(result, Base64.DEFAULT);
                        reactSuccessCallback.invoke(imageEncoded);
                      }
                  }
              }
    }
  };
    public FingerPrintAuthModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);

    }

    @Override
    public String getName() {
        return "FingerPrintAuth2";
    }

    @ReactMethod
    public void getFingerPrintAuth(final Callback reactErrorCallback, final Callback reactSuccessCallback) {
        this.reactErrorCallback = reactErrorCallback;
        this.reactSuccessCallback = reactSuccessCallback;
         Intent intent = new Intent(mContext, FaceVerificationApplication.class);
         getCurrentActivity().startActivityForResult(intent,AUTHEN_SUCCESSED);
    }
    public void test(){
      this.reactSuccessCallback.invoke();
    }

    @ReactMethod
    public void isSupported(final Callback reactErrorCallback, final Callback reactSuccessCallback) {
        this.reactErrorCallback = reactErrorCallback;
        this.reactSuccessCallback = reactSuccessCallback;
        FingerprintManager fingerprintManager = (FingerprintManager) getCurrentActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) getCurrentActivity().getSystemService(Context.KEYGUARD_SERVICE);
        String errorMessage = "";
        if (fingerprintManager.isHardwareDetected()) {
          errorMessage=FingerPrintContains.FINGER_PRINT_SUPPORT_NOT_DETECTED;
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
              errorMessage=FingerPrintContains.FINGER_PRINT_DONT_HAVE_PERMISSION;
            } else {
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                  errorMessage=FingerPrintContains.FINGER_PRINT_SUPPORT_ENROLLED_ERROR;
                } else {
                    if (!keyguardManager.isKeyguardSecure()) {
                      errorMessage=FingerPrintContains.FINGER_PRINT_SUPPORT_KEYGUARD_SECURE_NOT_ENABLE;
                    }
                }
            }
        }
        if(!"".equalsIgnoreCase(errorMessage)){
          reactErrorCallback.invoke(errorMessage);
        }else{
          reactSuccessCallback.invoke();
        }
    }
}