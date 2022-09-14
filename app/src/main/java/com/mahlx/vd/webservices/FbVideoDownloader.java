package com.infusiblecoder.allinonevideodownloader.webservices;


import static com.infusiblecoder.allinonevideodownloader.webservices.DownloadVideosMain.Mcontext;
import static com.infusiblecoder.allinonevideodownloader.webservices.DownloadVideosMain.fromService;
import static com.infusiblecoder.allinonevideodownloader.webservices.DownloadVideosMain.pd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;

import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.interfaces.VideoDownloader;
import com.infusiblecoder.allinonevideodownloader.models.dlapismodels.VideoModel;
import com.infusiblecoder.allinonevideodownloader.utils.DownloadFileMain;
import com.infusiblecoder.allinonevideodownloader.utils.iUtils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Random;

@Keep
public class FbVideoDownloader implements VideoDownloader {

    public static String downlink;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    String matag;
    int myselmethode = 0;
    private final String VideoURL;
    private String VideoTitle;

    public FbVideoDownloader(Context context, String videoURL, int method) {
        this.context = context;
        VideoURL = videoURL;
        myselmethode = method;
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }


    @Override
    public String getVideoId(String link) {
        return link;
    }

    @Override
    public void DownloadVideo() {

        try {
            downlink = VideoURL;


            switch (myselmethode) {

                case 0: {
                    downlink = downlink.contains("www.facebook") ? downlink.replace("www.facebook", "m.facebook") : downlink;
                    downlink = downlink.contains("story.php") ? downlink : downlink + "?refsrc=https%3A%2F%2Fm.facebook.com%2F&refid=28&_rdr";

                    new Fbwatch_myown().execute();

                    break;
                }
                case 1: {
                    new Fbwatch_getfvid().execute();
                    break;
                }
                case 2: {
                    new Fbwatch_fbdown().execute();
                    break;
                }
            }
        } catch (Exception e) {

            if (!fromService && pd != null) {

                pd.dismiss();
            }

            new Handler(Looper.getMainLooper()).post(() -> iUtils.ShowToast(Mcontext, Mcontext.getResources().getString(R.string.somthing)));
        }

    }

    public static class Fbwatch_myown extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {

                String uag = "";
                Random rand = new Random();
                int rand_int1 = rand.nextInt(2);

                if (rand_int1 == 0) {
                    uag = "Mozilla/5.0 (Linux; Android 5.0.2; SAMSUNG SM-G925F Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36";
                } else {
                    uag = "Mozilla/5.0 (Linux; U; Android 4.1.1; en-gb; Build/KLP) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30";
                }


                this.RoposoDoc = Jsoup.connect(downlink)
                        .header("Accept", "*/*")
                        .userAgent(uag)
                        .get();

            } catch (Exception e) {


            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            boolean isSecon = false;

            try {

                if (!fromService && pd != null) {

                    pd.dismiss();
                }
                System.out.println("myresponseis111 exp166 " + document);

                String data = "";

                Elements elements = document.select("script");
                for (Element element : elements) {
                    if (element.attr("type").equals("application/ld+json")) {


                        JSONObject obj = new JSONObject(element.html());

                        System.out.println("myresponseis111 list_of_qualities" + obj);


                        String replaceString = obj.getString("contentUrl");
                        System.out.println("myresponseis111 list_of_qualities" + replaceString);
                        DownloadFileMain.startDownloading(Mcontext, replaceString, "Facebook_" + System.currentTimeMillis(), ".mp4");

                        downlink = "";

                    }
                }


            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());
                downlink = "";

                if (!fromService && pd != null) {

                    pd.dismiss();
                }

                new Handler(Looper.getMainLooper()).post(() -> iUtils.ShowToastError(Mcontext, Mcontext.getResources().getString(R.string.somthing)));
            }
        }


    }

    public static class Fbwatch_tiktok extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect("https://vm.tiktok.com/ZSQTnNWu/")
                        .header("Accept", "*/*")
                        .userAgent("PostmanRuntime/7.26.8")
                        .get();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            boolean isSecon = false;

            try {

                if (!fromService && pd != null) {

                    pd.dismiss();
                }
                System.out.println("myresponseis111 exp166 " + document);

                String data = "";

                Elements elements = document.select("script");
                for (Element element : elements) {
                    if (element.attr("type").equals("application/ld+json")) {


                        JSONObject obj = new JSONObject(element.html());

                        System.out.println("myresponseis111 list_of_qualities" + obj);


                        String replaceString = obj.getString("contentUrl");
                        System.out.println("myresponseis111 list_of_qualities" + replaceString);
                        DownloadFileMain.startDownloading(Mcontext, replaceString, "Facebook_" + System.currentTimeMillis(), ".mp4");

                        downlink = "";

                    }
                }


            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());
                downlink = "";

                if (!fromService && pd != null) {

                    pd.dismiss();
                }
                iUtils.ShowToast(Mcontext, Mcontext.getResources().getString(R.string.somthing));
            }
        }


    }

    private class Fbwatch_fbdown extends AsyncTask<Void, Boolean, Boolean> {

        String title;
        ArrayList<VideoModel> removeDuplicatesvalues = new ArrayList<>();


        CharSequence[] charSequenceArr = {context.getString(R.string.hdlink) + "", context.getString(R.string.sdlink) + ""};

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect("https://www.getfvid.com/downloader")
                        .data("url", downlink)
                        .header("accept-encoding", "gzip, deflate, br")
                        .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
                        .header("content-type", "application/x-www-form-urlencoded")
                        .header("dnt", "1")
                        .header("origin", "https://www.getfvid.com")
                        .header("referer", "https://www.getfvid.com/")
                        .header("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36")
                        .timeout(100 * 1000)
                        .post();

                String atag = doc.select("a").get(9).attr("href");

                Log.e("TextLink", atag);

                matag = atag;
//                }
                return true;
            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {

            if (aVoid) {
                try {
                    if (!fromService && pd != null) {

                        pd.dismiss();
                    }

                    DownloadFileMain.startDownloading(context, matag, "Facebook_" + System.currentTimeMillis(), ".mp4");


                } catch (Exception e) {

                    if (!fromService && pd != null) {

                        pd.dismiss();
                    }

                    new Handler(Looper.getMainLooper()).post(() -> iUtils.ShowToast(Mcontext, Mcontext.getResources().getString(R.string.somthing)));
                }

            } else {
                if (!fromService && pd != null) {

                    pd.dismiss();
                }

                new Handler(Looper.getMainLooper()).post(() -> iUtils.ShowToast(Mcontext, Mcontext.getResources().getString(R.string.somthing)));
            }
        }
    }

    private class Fbwatch_getfvid extends AsyncTask<Void, Boolean, Boolean> {

        String title;
        ArrayList<VideoModel> removeDuplicatesvalues = new ArrayList<>();


        CharSequence[] charSequenceArr = {context.getString(R.string.hdlink) + "", context.getString(R.string.sdlink) + ""};

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {


            try {

                Document doc = Jsoup.connect("https://downvideo.net/download.php")
                        .data("URL", downlink)
                        .data("token", "2c17c6393771ee3048ae34d6b380c5ecz")
                        .timeout(100 * 1000)
                        .post();


                String myhd9 = doc.select("a").get(4).attr("href");
                String myhd = doc.select("a").get(5).attr("href");

                Log.e("TextLink myrrr 0.5 ", myhd);
                Log.e("TextLink myrrr 0 ", myhd9);


                VideoModel videoModel2 = new VideoModel();
                videoModel2.setUrl(myhd);
                removeDuplicatesvalues.add(videoModel2);

                VideoModel videoModel1 = new VideoModel();
                videoModel1.setUrl(myhd9);
                removeDuplicatesvalues.add(videoModel1);


                return true;


//                Element link = doc.body().getElementById("sdlink");
//                String atag = link.select("a").first().attr("href");// 10,11
//                // atag = URLDecoder.decode(atag, "UTF-8");
//                Log.e("TextLink", atag);
//
//                matag = atag;
//                }
                //  Log.e("Main12346", matag);
            } catch (Exception e) {


                return false;

            }
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {

            if (aVoid) {
                try {
                    if (!fromService && pd != null) {

                        pd.dismiss();
                    }
                    //   new downloadFile().Downloading(context, matag, "Facebook_" + System.currentTimeMillis(), ".mp4");


                    new AlertDialog.Builder(context).setTitle(context.getString(R.string.select_quality))
                            .setItems(charSequenceArr, (dialogInterface, i) -> DownloadFileMain.startDownloading(context, removeDuplicatesvalues.get(i).getUrl(), "Facebook_" + System.currentTimeMillis(), ".mp4")).setPositiveButton(R.string.cancel_option, (dialogInterface, i) -> dialogInterface.dismiss()).setCancelable(false).show();
                } catch (Exception e) {

                    if (!fromService && pd != null) {

                        pd.dismiss();
                    }

//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            iUtils.ShowToast(Mcontext, Mcontext.getResources().getString(R.string.somthing));
//
//                        }
//                    });
                }


            } else {
                try {
                    if (!fromService && pd != null) {

                        pd.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        iUtils.ShowToast(Mcontext, Mcontext.getResources().getString(R.string.somthing));
//
//                    }
//                });
            }
        }
    }


}
