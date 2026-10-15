package com.example.nhosttest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nhosttest.Auth.AccountInfo;
import com.example.nhosttest.Auth.NhostAuthHelper;
import com.example.nhosttest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

     private ActivityMainBinding binding;
     private NhostAuthHelper authHelper;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          binding = ActivityMainBinding.inflate(getLayoutInflater());
          setContentView(binding.getRoot());

          authHelper = new NhostAuthHelper(this);

          // Saved Session ရှိမရှိ စစ်ဆေးခြင်း
          AccountInfo savedAccount = authHelper.getStorageManager().getAccountInfo();
          if (savedAccount != null && savedAccount.getUserInfo() != null) {
               binding.tvStatus.setText(
                         "Saved Session Found!\nEmail: " + savedAccount.getUserInfo().getEmail());
          }

          // App စဖွင့်ချိန် Auto Login & Token Refresh စစ်ဆေးခြင်း
          if (authHelper.isLoggedIn()) {
               binding.tvStatus.setText("Checking & Refreshing Session...");

               // Background တွင် Token သက်တမ်း ရှိ/မရှိ စစ်ပြီး Auto-Refresh လုပ်မည်
               authHelper.refreshToken(new NhostAuthHelper.AuthCallback() {
                    @Override
                    public void onSuccess(AccountInfo accountInfo) {
                         runOnUiThread(() -> binding.tvStatus.setText("Auto Login Success!\nEmail: "
                                   + accountInfo.getUserInfo().getEmail()));
                    }

                    @Override
                    public void onError(String errorMessage) {
                         // Token သက်တမ်း ကုန်သွားပါက သို့မဟုတ် Error တက်ပါက ပြန်လည် Login ဝင်ခိုင်းမည်
                         runOnUiThread(() -> binding.tvStatus
                                   .setText("Session Expired. Please Login Again."));
                    }
               });
          } else {
               binding.tvStatus.setText("Status: Please Login");
          }

          // Login Button
          binding.btnLogin.setOnClickListener(v -> {
               String email = binding.etEmail.getText().toString();
               String password = binding.etPassword.getText().toString();

               authHelper.signIn(email, password, new NhostAuthHelper.AuthCallback() {
                    @Override
                    public void onSuccess(AccountInfo accountInfo) { // AccountInfo ကို Parameter အဖြစ် သုံးပါ
                         runOnUiThread(() -> binding.tvStatus
                                   .setText("Login Success & Saved!\nUser ID: "
                                             + accountInfo.getUserInfo().getId() + "\nEmail: "
                                             + accountInfo.getUserInfo().getEmail()));
                    }

                    @Override
                    public void onError(String errorMessage) {
                         runOnUiThread(() -> binding.tvStatus.setText(errorMessage));
                    }
               });
          });

          // Register Button
          binding.btnRegister.setOnClickListener(v -> {
               String email = binding.etEmail.getText().toString();
               String password = binding.etPassword.getText().toString();

               authHelper.signUp(email, password, new NhostAuthHelper.AuthCallback() {
                    @Override
                    public void onSuccess(AccountInfo accountInfo) { // AccountInfo ကို Parameter အဖြစ် သုံးပါ
                         runOnUiThread(() -> binding.tvStatus
                                   .setText("Register Success & Saved!\nEmail: "
                                             + accountInfo.getUserInfo().getEmail()));
                    }

                    @Override
                    public void onError(String errorMessage) {
                         runOnUiThread(() -> binding.tvStatus.setText(errorMessage));
                    }
               });
          });

          // Logout Button
          binding.btnLogout.setOnClickListener(v -> {
               authHelper.signOut(new NhostAuthHelper.LogoutCallback() {
                    @Override
                    public void onSuccess() { // Parameter မပါပါ
                         runOnUiThread(() -> binding.tvStatus
                                   .setText("Status: Logged Out & Storage Cleared"));
                    }

                    @Override
                    public void onError(String errorMessage) {
                         runOnUiThread(() -> binding.tvStatus
                                   .setText("Logout Notice: " + errorMessage));
                    }
               });
          });
     }
}
