package com.almexe.lingvaproject.utils;

import com.almexe.lingvaproject.pages.LessonTenWordFragment;

public class UpdateInfo {
    public String mainTextView;
    public String translateText;
    public int count;

    public UpdateInfo(LessonTenWordFragment lessonTenWordFragment) {
        this.mainTextView = lessonTenWordFragment.mainTextView;
        this.translateText = lessonTenWordFragment.translateText;
        this.count = lessonTenWordFragment.count;
    }

    public UpdateInfo(){}

    public String getMainTextView() {
        return mainTextView;
    }

    public void setMainTextView(String mainTextView) {
        this.mainTextView = mainTextView;
    }

    public String getTranslateText() {
        return translateText;
    }

    public void setTranslateText(String translateText) {
        this.translateText = translateText;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
