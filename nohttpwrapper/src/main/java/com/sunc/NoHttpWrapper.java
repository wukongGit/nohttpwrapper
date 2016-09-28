package com.sunc;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;

/**
 * Created by suncheng on 2016/9/28.
 */
public class NoHttpWrapper {
    public static void init(Application application) {
        NoHttp.initialize(application);
    }
}
