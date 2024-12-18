package com.example.whitenoisemixer.ui.library;

import com.example.whitenoisemixer.ui.topmenu.TopMenuItem;

import java.util.List;

public class Playlist {
    private String title;
    private List<String> fileName;

    public Playlist(String title, List<String> fileName) {
        this.title = title;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSongs() {
        return fileName;
    }
}

