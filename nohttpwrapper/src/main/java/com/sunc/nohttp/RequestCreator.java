package com.sunc.nohttp;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;

import org.json.JSONObject;

public class RequestCreator {

    public static Request<JSONObject> createGetRequest(String url) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.GET);
        appendBaseParams(request);
        return  request;
    }

    public static Request<JSONObject> createPostRequest(String url) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.POST);
        appendBaseParams(request);
        return  request;
    }

    private static <T> void appendBaseParams(Request<T> request) {
        request.add("tssign", System.currentTimeMillis());
    }

}
