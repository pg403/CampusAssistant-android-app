package com.example.simon.campusassistant;

/**
 * Created by Hairong Wang.
 */

import android.content.Context;
public class MyAdapter {

    private Context context;
    private MainActivity main;

    public MyAdapter(Context context){
        this.context=context;
        main=(MainActivity) context;
    }
}