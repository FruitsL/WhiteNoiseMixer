package com.example.whitenoisemixer.ui.library;

import static com.example.whitenoisemixer.ui.library.LibraryFragment.CURRENT_ITEMS_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoisemixer.R;
import com.example.whitenoisemixer.ui.topmenu.TopMenuItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.PlaylistViewHolder> {
    private final List<Playlist> playlists;
    private final SharedPreferences sharedPreferences;

    public LibraryAdapter(List<Playlist> playlists, SharedPreferences sharedPreferences) {
        this.playlists = playlists;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_items, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        // 태그를 사용하여 고유 데이터 저장
        holder.itemView.setTag(playlist);

        holder.playlistTitle.setText(playlist.getTitle());

        SongAdapter songAdapter = new SongAdapter(playlist.getSongs());
        holder.songsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.songsRecyclerView.setAdapter(songAdapter);

        holder.songsRecyclerView.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (playlist.getSongs().isEmpty()) {
                Toast.makeText(v.getContext(), "이 재생목록에는 곡이 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isVisible = holder.songsRecyclerView.getVisibility() == View.VISIBLE;
            holder.songsRecyclerView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });

        holder.expandButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Set<String> newCurrentItems = new HashSet<>(playlist.getSongs());
            editor.putStringSet(CURRENT_ITEMS_KEY, newCurrentItems);
            editor.apply();

            Toast.makeText(v.getContext(), playlist.getTitle() + " 곡들이 현재 재생목록으로 설정되었습니다.", Toast.LENGTH_SHORT).show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            Playlist itemToRemove = (Playlist) holder.itemView.getTag(); // 정확한 데이터 참조
            int indexToRemove = playlists.indexOf(itemToRemove);

            if (indexToRemove != -1) {
                // 데이터 삭제
                playlists.remove(indexToRemove);

                // SharedPreferences에서 삭제
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Set<String> playlistNames = sharedPreferences.getStringSet("playlists", new HashSet<>());
                if (playlistNames != null) {
                    playlistNames.remove(itemToRemove.getTitle());
                    editor.putStringSet("playlists", playlistNames);
                    editor.remove(itemToRemove.getTitle());
                    editor.apply();
                }

                // RecyclerView 업데이트
                notifyItemRemoved(indexToRemove);
                Toast.makeText(v.getContext(), itemToRemove.getTitle() + " 재생목록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView playlistTitle;
        Button expandButton;
        Button deleteButton;
        RecyclerView songsRecyclerView;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistTitle = itemView.findViewById(R.id.playlist_title);
            expandButton = itemView.findViewById(R.id.button_expand);
            deleteButton = itemView.findViewById(R.id.button_delete);
            songsRecyclerView = itemView.findViewById(R.id.songs_recycler_view);
        }
    }
}
