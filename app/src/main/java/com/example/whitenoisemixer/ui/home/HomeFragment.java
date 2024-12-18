package com.example.whitenoisemixer.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whitenoisemixer.R;
import com.example.whitenoisemixer.databinding.FragmentHomeBinding;
import com.example.whitenoisemixer.ui.topmenu.MenuAdapter;
import com.example.whitenoisemixer.ui.topmenu.TopMenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MenuAdapter menuAdapter;
    private List<TopMenuItem> menuItems;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "CurrentItemsPrefs";
    private static final String CURRENT_ITEMS_KEY = "current_items";

    private String currentCategory;

    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        binding.addPlaylistButton.setOnClickListener(v -> showAddPlaylistDialog());

        setupTabLayout();
        setupRecyclerView();
        playCurrentCategoryItems();
        loadMenuItems("current");

        return root;
    }

    private void showAddPlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("플레이리스트 추가");

        // 입력란 생성
        EditText input = new EditText(requireContext());
        input.setHint("재생목록 이름을 입력하세요");
        builder.setView(input);

        // 추가 버튼
        builder.setPositiveButton("추가", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                addToLibrary(playlistName);
                Toast.makeText(requireContext(), "플레이리스트가 추가되었습니다!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "재생목록 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼
        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    private void addToLibrary(String playlistName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 현재 재생 중인 current 카테고리의 파일 불러오기
        Set<String> currentItems = getCurrentItems();

        if (currentItems == null || currentItems.isEmpty()) {
            Toast.makeText(requireContext(), "현재 재생목록이 비어 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 기존 재생목록 이름 로드
        Set<String> playlistNames = sharedPreferences.getStringSet("playlists", new HashSet<>());
        if (playlistNames == null) {
            playlistNames = new HashSet<>();
        } else {
            // 복사본 생성하여 수정
            playlistNames = new HashSet<>(playlistNames);
        }

        // 새로운 재생목록 추가
        if (!playlistNames.add(playlistName)) {
            Toast.makeText(requireContext(), "이미 존재하는 재생목록 이름입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 업데이트된 재생목록 저장
        editor.putStringSet("playlists", playlistNames);

        // 새로운 재생목록과 곡 저장
        editor.putStringSet(playlistName, new HashSet<>(currentItems));
        editor.apply();

        Toast.makeText(requireContext(), playlistName + " 재생목록이 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }


    private void playCurrentCategoryItems() {
        Set<String> currentItems = getCurrentItems();

        for (String itemKey : currentItems) {
            String[] parts = itemKey.split("_", 2);
            if (parts.length == 2) {
                String fileName = parts[1]; // 파일명 가져오기
                startPlayback(fileName); // 파일명으로 재생
            }
        }
    }

    private void setupTabLayout() {
        @SuppressLint("RestrictedApi") Menu menu = new MenuBuilder(requireContext());
        MenuInflater inflater = new MenuInflater(requireContext());
        inflater.inflate(R.menu.top_home_menu, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            com.google.android.material.tabs.TabLayout.Tab tab = binding.tabLayout.newTab()
                    .setText(item.getTitle());
            binding.tabLayout.addTab(tab);
        }

        // 탭 클릭 이벤트 처리
        binding.tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                // Tab에서 텍스트(title) 가져오기
                String menuTitle = (String) tab.getText();

                // strings.xml의 값과 비교하여 로직 실행
                if (menuTitle != null) {
                    if (menuTitle.equals(getString(R.string.top_river))) {
                        loadMenuItems("river"); // river 로직 실행
                    } else if (menuTitle.equals(getString(R.string.top_bird))) {
                        loadMenuItems("bird"); // bird 로직 실행
                    } else if (menuTitle.equals(getString(R.string.top_forest))) {
                        loadMenuItems("forest"); // forest 로직 실행
                    } else if (menuTitle.equals(getString(R.string.top_wind))) {
                        loadMenuItems("wind"); // wind 로직 실행
                    } else if (menuTitle.equals(getString(R.string.top_current))) {
                        loadMenuItems("current"); // current 로직 실행
                    } else {
                        Toast.makeText(getContext(), "Invalid menu selected.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
        });

        // 초기 탭 데이터 로드
        if (binding.tabLayout.getTabCount() > 0) {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0));
        }
    }

    private void setupRecyclerView() {
        menuItems = new ArrayList<>();
        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapter = new MenuAdapter(menuItems, item -> {
            Set<String> currentItems = getCurrentItems();
            String itemKey = item.getFileName();

            if ("current".equalsIgnoreCase(currentCategory)) {
                // `current`에서 삭제 처리
                if (currentItems.remove(itemKey)) {
                    saveCurrentItems(currentItems);
                    loadMenuItems(currentCategory);

                    stopPlayback(item.getFileName());

                    Toast.makeText(getContext(), item.getTitle() + " removed from current.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Item not found in current.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 다른 카테고리에서 `current`로 추가
                if (currentItems.add(itemKey)) {
                    saveCurrentItems(currentItems);
                    loadMenuItems(currentCategory);

                    startPlayback(item.getFileName());

                    Toast.makeText(getContext(), item.getTitle() + " added to current.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Item already in current.", Toast.LENGTH_SHORT).show();
                }
            }

            menuAdapter.notifyDataSetChanged();
        });

        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycleView.setAdapter(menuAdapter);
    }



    private Set<String> getCurrentItems() {
        return sharedPreferences.getStringSet(CURRENT_ITEMS_KEY, new HashSet<>());
    }

    private void saveCurrentItems(Set<String> currentItems) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(CURRENT_ITEMS_KEY, currentItems);
        editor.apply();
    }

    private String generateItemKey(TopMenuItem item) {
        // 아이템을 고유하게 식별하기 위한 키 생성 (카테고리와 제목 결합)
        return item.getFileName();
    }

    private void loadMenuItems(String category) {
        menuItems.clear();
        currentCategory = category;

        if ("current".equalsIgnoreCase(category)) {
            Set<String> currentItems = getCurrentItems();
            for (String fileName : currentItems) {
                try {
                    // raw 디렉토리에서 파일 이름으로 리소스 ID 가져오기
                    int resId = getResources().getIdentifier(fileName, "raw", requireContext().getPackageName());

                    // 리소스를 내부 저장소로 복사
                    File tempFile = new File(getContext().getCacheDir(), fileName + ".ogg");
                    try (InputStream inputStream = getResources().openRawResource(resId);
                         FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }

                    // MediaMetadataRetriever를 이용해 메타데이터 가져오기
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(tempFile.getAbsolutePath());

                    String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    // 기본값 설정
                    if (title == null || title.isEmpty()) {
                        title = "Unknown Title";
                    }
                    if (artist == null || artist.isEmpty()) {
                        artist = "Unknown Artist";
                    }

                    // 리스트에 추가
                    menuItems.add(new TopMenuItem(
                            title,
                            artist,
                            android.R.drawable.ic_menu_report_image,
                            android.R.drawable.ic_delete,
                            fileName // 파일명을 그대로 저장
                    ));

                    retriever.release();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error loading file: " + fileName, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            try {
                Field[] rawFiles = R.raw.class.getFields();

                for (Field field : rawFiles) {
                    String fileName = field.getName().toLowerCase();
                    if (fileName.contains(category.toLowerCase())) {
                        int resId = field.getInt(null);

                        // 리소스를 내부 저장소로 복사
                        File tempFile = new File(getContext().getCacheDir(), fileName + ".ogg");
                        try (InputStream inputStream = getResources().openRawResource(resId);
                             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                        }

                        // MediaMetadataRetriever를 이용해 메타데이터 가져오기
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(tempFile.getAbsolutePath());

                        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                        // 기본값 설정
                        if (title == null || title.isEmpty()) {
                            title = "Unknown Title";
                        }
                        if (artist == null || artist.isEmpty()) {
                            artist = "Unknown Artist";
                        }

                        // 리스트에 추가
                        TopMenuItem newItem = new TopMenuItem(
                                title,
                                artist,
                                android.R.drawable.ic_menu_report_image,
                                android.R.drawable.ic_input_add,
                                fileName
                        );

                        // `current`에 포함된 아이템 제외
                        String itemKey = generateItemKey(newItem);
                        if (!getCurrentItems().contains(itemKey)) {
                            menuItems.add(newItem);
                        }

                        retriever.release();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading files for category: " + category, Toast.LENGTH_SHORT).show();
            }
        }

        menuAdapter.notifyDataSetChanged();
    }

    private void startPlayback(String fileName) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            // raw 리소스 ID 가져오기
            int resId = getResources().getIdentifier(fileName, "raw", requireContext().getPackageName());
            if (resId == 0) {
                Toast.makeText(getContext(), "File not found: " + fileName, Toast.LENGTH_SHORT).show();
                return;
            }

            mediaPlayer = MediaPlayer.create(requireContext(), resId);
            mediaPlayer.setLooping(true); // 반복 재생 설정
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error playing file: " + fileName, Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlayback(String fileName) {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace(); // 예외 로그를 남김
            } finally {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}