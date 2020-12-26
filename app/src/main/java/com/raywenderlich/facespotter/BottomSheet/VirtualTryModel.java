package com.raywenderlich.facespotter.BottomSheet;

public class VirtualTryModel {

    private Integer id;
    private String image;
    private String type;

    public VirtualTryModel(Integer id, String image, String type) {
        this.id = id;
        this.image = image;
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
