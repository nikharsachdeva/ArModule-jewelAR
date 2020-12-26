package com.raywenderlich.facespotter.BottomSheet;

import android.graphics.drawable.Drawable;

public class TryOnPojo {

    private Drawable image;
    private String type;

    public TryOnPojo() {
    }

    public TryOnPojo(Drawable image, String type) {
        this.image = image;
        this.type = type;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
