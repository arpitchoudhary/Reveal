package com.lba.poc.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by smohanthy on 1/4/18.
 */

public class ModuleItem implements Parcelable {

    public String imagePath;
    public String overlayImagePath;
    public String template_type;

    public ModuleItem(Parcel in) {
        imagePath = in.readString();
        overlayImagePath = in.readString();
        template_type = in.readString();
    }

    public ModuleItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
        dest.writeString(overlayImagePath);
        dest.writeString(template_type);
    }

    public static final Creator<ModuleItem> CREATOR = new Creator<ModuleItem>() {
        @Override
        public ModuleItem createFromParcel(Parcel in) {
            return new ModuleItem(in);
        }

        @Override
        public ModuleItem[] newArray(int size) {
            return new ModuleItem[size];
        }
    };
}
