package com.example.user.fingleprint_master;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;

import javax.crypto.Cipher;

import static javax.crypto.Cipher.ENCRYPT_MODE;

@TargetApi(23)
public class FingerprintDialogFragment extends DialogFragment {

    private FingerprintManagerCompat fingerprintManagerCompat;

    private CancellationSignal mCancellationSignal;

    private Cipher mCipher;

    private MainActivity mActivity;

    private TextView errorMsg;

    /**
     * 标识是否是用户主动取消的认证。
     */
    private boolean isSelfCancelled;

    public void setCipher(Cipher cipher) {
        mCipher = cipher;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fingerprintManagerCompat=FingerprintManagerCompat.from(getContext());
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fingerprint_dialog, container, false);
//        errorMsg = v.findViewById(R.id.error_msg);
//        TextView cancel = v.findViewById(R.id.cancel);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//                stopListening();
//            }
//        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 开始指纹认证监听
            startListening(mCipher);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止指纹认证监听
        stopListening();
    }

    private void startListening(Cipher cipher) {
        isSelfCancelled = false;
        mCancellationSignal = new CancellationSignal();
        fingerprintManagerCompat.authenticate(new FingerprintManagerCompat.CryptoObject(cipher),0,mCancellationSignal,new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                if (!isSelfCancelled) {
                    errorMsg.setText(errString);
                    if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                errorMsg.setText(helpString);
            }
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                Toast.makeText(mActivity, "指纹认证成功", Toast.LENGTH_SHORT).show();
                FingerprintDialogFragment.this.dismiss();
                mActivity.onAuthenticated();
                result.getCryptoObject().getCipher().
            }

            @Override
            public void onAuthenticationFailed() {
                errorMsg.setText("指纹认证失败，请再试一次");
            }
        }, null);
//        fingerprintManagerCompat..authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode, CharSequence errString) {
//                if (!isSelfCancelled) {
//                    errorMsg.setText(errString);
//                    if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
//                        Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show();
//                        dismiss();
//                    }
//                }
//            }
//
//            @Override
//            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//                errorMsg.setText(helpString);
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//                Toast.makeText(mActivity, "指纹认证成功", Toast.LENGTH_SHORT).show();
//                FingerprintDialogFragment.this.dismiss();
//                mActivity.onAuthenticated();
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                errorMsg.setText("指纹认证失败，请再试一次");
//            }
//        }, null);
    }

    private void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            isSelfCancelled = true;
        }
    }

}
