package xoho.com.cam;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String key;

    public Upload() {
        //empty constructor needed
    }
    public Upload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }



    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
