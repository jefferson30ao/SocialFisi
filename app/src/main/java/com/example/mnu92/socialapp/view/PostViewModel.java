package com.example.mnu92.socialapp.view;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;

public class PostViewModel extends ViewModel {

    Uri mediaUri;

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }
}
