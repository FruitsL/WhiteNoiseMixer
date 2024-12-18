package com.example.whitenoisemixer.ui.library;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoisemixer.R;
import com.example.whitenoisemixer.ui.topmenu.TopMenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private final List<String> songs;

    public SongAdapter(List<String> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_songs, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        String fileName = songs.get(position);

        // 기본값 설정
        String title = fileName; // 기본값으로 파일명을 사용
        String artist = "Unknown Artist"; // 기본 아티스트 설정

        try {
            // res/raw에서 파일 경로 가져오기
            Context context = holder.itemView.getContext();
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(
                    context.getResources().getIdentifier(fileName, "raw", context.getPackageName())
            );

            if (afd == null) {
                throw new Exception("File not found: " + fileName);
            }

            // MediaMetadataRetriever로 메타데이터 읽기
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            // 메타데이터 추출
            String metaTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String metaArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            if (metaTitle != null && !metaTitle.isEmpty()) {
                title = metaTitle; // 메타데이터에서 제목 읽기
            }

            if (metaArtist != null && !metaArtist.isEmpty()) {
                artist = metaArtist; // 메타데이터에서 아티스트 읽기
            }

            retriever.release();
            afd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 제목과 아티스트 설정
        holder.songTitle.setText(title);
        holder.songArtist.setText(artist);
    }




    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView songArtist;
        ImageView albumImage;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            albumImage = itemView.findViewById(R.id.albumImageView);
        }
    }
}
