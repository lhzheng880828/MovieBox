package com.github.catvod.crawler;

import android.content.Context;
import android.util.Log;

public class SpiderNull extends Spider {
    @Override
    public void init(Context context) throws Exception {
        super.init(context);
        Log.d("SpiderNull", "init");
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        Log.d("SpiderNull", "homeContent, filter: "+filter);
        return super.homeContent(filter);
    }
}

