package com.github.kiulian.downloader.downloader.request;

import com.github.kiulian.downloader.downloader.YoutubeCallback;

import java.util.HashMap;
import java.util.Map;

public abstract class Request<T extends Request<T, S>, S> {

    protected Map<String, String> headers;
    private YoutubeCallback<S> callback;
    private Integer maxRetries;
    private boolean async;

    public T maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return (T) this;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public T callback(YoutubeCallback<S> callback) {
        this.callback = callback;
        return (T) this;
    }

    public YoutubeCallback<S> getCallback() {
        return callback;
    }

    public T header(String key, String value) {
        if (this.headers == null) this.headers = new HashMap<>();
        this.headers.put(key, value);
        return (T) this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public T async() {
        this.async = true;
        return (T) this;
    }

    public boolean isAsync() {
        return async;
    }
}
