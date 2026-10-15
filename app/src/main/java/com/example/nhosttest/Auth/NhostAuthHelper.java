package com.example.nhosttest.Auth;

import android.content.Context;
import android.util.Patterns;
import com.example.nhosttest.AppConfig;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class NhostAuthHelper {

    private final String baseUrl;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final AuthStorageManager storageManager;

    public interface AuthCallback {
        void onSuccess(AccountInfo accountInfo);
        void onError(String errorMessage);
    }

    public interface LogoutCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public NhostAuthHelper(Context context) {
        this.baseUrl = AppConfig.getAuthBaseUrl();
        this.client = new OkHttpClient();
        this.storageManager = new AuthStorageManager(context);
    }

    // ==========================================
    // 1. Account / Session ရှိမရှိ စစ်ဆေးသည့် Helper Methods
    // ==========================================
    
    // Account ရှိပြီး Login ဝင်ထားသလား စစ်ရန်
    public boolean isLoggedIn() {
        AccountInfo account = storageManager.getAccountInfo();
        return account != null 
                && account.getRefreshToken() != null 
                && !account.getRefreshToken().isEmpty();
    }

    // သိမ်းဆည်းထားသော Account/User အချက်အလက်များ တိုက်ရိုက် ယူရန်
    public AccountInfo getSavedAccountInfo() {
        return storageManager.getAccountInfo();
    }

    // ==========================================
    // 2. Token Refresh Method
    // ==========================================
    
    public void refreshToken(AuthCallback callback) {
        AccountInfo savedAccount = storageManager.getAccountInfo();
        
        if (savedAccount == null || savedAccount.getRefreshToken() == null || savedAccount.getRefreshToken().isEmpty()) {
            callback.onError("Refresh Token မရှိပါ။ ကျေးဇူးပြု၍ ပြန်လည် Login ဝင်ပါ။");
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("refreshToken", savedAccount.getRefreshToken());

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(baseUrl + "/token") // Nhost Auth v1 Refresh Token Endpoint
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Network Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        try {
                            // Token အသစ် ရရှိပါက Local Storage တွင် ပြန်လည် Update လုပ်မည်
                            AccountInfo newAccountInfo = parseNhostAuthResponse(responseData);
                            storageManager.saveAccountInfo(newAccountInfo);
                            callback.onSuccess(newAccountInfo);
                        } catch (Exception e) {
                            callback.onError("Parsing Error: " + e.getMessage());
                        }
                    } else {
                        // Refresh Token ပါ သက်တမ်းကုန်သွားပါက Local Storage ကို ရှင်းထုတ်လိုက်မည်
                        storageManager.clearAuthData();
                        callback.onError("Session Expired: ကျေးဇူးပြု၍ ပြန်လည် Login ဝင်ပေးပါ (" + response.code() + ")");
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // --- Input Validation ---
    private String validateInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "Email ဖြည့်စွက်ရန် လိုအပ်ပါသည်။";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "အီးမေးလ် ပုံစံ (Email Format) မှားယွင်းနေပါသည်။";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password ဖြည့်စွက်ရန် လိုအပ်ပါသည်။";
        }
        if (password.length() < 9) {
            return "Password သည် အနည်းဆုံး ၉ လုံး ရှိရပါမည်။";
        }
        return null;
    }

    // Sign Up
    public void signUp(String email, String password, AuthCallback callback) {
        String validationError = validateInput(email, password);
        if (validationError != null) {
            callback.onError("Validation Error: " + validationError);
            return;
        }
        executeAuthRequest("/signup/email-password", email, password, callback);
    }

    // Sign In
    public void signIn(String email, String password, AuthCallback callback) {
        String validationError = validateInput(email, password);
        if (validationError != null) {
            callback.onError("Validation Error: " + validationError);
            return;
        }
        executeAuthRequest("/signin/email-password", email, password, callback);
    }

    // Sign Out
    public void signOut(LogoutCallback callback) {
        AccountInfo accountInfo = storageManager.getAccountInfo();
        String refreshToken = (accountInfo != null) ? accountInfo.getRefreshToken() : "";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("refreshToken", refreshToken);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(baseUrl + "/signout")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    storageManager.clearAuthData();
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    storageManager.clearAuthData();
                    callback.onSuccess();
                }
            });
        } catch (Exception e) {
            storageManager.clearAuthData();
            callback.onError(e.getMessage());
        }
    }

    private void executeAuthRequest(String endpoint, String email, String password, AuthCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email.trim());
            jsonBody.put("password", password);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Network Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        try {
                            AccountInfo accountInfo = parseNhostAuthResponse(responseData);
                            storageManager.saveAccountInfo(accountInfo);
                            callback.onSuccess(accountInfo);
                        } catch (Exception e) {
                            callback.onError("Parsing Error: " + e.getMessage());
                        }
                    } else {
                        callback.onError("HTTP " + response.code() + ": " + responseData);
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private AccountInfo parseNhostAuthResponse(String jsonResponse) throws Exception {
        JSONObject root = new JSONObject(jsonResponse);
        JSONObject sessionObj = root.has("session") && !root.isNull("session") 
                ? root.getJSONObject("session") : root;

        String accessToken = sessionObj.optString("accessToken", "");
        long accessTokenExpiresIn = sessionObj.optLong("accessTokenExpiresIn", 0);
        String refreshToken = sessionObj.optString("refreshToken", "");

        JSONObject userObj = sessionObj.optJSONObject("user");
        UserInfo userInfo = new UserInfo();
        if (userObj != null) {
            userInfo.setId(userObj.optString("id", ""));
            userInfo.setEmail(userObj.optString("email", ""));
            userInfo.setDisplayName(userObj.optString("displayName", ""));
            userInfo.setAvatarUrl(userObj.optString("avatarUrl", ""));
            userInfo.setDefaultRole(userObj.optString("defaultRole", ""));
            userInfo.setAnonymous(userObj.optBoolean("isAnonymous", false));
            userInfo.setEmailVerified(userObj.optBoolean("emailVerified", false));
        }

        return new AccountInfo(accessToken, accessTokenExpiresIn, refreshToken, userInfo);
    }

    public AuthStorageManager getStorageManager() {
        return storageManager;
    }
}
