package com.mordekai.poggtech.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private final int category_id;

    private String name;

    private final String description;

    private final String createdAt;

    private String error;

    private String icon;

    // Getters e Setters
    public int getCategory_id() {
        return category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getIcon() { return icon; }

    public void setIcon(String icon) { this.icon = icon; }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + category_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    protected Category(Parcel in) {
        category_id = in.readInt();
        name = in.readString();
        description = in.readString();
        createdAt = in.readString();
        icon = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(category_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(createdAt);
        dest.writeString(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
