package com.infusiblecoder.allinonevideodownloader.adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.activities.VideoPlayActivity;
import com.infusiblecoder.allinonevideodownloader.interfaces.OnClickFileDeleteListner;
import com.infusiblecoder.allinonevideodownloader.models.StatusSaverGalleryModel;
import com.infusiblecoder.allinonevideodownloader.utils.GlideApp;
import com.infusiblecoder.allinonevideodownloader.utils.iUtils;

import java.io.File;
import java.util.ArrayList;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<StatusSaverGalleryModel> filesList;
    OnClickFileDeleteListner onClickFileDeleteListner;

    public GalleryAdapter(Context context, ArrayList<StatusSaverGalleryModel> filesList, OnClickFileDeleteListner onClickFileDeleteListner) {
        this.context = context;
        this.filesList = filesList;
        this.onClickFileDeleteListner = onClickFileDeleteListner;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_card_row_statussaver, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final StatusSaverGalleryModel files = filesList.get(position);
        final Uri uri = Uri.parse(files.getUri().toString());
        final File file = new File(uri.getPath());
        holder.userName.setText(files.getName());
        if (files.getUri().toString().endsWith(".mp4") || files.getUri().toString().endsWith(".webm")) {
            holder.playIcon.setVisibility(View.VISIBLE);
        } else {
            holder.playIcon.setVisibility(View.INVISIBLE);
        }
        GlideApp.with(context)
                .load(files.getUri())
                .into(holder.savedImage);
        holder.savedImage.setOnClickListener(v -> {
            if (files.getUri().toString().endsWith(".mp4")) {


                try {
                    context.startActivity(new Intent(context, VideoPlayActivity.class)
                            .putExtra("videourl", file.getAbsolutePath()));
                } catch (Exception e) {
                    iUtils.ShowToast(context, context.getResources().getString(R.string.somth_video_wrong));
                    Log.e("Errorisnewis", e.getMessage());
                }


            } else if (files.getUri().toString().endsWith(".jpg")) {
                Uri VideoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(VideoURI, "image/*");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.noApp_f, Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.repostID.setOnClickListener(v -> {

            Uri mainUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

            if (files.getUri().toString().endsWith(".jpg")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setPackage("com.whatsapp");
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.noApp_f, Toast.LENGTH_LONG).show();
                }
            } else if (files.getUri().toString().endsWith(".mp4")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setPackage("com.whatsapp");
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.noApp_f, Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.deleteID.setOnClickListener(v -> {
            final String path = filesList.get(position).getPath();
            final File file1 = new File(path);


            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle(R.string.del_tit);
            builder1.setMessage(R.string.are_sure_del);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    R.string.yyy,
                    (dialog, id) -> {
                        try {
                            if (file1.exists()) {
                                boolean del = file1.delete();
                                onClickFileDeleteListner.delFile(file1.getAbsolutePath());
                                filesList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, filesList.size());
                                notifyDataSetChanged();
                                Toast.makeText(context, R.string.file_del, Toast.LENGTH_SHORT).show();
                                if (del) {
                                    MediaScannerConnection.scanFile(
                                            context,
                                            new String[]{path, path},
                                            new String[]{"image/jpg", "video/mp4"},
                                            new MediaScannerConnection.MediaScannerConnectionClient() {
                                                public void onMediaScannerConnected() {
                                                }

                                                public void onScanCompleted(String path1, Uri uri1) {
                                                    Log.d("Video path: ", path1);
                                                }
                                            });
                                }
                            }
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            builder1.setNegativeButton(
                    R.string.nn,
                    (dialog, id) -> dialog.dismiss());

            AlertDialog alert11 = builder1.create();
            alert11.show();


        });
        holder.shareID.setOnClickListener(v -> {
            Uri mainUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

            if (files.getUri().toString().endsWith(".jpg")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.noApp_f, Toast.LENGTH_LONG).show();
                }
            } else if (files.getUri().toString().endsWith(".mp4")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.noApp_f, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView savedImage;
        ImageView playIcon;
        ImageView repostID, shareID, deleteID;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.profileUserName);
            savedImage = itemView.findViewById(R.id.mainImageView);
            playIcon = itemView.findViewById(R.id.playButtonImage);
            repostID = itemView.findViewById(R.id.repostID);
            shareID = itemView.findViewById(R.id.shareID);
            deleteID = itemView.findViewById(R.id.deleteID);
        }
    }
}
