package com.infusiblecoder.allinonevideodownloader.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.adapters.GalleryAdapter;
import com.infusiblecoder.allinonevideodownloader.models.StatusSaverGalleryModel;
import com.infusiblecoder.allinonevideodownloader.utils.Constants;
import com.infusiblecoder.allinonevideodownloader.utils.FilePathUtility;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class GalleryStatusSaver extends Fragment {

    public ArrayList<StatusSaverGalleryModel> statusSaverGalleryModelArrayList;
    AsyncTask<Void, Void, Void> fetchRecordingsAsyncTask;
    private TextView noresultfound;
    private SwipeRefreshLayout swiperefreshlayout;
    private RecyclerView recststuslist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status_saver_gallery, container, false);


        noresultfound = view.findViewById(R.id.noresultfound);
        swiperefreshlayout = view.findViewById(R.id.swiperefreshlayout);
        recststuslist = view.findViewById(R.id.recststuslist);

        statusSaverGalleryModelArrayList = new ArrayList<>();
        initViews();

//        fetchRecordingsAsyncTask = new FetchRecordingsAsyncTask(getActivity());
//        fetchRecordingsAsyncTask.execute();


        getAllFiles();
        return view;
    }


    private void initViews() {


        swiperefreshlayout.setOnRefreshListener(() -> {
            getAllFiles();
            swiperefreshlayout.setRefreshing(false);
        });
    }

    private void getAllFiles() {
        statusSaverGalleryModelArrayList = new ArrayList<>();


        String location = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME;

        File[] files = new File(location).listFiles();


        if (files != null && files.length > 1) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
        if (files != null) {
            for (int i = 0; i < files.length; i++) {

                statusSaverGalleryModelArrayList.add(new StatusSaverGalleryModel(getString(R.string.savedstt) + i, Uri.fromFile(files[i]), files[i].getAbsolutePath(), files[i].getName()));
            }

            setAdaptertoRecyclerView();
        } else {
            requireActivity().runOnUiThread(() -> noresultfound.setVisibility(View.VISIBLE));
        }
    }


    void setAdaptertoRecyclerView() {

        GalleryAdapter fileListAdapter = new GalleryAdapter(getActivity(), statusSaverGalleryModelArrayList, path -> {
            if (path == null) {
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.error_occ), Toast.LENGTH_SHORT).show();
            } else {
                FilePathUtility.deletefileAndroid10andABOVE(requireActivity(), path, false);

            }
        });
        recststuslist.setAdapter(fileListAdapter);

    }


    private class FetchRecordingsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog;

        public FetchRecordingsAsyncTask(Context activity) {
            dialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.loadingdata));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... args) {
            getAllFiles();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            setAdaptertoRecyclerView();
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (fetchRecordingsAsyncTask != null) {
                    fetchRecordingsAsyncTask.cancel(true);
                }
            }
        }


    }

}