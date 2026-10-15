package com.example.nhosttest.Auth;

public class UserInfo {
    private String id;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String defaultRole;
    private boolean isAnonymous;
    private boolean emailVerified;

    public UserInfo() {}

    public UserInfo(String id, String email, String displayName, String avatarUrl, String defaultRole, boolean isAnonymous, boolean emailVerified) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.defaultRole = defaultRole;
        this.isAnonymous = isAnonymous;
        this.emailVerified = emailVerified;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getDefaultRole() { return defaultRole; }
    public void setDefaultRole(String defaultRole) { this.defaultRole = defaultRole; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}
