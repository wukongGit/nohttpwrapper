package com.sunc.nohttp;

public interface OnDataCallBack<T extends Object> {
    void onSuccess(T result, int statusCode, String message);

    void onFailed(Exception e, int responseCode);

}