package com.github.kiulian.downloader.downloader.request;

import com.github.kiulian.downloader.model.playlist.PlaylistInfo;

public class RequestPlaylistInfo extends Request<RequestPlaylistInfo, PlaylistInfo> {

    private final String playlistId;

    public RequestPlaylistInfo(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistId() {
        return playlistId;
    }
}