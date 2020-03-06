package com.mymusic.orvai.high_pitched_tone.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mymusic.orvai.high_pitched_tone.fragment.Lecture;
import com.mymusic.orvai.high_pitched_tone.fragment.Board;
import com.mymusic.orvai.high_pitched_tone.fragment.Chat;
import com.mymusic.orvai.high_pitched_tone.fragment.Mic;
import com.mymusic.orvai.high_pitched_tone.fragment.School;

/**
 * Created by orvai on 2018-01-25.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {


    int tabCount;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Lecture tab1 = new Lecture();
                return tab1;
            case 1:
                Board tab2 = new Board();
                return tab2;
            case 2:
                Chat tab3 = new Chat();
                return tab3;
            case 3:
                Mic tab4 = new Mic();
                return tab4;
            case 4:
                School tab5 = new School();
                return tab5;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}