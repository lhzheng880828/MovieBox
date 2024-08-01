package com.calvin.box.movie.utils

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.calvin.box.movie.ContextProvider
import kotlin.concurrent.Volatile

class Notify {
    //See CommonUI.kt
    //private var mDialog: AlertDialog? = null
    private var mToast: Toast? = null

    private object Loader {
        @Volatile
        var INSTANCE: Notify = Notify()
    }



  /*  private fun create(context: Context) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(xml,null)
        //val binding: ViewProgressBinding = ViewProgressBinding.inflate()
        mDialog = MaterialAlertDialogBuilder(context).setView(view).create()
        mDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        mDialog!!.show()
    }*/

    private fun makeText(message: String) {
        if (mToast != null) mToast!!.cancel()
        if (TextUtils.isEmpty(message)) return
        mToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG)
        mToast!!.show()
    }

    companion object {
        const val DEFAULT: String = "default"
        const val ID: Int = 9527
        private fun get(): Notify {
            return Loader.INSTANCE
        }

        fun createChannel() {
            val notifyMgr = NotificationManagerCompat.from(getContext())
            notifyMgr.createNotificationChannel(
                NotificationChannelCompat.Builder(
                    DEFAULT,
                    NotificationManagerCompat.IMPORTANCE_LOW
                ).setName("TV").build()
            )
        }

        fun getError(resId: Int, e: Throwable): String {
            if (TextUtils.isEmpty(e.message)) return ResUtil.getString(resId)
            return """
                 ${ResUtil.getString(resId)}
                 ${e.message}
                 """.trimIndent()
        }

        fun show(notification: Notification?) {
            if (ActivityCompat.checkSelfPermission(
                    getContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
            NotificationManagerCompat.from(getContext()).notify(ID, notification!!)
        }

        fun show(resId: Int) {
            if (resId != 0) show(ResUtil.getString(resId))
        }

        fun show(text: String?) {
            if (text != null) {
                get().makeText(text)
            }
        }

        fun progress(context: Context) {
           /* dismiss()
            get().create(context)*/
        }

        fun dismiss() {
           /* try {
                if (get().mDialog != null) get().mDialog!!.dismiss()
            } catch (ignored: Exception) {
            }*/
        }
        private fun getContext():Context{
            return ContextProvider.context as Context
        }
    }

}
