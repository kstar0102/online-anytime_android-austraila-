package com.austraila.online_anytime.Common;

public class Common {
    private String ApiKey = "api_key=54d0a2c6b96b514cb47c3645714f7ce8";
    private String TestloginUrl = "http://192.168.107.90:89/login?";
    private String LogingUrl = "http://online-anytime.com.au";

    private static Common instance = new Common();

    public static Common getInstance()
    {
        return instance;
    }

    public String getBaseURL() {
        return TestloginUrl;
    }
    public String getApiKey() {
        return ApiKey;
    }
}
