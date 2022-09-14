package com.infusiblecoder.allinonevideodownloader.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.activities.FullImageActivity;
import com.infusiblecoder.allinonevideodownloader.models.bulkdownloader.EdgeInfo;
import com.infusiblecoder.allinonevideodownloader.utils.Constants;
import com.infusiblecoder.allinonevideodownloader.utils.GlideApp;
import com.infusiblecoder.allinonevideodownloader.utils.iUtils;

import java.util.List;


public class ListAllProfilePostsInstagramUserAdapter extends RecyclerView.Adapter<ListAllProfilePostsInstagramUserAdapter.ViewHolder> {
    private final Context context;
    private final List<EdgeInfo> storyModelInstaItemList;

    public ListAllProfilePostsInstagramUserAdapter(Context context, List<EdgeInfo> list) {
        this.context = context;
        this.storyModelInstaItemList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.items_instastory_view_placeholder, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        EdgeInfo modelInstaItem = storyModelInstaItemList.get(position);

        try {
            if (modelInstaItem.getNode().getIs_video()) {
                viewHolder.playIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.playIcon.setVisibility(View.GONE);
            }
            GlideApp.with(context)
                    .load(modelInstaItem.getNode().getThumbnail_src())
                    .into(viewHolder.savedImage);
            System.out.println("response1122ff3344554444tt:  " + modelInstaItem.getNode().getThumbnail_src());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        viewHolder.relativinstastoryplace.setOnClickListener(view -> {
            if (modelInstaItem.getNode().getIs_video()) {

                Constants.INSTANCE.startInstaDownload(context,
                        "http://www.instagram.com/p/" + modelInstaItem.getNode().getShortcode() + "?__a=1&__d=dis"
                );

            } else {
                try {

                    Intent intent = new Intent(context, FullImageActivity.class);
                    intent.putExtra("myimgfile", modelInstaItem.getNode().getThumbnail_src());
                    context.startActivity(intent);

                } catch (Exception e) {
                    iUtils.ShowToast(context, context.getResources().getString(R.string.somth_video_wrong));
                    Log.e("Errorisnewis", e.getMessage());
                }

            }
        });


        viewHolder.downloadID.setOnClickListener(v -> Constants.INSTANCE.startInstaDownload(context,
                "http://www.instagram.com/p/" + modelInstaItem.getNode().getShortcode() + "?__a=1&__d=dis"
        ));


    }

    @Override
    public int getItemCount() {
        return storyModelInstaItemList == null ? 0 : storyModelInstaItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView savedImage;
        ImageView playIcon;
        ImageView downloadID;
        RelativeLayout relativinstastoryplace;
        LinearLayout linlayoutclickinsta;

        public ViewHolder(View itemView) {
            super(itemView);
            relativinstastoryplace = itemView.findViewById(R.id.relativinstastoryplace);
            userName = itemView.findViewById(R.id.profileUserName);
            savedImage = itemView.findViewById(R.id.storyimgview);
            playIcon = itemView.findViewById(R.id.storyVIDview);
            downloadID = itemView.findViewById(R.id.downloadID);
            linlayoutclickinsta = itemView.findViewById(R.id.linlayoutclickinsta);
        }
    }
}