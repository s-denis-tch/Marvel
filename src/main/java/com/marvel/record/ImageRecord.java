package com.marvel.record;

import com.marvel.entity.Image;

public class ImageRecord {

    private String name;
    private String type;
    private byte[] data;

    public ImageRecord() {
    }

    public ImageRecord(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public static ImageRecord from(Image image) {
        return new ImageRecord(image.getName(), image.getType(), image.getData());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
