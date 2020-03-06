package com.mymusic.orvai.high_pitched_tone.EventBus;

public class Event_bus_musicplay {

    public final String music_title;
    public final int music_data;

    public Event_bus_musicplay(String music_title, int music_data) {
        this.music_title = music_title;
        this.music_data = music_data;
    }

}
