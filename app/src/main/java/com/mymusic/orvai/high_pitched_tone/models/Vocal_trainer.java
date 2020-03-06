package com.mymusic.orvai.high_pitched_tone.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by orvai on 2018-01-25.
 */

public class Vocal_trainer extends ExpandableGroup {

    private int image;

    public Vocal_trainer(String title, int image, List<Course> items) {
        super(title, items);
        this.image = image;
    }

    public int getImage() {
        return image;
    }
}
