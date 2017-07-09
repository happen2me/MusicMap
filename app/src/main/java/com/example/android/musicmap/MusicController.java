package com.example.android.musicmap;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by 申源春 on 2017/7/6.
 */

public class MusicController extends MediaController {



    public MusicController(Context context) {
        super(context);
    }

    /**
     * Remove the controller from the screen.
     */
    @Override
    public void hide() {
    }


}
