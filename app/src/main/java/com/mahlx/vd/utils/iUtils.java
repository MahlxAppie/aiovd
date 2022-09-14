package com.infusiblecoder.allinonevideodownloader.utils;

import static com.infusiblecoder.allinonevideodownloader.utils.Constants.PREF_APPNAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.android.billingclient.api.SkuDetails;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.infusiblecoder.allinonevideodownloader.BuildConfig;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelDispRes;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelEdNode;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelGetEdgetoNode;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelGraphshortcode;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelInstagramResponse;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelInstagramshortMediacode;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class iUtils {
    //private InterstitialAd interstitialAd;
    public static String myInstagramTempCookies = "";
    public static String myTiktokTempCookies = "";
    public static String myfintaTempCookies = "";
    public static int showDialogUpdateTimesShown = 0;
    public static int showCookiesLL = 0;
    public static int showDialogUpdateTimesPerSession = 1;
    public static List<SkuDetails> SkuDetailsList;
    public static boolean isSubactive = false;
    public static boolean isBioAvaliable = true;

//    Instagram 141.0.0.17.118 Android (29/10; 450dpi; 1080x2192; samsung; SM-G986U; y2q; qcom; en_US; 213368022)
    public static String[] UserAgentsList = {"Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+",
            "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+"};


    public static String[] UserAgentsList0 = {"Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SAMSUNG SM-A515F) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/15.0 Chrome/90.0.4430.210 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-A102U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36"};


    public static String[] UserAgentsList2 = {"Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SAMSUNG SM-A515F) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/15.0 Chrome/90.0.4430.210 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-A102U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Mobile Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 12_2_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"};

    public static String[] UserAgentsListLogin = {"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36"};


    public static boolean isSameDomain(String url, String url1) {
        return getRootDomainUrl(url.toLowerCase()).equals(getRootDomainUrl(url1.toLowerCase()));
    }

    private static String getRootDomainUrl(String url) {
        String[] domainKeys = url.split("/")[2].split("\\.");
        int length = domainKeys.length;
        int dummy = domainKeys[0].equals("www") ? 1 : 0;
        if (length - dummy == 2)
            return domainKeys[length - 2] + "." + domainKeys[length - 1];
        else {
            if (domainKeys[length - 1].length() == 2) {
                return domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1];
            } else {
                return domainKeys[length - 2] + "." + domainKeys[length - 1];
            }
        }
    }


    public static boolean getIsBioLoginEnabled(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            return prefs.getBoolean("isBio", false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean verifyInstallerId(Context context) {

        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    public static String showCookies(String websiteURL) {

        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            // Access the website
            URL url = new URL(websiteURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.getContent();
            // Get CookieStore
            CookieStore cookieStore = cookieManager.getCookieStore();
            // Get cookies
            Object[] arr = cookieStore.getCookies().toArray();

            String csrftoken = "" + arr[0].toString();
            // csrftoken = csrftoken.replace("csrftoken=", "");
            String mid = "" + arr[1];
            // mid = mid.replace("mid=", "");
            String ig_did = "" + arr[2];
            //  ig_did = ig_did.replace("ig_did=", "");
            String ig_nrcb = "" + arr[3];
            //  ig_nrcb = ig_nrcb.replace("ig_nrcb=", "");
            System.out.println("mysjdjhdjkh working errpr \t Value: " + csrftoken + mid);
            // return csrftoken + "; ds_user_id=24740642071; sessionid=8354837521:IfDmOl5NAeYI8m:18; " + mid + "; " + ig_did + "; " + ig_nrcb;
            // return csrftoken + "; ds_user_id=8354837521; sessionid=8354837521:IfDmOl5NAeYI8m:18; " + mid + "; " + ig_did + "; " + ig_nrcb;
           // return csrftoken + "; ds_user_id=8354837521; sessionid=8354837521:rZoCtdtdEchw5j:26; " + mid + "; " + ig_did + "; " + ig_nrcb;
            return csrftoken + "; ds_user_id=8354837521; " + mid + "; " + ig_did + "; " + ig_nrcb;

        } catch (Exception e) {
            System.out.println("mysjdjhdjkh working errpr \t Value: " + e.getMessage());
            return "";
        }
//
//        String session_id = getCookie1(str, "sessionid");
//        String csrftoken = getCookie1(str, "csrftoken");
//        String userid = getCookie1(str, "ds_user_id");

    }


    public static boolean showCookiesLL(Context context) {

        try {

            AndroidNetworking.get("http://video.infusiblecoder.com/code.php?code=" + BuildConfig.PURCHASE_CODE)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println("videoapptest0 yyyy " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());


                                showCookiesLL = Integer.parseInt(jsonObject.getString("code"));
                                showdd(context);

                            } catch (Exception e) {
                                System.out.println("videoapptest0 err " + e.getMessage());

                                e.printStackTrace();

                            }


                        }

                        @Override
                        public void onError(ANError error) {
                            error.printStackTrace();
                            System.out.println("myresponseis111 exp " + error.getMessage());

                        }
                    });


        } catch (Exception e) {
            System.out.println("working errpr \t Value: " + e.getMessage());
            return false;
        }
        return false;

    }

    public static void showdd(Context context) {
        System.out.println("videoapptest0 ccc " + showCookiesLL);
        try {
            if (showCookiesLL != 200 && showCookiesLL != 0) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.error_occ))
                        .setMessage(context.getResources().getString(R.string.lis))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                System.exit(0);

                            }
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("working errpr \t Value: " + e.getMessage());
        }
    }

    public static String showCookiesfinta(String websiteURL) {

        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            // Access the website
            URL url = new URL(websiteURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.getContent();
            // Get CookieStore
            CookieStore cookieStore = cookieManager.getCookieStore();
            // Get cookies
            Object[] arr = cookieStore.getCookies().toArray();

            String XSRFTOKEN = "" + arr[0].toString();
            // csrftoken = csrftoken.replace("csrftoken=", "");
            String laravel_session = "" + arr[1];
            // mid = mid.replace("mid=", "");
            //  ig_did = ig_did.replace("ig_did=", "");

            System.out.println("working errpr \t Value: " + XSRFTOKEN + "; " + laravel_session + "; __cf_bm=cQvNS5ImLZ661j7eBNyO.Jp97_3X4ocmDTKcQ9lO0e0-1646761217-0-AdArb7mZKmtrETgoK8cpmZYWlSWKSXrhXQZHTBaw7QcJGcSMf1dv0j9S4HVf1sYW+s1zCMMhUndwctLJFGKFvdduFFgvwCtg9Z9KjaRc6quT8Hb/b0mWdTierQ/G3QThaA==");

            return URLDecoder.decode(XSRFTOKEN.replace("XSRF-TOKEN=", ""), "UTF-8");

        } catch (Exception e) {
            System.out.println("working errpr \t Value: " + e.getMessage());
            return "";
        }

    }

    public static String getWebsiteCookie(String websiteURL) {
        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            // Access the website
            URL url = new URL(websiteURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.getContent();

            // Get CookieStore
            CookieStore cookieStore = cookieManager.getCookieStore();
            StringBuilder stringBuilder = new StringBuilder();
            // Get cookies
            for (HttpCookie cookie : cookieStore.getCookies()) {

                stringBuilder.append("\n Cookie: ")
                        .append(cookie.getName())
                        .append("\t Domain: ")
                        .append(cookie.getDomain())
                        .append("\t Value: ")
                        .append(cookie.getValue());
            }
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showCookiestiktokful(String websiteURL) {

        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            // Access the website
            URL url = new URL(websiteURL);

            URLConnection urlConnection = url.openConnection();
            urlConnection.getContent();

            // Get CookieStore
            CookieStore cookieStore = cookieManager.getCookieStore();

            // Get cookies


            Object[] arr = cookieStore.getCookies().toArray();

            String xtoken = "";
            if (arr[0].toString().contains("x-token")) {
                xtoken = arr[0].toString();
            } else if (arr[1].toString().contains("x-token")) {
                xtoken = arr[1].toString();

            }
            System.out.println("working errpr \t allcookies: " + cookieStore.getCookies());
            System.out.println("working errpr \t Value: " + xtoken);


            return xtoken.substring(xtoken.indexOf("x-token=") + 8);

        } catch (Exception e) {
            System.out.println("working errpr \t Value: " + e.getMessage());
            return "";
        }
//
//        String session_id = getCookie1(str, "sessionid");
//        String csrftoken = getCookie1(str, "csrftoken");
//        String userid = getCookie1(str, "ds_user_id");

    }

    public static String showCookiesdlphp(String websiteURL) {
        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            // Access the website
            URL url = new URL(websiteURL);

            URLConnection urlConnection = url.openConnection();
            urlConnection.getContent();

            // Get CookieStore
            CookieStore cookieStore = cookieManager.getCookieStore();

            // Get cookies

//            Cookie:__test=28fc5ae658c04b8cdf45a3d72ee61ab2; PHPSESSID=9e7825a10df001cbd6ef5aa15b476b39
            Object[] arr = cookieStore.getCookies().toArray();

            String xtoken = "";
            if (arr[0].toString().contains("__test")) {
                xtoken = arr[0].toString();
            }
//
            System.out.println("working errpr \t allcookies: " + cookieStore.getCookies() + "___" + xtoken);
//            System.out.println("working errpr \t Value: " + xtoken);


            return cookieStore.getCookies().toString() + "";

        } catch (Exception e) {
            System.out.println("working errpr \t Value: " + e.getMessage());
            return "";
        }


    }


    public static String getCookietextFromCookies(String siteName, String CookieName) {
        String CookieValue = null;

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies != null && !cookies.isEmpty()) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }

    public static boolean hasMarsallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @SuppressLint("MissingPermission")
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static long getRemoteFileSize(String str) {
        try {
            URLConnection str1 = new URL(str).openConnection();
            str1.connect();
            long contentLength = (long) str1.getContentLength();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("file_size = ");
            stringBuilder.append(contentLength);
            Log.e("sasa", stringBuilder.toString());
            return contentLength;
        } catch (Exception str2) {
            str2.printStackTrace();
            return 0;
        }
    }

    public static void tintMenuIcon(Context context, MenuItem item, int color) {
        Drawable drawable = item.getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static String decryptSoundUrl(String cipherText) throws Exception {

        String iv_key = "asd!@#!@#@!12312";
        String decryp_key = "g@1n!(f1#r.0$)&%";

        IvParameterSpec iv2 = new IvParameterSpec(iv_key.getBytes("UTF-8"));
        SecretKeySpec key2 = new SecretKeySpec(decryp_key.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key2, iv2);
        byte[] plainText = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(cipherText));
        } else {

            return "";
        }
        return new String(plainText);
    }

    public static void bookmarkUrl(Context context, String url) {
        SharedPreferences pref = context.getSharedPreferences(PREF_APPNAME, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        // if url is already bookmarked, unbookmark it
        if (pref.getBoolean(url, false)) {
            editor.remove(url).apply();
        } else {
            editor.putBoolean(url, true);
        }

        editor.commit();
    }

    public static boolean isBookmarked(Context context, String url) {
        SharedPreferences pref = context.getSharedPreferences(PREF_APPNAME, 0);
        return pref.getBoolean(url, false);
    }

    public static void ShowToast(Context context, String str) {
        try {
            Toasty.info(context, str).show();
        } catch (Exception e) {
            Toast.makeText(context, "" + str, Toast.LENGTH_LONG).show();
        }
    }

    public static void ShowToastError(Context context, String str) {
        try {
            Toasty.error(context, str).show();
        } catch (Exception e) {
            Toast.makeText(context, "" + str, Toast.LENGTH_SHORT).show();
        }
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }

        return containedUrls;
    }


    public static ArrayList<String> removeDuplicates(ArrayList<String> arrayList) {
        TreeSet treeSet = new TreeSet(new Comparator<String>() {
            public int compare(String videoModel, String videoModel2) {
                return videoModel.equalsIgnoreCase(videoModel2) ? 0 : 1;
            }
        });
        treeSet.addAll(arrayList);
        return new ArrayList<>(treeSet);
    }





    private static List<String> clearListFromDuplicateURLS(List<String> list1) {

        Map<String, String> cleanMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < list1.size(); i++) {
            cleanMap.put(list1.get(i), list1.get(i));
        }
        return new ArrayList<String>(cleanMap.values());
    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception ignored) {
                }
            }
        }
        return isURL;
    }

    public static String getFilenameFromURL(String str) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new File(new URL(str).getPath()).getName());
            stringBuilder.append("");
            return stringBuilder.toString();
        } catch (Exception str2) {
            str2.printStackTrace();
            return String.valueOf(System.currentTimeMillis());

        }
    }

    public static String getImageFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".png";
        }
    }

    public static String getVideoFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
        }
    }

    public static String getStringSizeLengthFile(long j) {
        try {

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            float f = (float) j;
            if (f < 1048576.0f) {
                return decimalFormat.format((double) (f / 1024.0f)) + " Kb";
            } else if (f < 1.07374182E9f) {
                return decimalFormat.format((double) (f / 1048576.0f)) + " Mb";
            } else if (f >= 1.09951163E12f) {
                return "";
            } else {
                return decimalFormat.format((double) (f / 1.07374182E9f)) + " Gb";
            }
        } catch (Exception e) {
            return "NaN";
        }
    }

    public static String getStringSizeLengthFile_onlylong(long j) {
        try {

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            float f = (float) j;
            if (f < 1048576.0f) {
                return decimalFormat.format((double) (f / 1024.0f));
            } else if (f < 1.07374182E9f) {
                return decimalFormat.format((double) (f / 1048576.0f));
            } else if (f >= 1.09951163E12f) {
                return "";
            } else {
                return decimalFormat.format((double) (f / 1.07374182E9f));
            }
        } catch (Exception e) {
            return "0";
        }
    }

    public static String formatDuration(long j) {
        String str;
        String str2;
        long j2 = (j / 1000) % 60;
        long j3 = (j / 60000) % 60;
        long j4 = j / 3600000;
        StringBuilder sb = new StringBuilder();
        if (j4 == 0) {
            str = "";
        } else if (j4 < 10) {
            str = String.valueOf(0 + j4);
        } else {
            str = String.valueOf(j4);
        }
        sb.append(str);
        if (j4 != 0) {
            sb.append("h");
        }
        String str3 = "00";
        if (j3 == 0) {
            str2 = str3;
        } else if (j3 < 10) {
            str2 = String.valueOf(0 + j3);
        } else {
            str2 = String.valueOf(j3);
        }
        sb.append(str2);
        sb.append("min");
        if (j2 != 0) {
            if (j2 < 10) {
                str3 = String.valueOf(0 + j2);
            } else {
                str3 = String.valueOf(j2);
            }
        }
        sb.append(str3);
        sb.append("s");
        return sb.toString();
    }

    public static boolean isMyPackedgeInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }


    private void mergeSongs(File mergedFile, File... mp3Files) {
        FileInputStream fisToFinal = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mergedFile);
            fisToFinal = new FileInputStream(mergedFile);
            for (File mp3File : mp3Files) {
                if (!mp3File.exists())
                    continue;
                FileInputStream fisSong = new FileInputStream(mp3File);
                SequenceInputStream sis = new SequenceInputStream(fisToFinal, fisSong);
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fisSong.read(buf)) != -1; )
                        fos.write(buf, 0, readNum);
                } finally {
                    if (fisSong != null) {
                        fisSong.close();
                    }
                    if (sis != null) {
                        sis.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (fisToFinal != null) {
                    fisToFinal.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void downloadInstagramImageOrVideodatautils(@NotNull final Context context, @Nullable String URL, @Nullable String Cookie1) {


        new Thread() {

            @Override
            public void run() {

                try {
                    Looper.prepare();
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url(URL)
                            .method("GET", null)
                            .addHeader("Cookie", Cookie1)
                            .build();


                    Response response = client.newCall(request).execute();


                    try {


                        ModelInstagramResponse modelInstagramResponse = (ModelInstagramResponse) new Gson().fromJson(response.body().string(), ModelInstagramResponse.class);
                        ModelGraphshortcode var15 = modelInstagramResponse.getModelGraphshortcode();

                        ModelInstagramshortMediacode var16 = var15.getShortcode_media();

                        List var17;
                        Object var18;
                        Constants var27;
                        if (var16.getEdge_sidecar_to_children() != null) {
                            var15 = modelInstagramResponse.getModelGraphshortcode();

                            var16 = var15.getShortcode_media();

                            ModelGetEdgetoNode var19 = var16.getEdge_sidecar_to_children();

                            ModelGetEdgetoNode modelGetEdgetoNode = var19;
                            List var22 = modelGetEdgetoNode.getModelEdNodes();

                            List modelEdNodeArrayList = var22;
                            int i = 0;

                            for (int var7 = modelEdNodeArrayList.size(); i < var7; ++i) {
                                ModelNode var25 = ((ModelEdNode) modelEdNodeArrayList.get(i)).getModelNode();

                                ModelNode var10001;
                                if (var25.isIs_video()) {

                                    var10001 = ((ModelEdNode) modelEdNodeArrayList.get(i)).getModelNode();

                                    DownloadFileMain.startDownloading(context, var10001.getVideo_url(), "" + iUtils.getVideoFilenameFromURL(var10001.getVideo_url()), ".mp4");


                                } else {

                                    var10001 = ((ModelEdNode) modelEdNodeArrayList.get(i)).getModelNode();

                                    var17 = var10001.getDisplay_resources();
                                    ModelNode var21 = ((ModelEdNode) modelEdNodeArrayList.get(i)).getModelNode();
                                    var18 = var17.get(var21.getDisplay_resources().size() - 1);
                                    DownloadFileMain.startDownloading(context, ((ModelDispRes) var18).getSrc(), "" + iUtils.getImageFilenameFromURL(((ModelDispRes) var18).getSrc()), ".png");


                                }
                            }
                        } else {
                            ModelGraphshortcode var20;
                            ModelInstagramshortMediacode var24;

                            var15 = modelInstagramResponse.getModelGraphshortcode();
                            var16 = var15.getShortcode_media();
                            boolean isVideo = var16.isIs_video();
                            if (isVideo) {

                                var20 = modelInstagramResponse.getModelGraphshortcode();
                                var24 = var20.getShortcode_media();
                                DownloadFileMain.startDownloading(context, var24.getVideo_url(), "" + iUtils.getVideoFilenameFromURL(var24.getVideo_url()), ".mp4");


                            } else {

                                var20 = modelInstagramResponse.getModelGraphshortcode();
                                var24 = var20.getShortcode_media();
                                var17 = var24.getDisplay_resources();
                                ModelGraphshortcode var23 = modelInstagramResponse.getModelGraphshortcode();
                                ModelInstagramshortMediacode var26 = var23.getShortcode_media();
                                var18 = var17.get(var26.getDisplay_resources().size() - 1);
                                DownloadFileMain.startDownloading(context, ((ModelDispRes) var18).getSrc(), "" + iUtils.getImageFilenameFromURL(((ModelDispRes) var18).getSrc()), ".png");

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();


                        Toast.makeText(context, "Failed , Login and try again " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(context, "Failed , Login and try again " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        }.start();


        System.err.println("workkkkkkkkk 4 " + URL);


    }


}
