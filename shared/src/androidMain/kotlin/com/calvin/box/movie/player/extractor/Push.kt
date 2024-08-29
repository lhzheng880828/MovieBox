package com.calvin.box.movie.player.extractor

import android.os.SystemClock
import com.calvin.box.movie.player.Extractor

/*import com.fongmi.android.tv.App;
import com.fongmi.android.tv.ui.activity.VideoActivity;*/
class Push : Extractor {
    override fun match(scheme: String?, host: String?): Boolean {
        return "push" == scheme
    }

    @Throws(Exception::class)
    override fun fetch(url: String?): String? {
        // if (App.activity() != null) VideoActivity.start(App.activity(), url.substring(7));
        SystemClock.sleep(500)
        return ""
    }

    override fun stop() {
    }

    override fun exit() {
    }
}
