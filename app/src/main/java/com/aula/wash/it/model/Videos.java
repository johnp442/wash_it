package com.aula.wash.it.model;

import android.net.Uri;


public class Videos {
    private String tituloVideo;
    private String descricaoVideo;
    private Uri videoUri;

    public Videos(String tituloVideo, String descricaoVideo, Uri videoUri) {
        this.tituloVideo = tituloVideo;
        this.descricaoVideo = descricaoVideo;
        this.videoUri = videoUri;
    }

    public String getTituloVideo() {
        return tituloVideo;
    }

    public void setTituloVideo(String tituloVideo) {
        this.tituloVideo = tituloVideo;
    }

    public String getDescricaoVideo() {
        return descricaoVideo;
    }

    public void setDescricaoVideo(String descricaoVideo) {
        this.descricaoVideo = descricaoVideo;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }
}
