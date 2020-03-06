package com.mymusic.orvai.high_pitched_tone.EventBus;

import com.squareup.otto.Bus;

/**
 * Created by orvai on 2018-02-10.
 */

public final class BusProvider extends Bus{
    private static final Bus BUS = new Bus();

    public static Bus getInstance(){
        return BUS;
    }

    private BusProvider(){
    }
}
