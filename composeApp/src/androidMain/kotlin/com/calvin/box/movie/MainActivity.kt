package com.calvin.box.movie

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.documentfile.provider.DocumentFile
import com.calvin.box.movie.model.Fruittie
import com.calvin.box.movie.screens.fruitties.FruittieItem
import com.calvin.box.movie.utils.FileUtil
import com.calvin.box.movie.utils.ResUtil
import com.calvin.box.movie.xlab.BottomSheetExample
import com.calvin.box.movie.xlab.qr.QrAppHome
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setTransparent(this)
        setContent {
            //MovieApp()
            //ThemeApp()
            //SearchApp()
            //MyMovieApp()
            //MovieDetailScreen()
            //com.calvin.box.movie.xlab.SearchScreen()
            //MovieFilterScreen()
            //MovieSearchScreen()
            //PhotosGrid()
            //MovieHistoryScreen()
            QrAppHome()
        }


       /* resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.also { uri ->
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    createTvFolderAndAliyunFile(uri)
                }
            }
        }
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        resultLauncher.launch(intent)*/
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
       // refreshWall()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
       // refreshWall()
    }

    private fun setTransparent(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        activity.window.statusBarColor = Color.TRANSPARENT
    }

    private fun refreshWall() {
        try {
            //if (!customWall()) return
            val file: File = FileUtil.getWall(Setting.wall)
            if (file.exists() && file.length() > 0) window.setBackgroundDrawable(
                Drawable.createFromPath(
                    file.absolutePath
                )
            )
            else window.setBackgroundDrawableResource(ResUtil.getDrawable(file.name))
        } catch (e: Exception) {
            window.setBackgroundDrawableResource(R.drawable.wallpaper_1)
        }
    }

    private fun createTvFolderAndAliyunFile(rootUri: Uri) {
        val documentFile = DocumentFile.fromTreeUri(this, rootUri)
        documentFile?.let {
            val tvFolder = it.createDirectory("TV")
            tvFolder?.createFile("text/plain", ".aliyun")
        }
    }

}


@Preview(name = "Light Mode", heightDp = 100, device = Devices.PIXEL_4)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",device = Devices.PIXEL_4)
@Composable
fun AppAndroidPreview() {
    //MovieApp()
    FruittieItem(item = Fruittie(name="apple", fullName = "Red apple", calories = "apple calories"), onAddToCart = {})
}

@Preview(name = "Light Mode",  device = Devices.PIXEL_4)
@Composable
fun BottomSheetPreview(){
    BottomSheetExample()
}