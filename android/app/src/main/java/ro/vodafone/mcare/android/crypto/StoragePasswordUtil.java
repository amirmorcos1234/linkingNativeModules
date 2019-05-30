package ro.vodafone.mcare.android.crypto;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.Settings;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StoragePasswordUtil {
    private static final byte[] SEED = "sSIvildUHFdKoI40iVM4HNTLAJntbWE97zy8PKYhCpcVtjLbhr3HTXfQz9fZaa".getBytes();

    public static byte[] getStoragePassword(Context context) {
        long appInstallTime;
        String appSignature = "";
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo appInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            appInstallTime = appInfo.firstInstallTime;
            for (Signature s : appInfo.signatures) {
                appSignature += s.toCharsString();
            }
        } catch (Exception e) {
            appInstallTime = 0;
        }
        final String appPackage = context.getPackageName();
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        final String undigestedPassword = (appPackage + appSignature + appInstallTime + androidId);

        byte[] undigestedPasswordBytes;
        try {
            undigestedPasswordBytes = undigestedPassword.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            undigestedPasswordBytes = undigestedPassword.getBytes();
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("sha512");
        } catch (NoSuchAlgorithmException e) {
            try {
                md = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
        }

        final byte[] dbPassword = new byte[64];
        final byte[] rawPassword;

        if (md != null) {
            md.update(SEED);
            md.update(undigestedPasswordBytes);
            rawPassword = md.digest();
        }
        else rawPassword = undigestedPasswordBytes;
        for(int i = 0; i < dbPassword.length; i++)
            dbPassword[i] = (byte)(rawPassword[i%rawPassword.length] ^ SEED[i%SEED.length]);

        return dbPassword;
    }
}
