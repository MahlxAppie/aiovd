package com.infusiblecoder.allinonevideodownloader.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.models.SongInfo;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.SongHolder> {

    private final ArrayList<SongInfo> _songs;
    private final Context context;
    private OnItemClickListener mOnItemClickListener;

    public AudioAdapter(Context context, ArrayList<SongInfo> songs) {
        this.context = context;
        this._songs = songs;

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(context).inflate(R.layout.row_songs, viewGroup, false);
        return new SongHolder(myView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final SongHolder songHolder, @SuppressLint("RecyclerView") final int i) {
        final SongInfo s = _songs.get(i);

        try {

            int completeLength = _songs.get(i).getSongname().length();
            int sizeoflast_ = StringUtils.ordinalIndexOf(_songs.get(i).getSongname(), "_", 3) + 1;
            songHolder.tvSongName.setText(_songs.get(i).getSongname().substring(sizeoflast_, completeLength));
            songHolder.tvSongArtist.setText(_songs.get(i).getSongname().substring(sizeoflast_, completeLength));

        } catch (Exception e) {
            songHolder.tvSongName.setText("no name");
            songHolder.tvSongArtist.setText("no name");

        }


        songHolder.btnAction.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(songHolder.btnAction, v, s, i);

            }
        });


        songHolder.delte_song.setOnClickListener(v -> {


            @SuppressLint("NotifyDataSetChanged") DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        try {

                            new File(s.getSongUrl()).delete();
                            _songs.remove(i);
                            notifyDataSetChanged();
                            Toast.makeText(context, context.getString(R.string.delete), Toast.LENGTH_SHORT).show();


                        } catch (Exception e) {
                            Toast.makeText(context, "Error deleting", Toast.LENGTH_SHORT).show();

                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        dialog.dismiss();
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.are_sure_del)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(context.getString(R.string.btn_no), dialogClickListener).show();


        });
    }

    @Override
    public int getItemCount() {
        return _songs.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Button b, View view, SongInfo obj, int position);
    }

    public static class SongHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvSongArtist;
        Button btnAction;
        LinearLayout mLayout;
        ImageView delte_song;

        public SongHolder(View itemView) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            delte_song = itemView.findViewById(R.id.delte_song);
            tvSongArtist = itemView.findViewById(R.id.tvArtistName);
            btnAction = itemView.findViewById(R.id.btnPlay);
            mLayout = itemView.findViewById(R.id.mLayout);
        }
    }
}