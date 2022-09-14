package com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbmodels;

public class ModelFacebookpref {
    public String fb_pref_cookie = "";
    public String fb_pref_key = "";
    public String fb_pref_isloggedin = "";

    public ModelFacebookpref() {
    }

    public ModelFacebookpref(String fb_pref_cookie, String fb_pref_key, String fb_pref_isloggedin) {
        this.fb_pref_cookie = fb_pref_cookie;
        this.fb_pref_key = fb_pref_key;
        this.fb_pref_isloggedin = fb_pref_isloggedin;
    }

    public String getFb_pref_cookie() {
        return fb_pref_cookie;
    }

    public void setFb_pref_cookie(String fb_pref_cookie) {
        this.fb_pref_cookie = fb_pref_cookie;
    }

    public String getFb_pref_key() {
        return fb_pref_key;
    }

    public void setFb_pref_key(String fb_pref_key) {
        this.fb_pref_key = fb_pref_key;
    }

    public String getFb_pref_isloggedin() {
        return fb_pref_isloggedin;
    }

    public void setFb_pref_isloggedin(String fb_pref_isloggedin) {
        this.fb_pref_isloggedin = fb_pref_isloggedin;
    }
}

