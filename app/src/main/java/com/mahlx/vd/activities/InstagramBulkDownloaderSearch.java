package com.infusiblecoder.allinonevideodownloader.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.infusiblecoder.allinonevideodownloader.adapters.ProfileBulkLayoutAdapter;
import com.infusiblecoder.allinonevideodownloader.databinding.ActivityInstagramBulkDownloaderBinding;
import com.infusiblecoder.allinonevideodownloader.models.bulkdownloader.UserUser;
import com.infusiblecoder.allinonevideodownloader.models.instawithlogin.ModelInstagramPref;
import com.infusiblecoder.allinonevideodownloader.utils.SharedPrefsForInstagram;
import com.infusiblecoder.allinonevideodownloader.utils.iUtils;
import com.infusiblecoder.allinonevideodownloader.webservices.api.RetrofitApiInterface;
import com.infusiblecoder.allinonevideodownloader.webservices.api.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.mauker.materialsearchview.MaterialSearchView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Keep
public class InstagramBulkDownloaderSearch extends AppCompatActivity {

    private ProfileBulkLayoutAdapter adapter;

    private ActivityInstagramBulkDownloaderBinding binding;
    private ProgressDialog progressDralogGenaratinglink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstagramBulkDownloaderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {

            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

            progressDralogGenaratinglink = new ProgressDialog(InstagramBulkDownloaderSearch.this);


            binding.txtSearchClick.setOnClickListener(view1 -> binding.searchinstaprofile.openSearch());

            binding.searchinstaprofile.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(@NonNull String s) {

                    System.out.println("hvjksdhfhdkd bb " + s);
                    loadSearchData(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(@NonNull String s) {
                    return false;
                }
            });

            loadDummyData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadSearchData(String username) {

        SharedPrefsForInstagram sharedPrefsFor = new SharedPrefsForInstagram(InstagramBulkDownloaderSearch.this);
        ModelInstagramPref map = sharedPrefsFor.getPreference();
        String myCookies = "";
        if (map != null && map.getPREFERENCE_USERID() != null && !map.getPREFERENCE_USERID().equals("oopsDintWork") && !map.getPREFERENCE_USERID().equals("")) {
            myCookies = "ds_user_id=" + map.getPREFERENCE_USERID() + "; sessionid=" + map.getPREFERENCE_SESSIONID();
            System.out.println("hvjksdhfhdkd userpkId yhyhy ");


        } else {
            System.out.println("hvjksdhfhdkd userpkId 3434 ");

            myCookies = iUtils.myInstagramTempCookies;
        }

        if (TextUtils.isEmpty(myCookies)) {
            myCookies = "";
        }

        RetrofitApiInterface apiService = RetrofitClient.getClient();

        Call<JsonObject> callResult = apiService.getInstagramSearchResults("https://www.instagram.com/web/search/topsearch/?query=" + username, myCookies,
                "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");


        callResult.enqueue(new Callback<JsonObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                System.out.println("response1122334455_jsomobj:   " + response);

                if (progressDralogGenaratinglink != null)
                    progressDralogGenaratinglink.dismiss();
                List<UserUser> list = new ArrayList<>();


                //   System.out.println("hvjksdhfhdkd " + response.toString().substring(0,150));
                //4162923872
                //3401888503
                try {


                    JsonArray responseJSONArray = Objects.requireNonNull(response.body()).getAsJsonArray("users");
                    for (int i = 0; i < responseJSONArray.size(); i++) {
                        Gson gson = new Gson();

                        UserUser gsonObj = gson.fromJson(responseJSONArray.get(i).getAsJsonObject().get("user").getAsJsonObject().toString(), UserUser.class);
                        list.add(gsonObj);
                        System.out.println("hvjksdhfhdkd " + gsonObj.is_verified);

                    }


                    //     System.out.println("hvjksdhfhdkd length " + response.getJSONArray("places").getJSONObject(0).getJSONObject("place").getJSONObject("location").getString("short_name"));

                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(InstagramBulkDownloaderSearch.this));

                    adapter = new ProfileBulkLayoutAdapter(list, InstagramBulkDownloaderSearch.this);

                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } catch (Exception e) {

                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                System.out.println("response1122334455:   " + "Failed0");

            }
        });


    }


    @SuppressLint("NotifyDataSetChanged")
    void loadDummyData() {


        List<UserUser> list = new ArrayList<>();


        //   System.out.println("hvjksdhfhdkd " + response.toString().substring(0,150));
        //4162923872
        //3401888503
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());

            JSONArray responseJSONArray = obj.getJSONArray("users");
            for (int i = 0; i < responseJSONArray.length(); i++) {
                Gson gson = new Gson();

                UserUser gsonObj = gson.fromJson(responseJSONArray.getJSONObject(i).getJSONObject("user").toString(), UserUser.class);
                list.add(gsonObj);
                System.out.println("hvjksdhfhdkd " + gsonObj.is_verified);

            }

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(InstagramBulkDownloaderSearch.this));

            adapter = new ProfileBulkLayoutAdapter(list, InstagramBulkDownloaderSearch.this);

            binding.recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("dummy.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}