package com.example.nhosttest.Auth;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.google.gson.Gson;

public class AuthStorageManager {

    private static final String PREF_NAME = "secure_nhost_auth_prefs";
    private static final String KEY_ACCOUNT_INFO = "account_info";
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public AuthStorageManager(Context context) {
        try {
            // MasterKey တည်ဆောက်ခြင်း (security-crypto 1.1.0+)
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // PrefKeyEncryptionScheme တွင် AES256_SKEY ကို သုံးရပါမည်
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            // ကြိုတင်ကာကွယ်မှုအဖြစ် Error တက်ပါက Standard SharedPreferences သို့ Fallback လုပ်မည်
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveAccountInfo(AccountInfo accountInfo) {
        if (sharedPreferences == null) return;
        String json = gson.toJson(accountInfo);
        sharedPreferences.edit().putString(KEY_ACCOUNT_INFO, json).apply();
    }

    public AccountInfo getAccountInfo() {
        if (sharedPreferences == null) return null;
        String json = sharedPreferences.getString(KEY_ACCOUNT_INFO, null);
        if (json == null) return null;
        return gson.fromJson(json, AccountInfo.class);
    }

    public void clearAuthData() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(KEY_ACCOUNT_INFO).apply();
        }
    }
}
