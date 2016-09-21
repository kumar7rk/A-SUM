package com.geeky7.rohit.location;

import android.graphics.drawable.Drawable;

/**
 * Created by Rohit on 16/09/2016.
 */
public class DataObject {
    private String mText1;
    private String mText2;
    private Drawable mImage;
    private Drawable[] mImageD;

    public DataObject(String text1, String text2){
        mText1 = text1;
        mText2 = text2;
    }
    public DataObject(String text1, Drawable image){
        mText1 = text1;
        mImage = image;
    }

    public DataObject(String text1){
        mText1 = text1;
    }


    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public Drawable getmImage() {
        return mImage;
    }

    public void setmImage(Drawable image) {
        this.mImage = image;
    }

    public Drawable[] getmImageD() {
        return mImageD;
    }

    public void setmImageD(Drawable[] image) {
        this.mImageD = image;
    }
}