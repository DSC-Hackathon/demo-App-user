package com.floating_cloud.event_point_user;

public interface ApiCallback<T> {
    void onSuccess(T result);
    void onFailure(Throwable t);
}