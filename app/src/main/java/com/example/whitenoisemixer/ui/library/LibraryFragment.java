package com.example.whitenoisemixer.ui.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoisemixer.R;
import com.example.whitenoisemixer.databinding.FragmentLibraryBinding;
import com.example.whitenoisemixer.ui.topmenu.TopMenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibraryFragment extends Fragment {

    public static final String PREFS_NAME = "CurrentItemsPrefs";
    public static final String CURRENT_ITEMS_KEY = "current_items";

    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // RecyclerView 초기화
        RecyclerView recyclerView = view.findViewById(R.id.library_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 재생목록 데이터 로드 및 어댑터 연결
        List<Playlist> playlists = loadPlaylists();
        LibraryAdapter adapter = new LibraryAdapter(playlists, sharedPreferences);
        recyclerView.setAdapter(adapter);

        return view;
    }


    private List<Playlist> loadPlaylists() {
        List<Playlist> playlists = new ArrayList<>();

        // SharedPreferences에서 재생목록 이름 가져오기
        Set<String> playlistNames = sharedPreferences.getStringSet("playlists", new HashSet<>());

        if (playlistNames == null || playlistNames.isEmpty()) {
            return playlists; // 비어 있는 목록 반환
        }

        for (String playlistName : playlistNames) {
            // 재생목록에 속한 곡 데이터 가져오기
            Set<String> songFiles = sharedPreferences.getStringSet(playlistName, new HashSet<>());

            if (songFiles != null && !songFiles.isEmpty()) {
                playlists.add(new Playlist(playlistName, new ArrayList<>(songFiles)));
            }
        }

        return playlists;
    }


    private TopMenuItem getTopMenuItemFromFile(String fileName) {
        try {
            int resId = getResources().getIdentifier(fileName, "raw", requireContext().getPackageName());
            if (resId == 0) {
                return null; // 파일이 존재하지 않음
            }

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getResources().openRawResourceFd(resId).getFileDescriptor());

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            if (title == null || title.isEmpty()) title = "Unknown Title";
            if (artist == null || artist.isEmpty()) artist = "Unknown Artist";

            retriever.release();

            return new TopMenuItem(title, artist, android.R.drawable.ic_menu_report_image, 0, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
