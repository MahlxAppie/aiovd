package com.infusiblecoder.allinonevideodownloader.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.interfaces.UserListInStoryListner;
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelUsrTray;
import com.infusiblecoder.allinonevideodownloader.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryUsersListAdapter extends RecyclerView.Adapter<StoryUsersListAdapter.ViewHolder> implements Filterable {
    UserListInStoryListner userListInStoryListner;
    private final Context context;
    private ArrayList<ModelUsrTray> userListIninstaStory;
    private final ArrayList<ModelUsrTray> userListIninstaStoryOrignal;


    public StoryUsersListAdapter(Context context, ArrayList<ModelUsrTray> userListIninstaStory, UserListInStoryListner userListInStoryListner) {
        this.context = context;
        this.userListIninstaStory = userListIninstaStory;
        this.userListInStoryListner = userListInStoryListner;
        this.userListIninstaStoryOrignal = userListIninstaStory;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_userlist_placeholder, viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        try {
            if (userListIninstaStory.get(position).getMedia_count() > 0) {


                try {
                    viewHolder.rec_item_username.setText(userListIninstaStory.get(position).getUser().getUsername() + "");
                } catch (Exception e) {
                    System.out.println("errorisnnnnnn: " + e.getMessage());

                    try {

                        viewHolder.rec_item_username.setText(userListIninstaStory.get(position).getOwner().getUsername() + "");
                    } catch (Exception e1) {
                        System.out.println("errorisnnnnnn: " + e1.getMessage());
                    }
                }


                try {
                    viewHolder.rec_user_fullname.setText(userListIninstaStory.get(position).getUser().getFull_name() + "");

                } catch (Exception e) {
                    System.out.println("errorisnnnnnn: " + e.getMessage());
                }
                try {
                    GlideApp.with(context).load(userListIninstaStory.get(position).getUser().getProfile_pic_url() + "")
                            .thumbnail(0.2f).into(viewHolder.story_item_imgview);
                } catch (Exception e) {
                    System.out.println("errorisnnnnnn: " + e.getMessage());

                    try {
                        GlideApp.with(context).load(userListIninstaStory.get(position).getOwner().getProfile_pic_url() + "")
                                .thumbnail(0.2f).into(viewHolder.story_item_imgview);
                    } catch (Exception e1) {
                        System.out.println("errorisnnnnnn: " + e1.getMessage());
                    }


                }
                viewHolder.rec_item_relativelative_layot.setOnClickListener(view -> {


                    System.out.println("response1122ff334455:   workingggg");
                    userListInStoryListner.onclickUserStoryListeItem(position, userListIninstaStory.get(position));


                });

                try {
                    GlideApp.with(context).load(userListIninstaStory.get(position).getUser().getProfile_pic_url())
                            .thumbnail(0.2f).into(viewHolder.story_item_imgview);
                } catch (Exception e) {
                    System.out.println("errorisnnnnnn: " + e.getMessage());
                }

            }

        } catch (Exception e) {
            System.out.println("errorisnnnnnn: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return userListIninstaStory == null ? 0 : userListIninstaStory.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userListIninstaStory = (ArrayList<ModelUsrTray>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ModelUsrTray> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = userListIninstaStoryOrignal;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<ModelUsrTray> getFilteredResults(String constraint) {
        List<ModelUsrTray> results = new ArrayList<>();

        for (ModelUsrTray item : userListIninstaStoryOrignal) {
            if (item.getUser().getUsername().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout rec_item_relativelative_layot;
        private final CircleImageView story_item_imgview;
        private final TextView rec_item_username;
        private final TextView rec_user_fullname;

        public ViewHolder(View view) {
            super(view);

            rec_item_relativelative_layot = view.findViewById(R.id.rec_item_relativelative_layot);
            story_item_imgview = view.findViewById(R.id.story_item_imgview);
            rec_item_username = view.findViewById(R.id.rec_item_username);
            rec_user_fullname = view.findViewById(R.id.rec_user_fullname);

        }
    }
}