package com.example.nhosttest.Auth;

public class AccountInfo {
    private String accessToken;
    private long accessTokenExpiresIn; // Seconds
    private String refreshToken;
    private UserInfo userInfo;

    public AccountInfo() {}

    public AccountInfo(String accessToken, long accessTokenExpiresIn, String refreshToken, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshToken = refreshToken;
        this.userInfo = userInfo;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public long getAccessTokenExpiresIn() { return accessTokenExpiresIn; }
    public void setAccessTokenExpiresIn(long accessTokenExpiresIn) { this.accessTokenExpiresIn = accessTokenExpiresIn; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }
}
