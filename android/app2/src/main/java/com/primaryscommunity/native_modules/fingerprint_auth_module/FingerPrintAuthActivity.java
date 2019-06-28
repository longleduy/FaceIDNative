package com.primaryscommunity.native_modules.fingerprint_auth_module;
import com.primaryscommunity.R;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.facebook.react.bridge.Callback;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Permission;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import android.hardware.fingerprint.FingerprintManager;
import android.app.KeyguardManager;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import android.content.Intent;

public class FingerPrintAuthActivity extends AppCompatActivity {
    private TextView txtError;
    private KeyStore keyStore;
    private static final String KEY_NAME = "devsKey";
    private Cipher cipher;
    private TextView textView;
    private Button btnClose;
    private Callback dialogCallback;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);
        txtError = (TextView) findViewById(R.id.txtError);
        btnClose = (Button) findViewById(R.id.btnClose);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!fingerprintManager.isHardwareDetected()) {
            txtError.setText(FingerPrintContains.FINGER_PRINT_SUPPORT_NOT_DETECTED);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                txtError.setText(FingerPrintContains.FINGER_PRINT_DONT_HAVE_PERMISSION);
            } else {
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    txtError.setText(FingerPrintContains.FINGER_PRINT_SUPPORT_ENROLLED_ERROR);
                } else {
                    if (!keyguardManager.isKeyguardSecure()) {
                        txtError.setText(FingerPrintContains.FINGER_PRINT_SUPPORT_KEYGUARD_SECURE_NOT_ENABLE);
                    }
                    else{
                        try {
                            generateKey();
                            if(cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                                fingerprintHandler.startFingerIDAuthen(fingerprintManager,cryptoObject);
                            }
                        } catch (Exception e) {
                            txtError.setText(FingerPrintContains.FINGER_PRINT_SUPPORT_ERROR);
                        }

                    }
                }
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT
                            | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException |
                CertificateException |
                UnrecoverableKeyException |
                IOException |
                NoSuchAlgorithmException |
                InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
    public void setDialogCallback(Callback newDialogCallback) {
        this.dialogCallback = newDialogCallback;
    }
}
