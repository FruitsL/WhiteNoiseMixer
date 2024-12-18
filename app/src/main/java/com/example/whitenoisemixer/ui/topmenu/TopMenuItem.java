package com.example.whitenoisemixer.ui.topmenu;

public class TopMenuItem {
    private String title;
    private String description;
    private int imageResId;
    private int imageBtnId;

    private String fileName;

    public TopMenuItem(String title, String description, int imageResId, int imageBtnId, String fileName) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.imageBtnId = imageBtnId;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getImageBtnId() { return imageBtnId; }

    public String getFileName() { return fileName; }
}
