package com.example.nhosttest;


public class AppConfig {
    // မိမိ Nhost Project ရဲ့ Subdomain နဲ့ Region ကို ဒီမှာ ပြင်ပေးပါ
    public static final String NHOST_SUBDOMAIN = "wdofxmrjwjsuwcmysxed"; 
    public static final String NHOST_REGION = "ap-southeast-1"; // e.g. "eu-central-1", "ap-south-1", "us-east-1"

    // Auth API Base URL
    public static String getAuthBaseUrl() {
        return "https://" + NHOST_SUBDOMAIN + ".auth." + NHOST_REGION + ".nhost.run/v1";
    }

    // နောက်ပိုင်း GraphQL သို့မဟုတ် Storage စမ်းသပ်ပါကလည်း ဒီမှာ တိုးချဲ့ရေးနိုင်ပါသည်
    // public static String getGraphQLUrl() { ... }
    // public static String getStorageUrl() { ... }
}
