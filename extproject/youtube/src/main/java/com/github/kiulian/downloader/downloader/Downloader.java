package com.github.kiulian.downloader.downloader;

import com.github.kiulian.downloader.downloader.request.RequestWebpage;
import com.github.kiulian.downloader.downloader.response.Response;

public interface Downloader {

    Response<String> downloadWebpage(RequestWebpage request);
}
