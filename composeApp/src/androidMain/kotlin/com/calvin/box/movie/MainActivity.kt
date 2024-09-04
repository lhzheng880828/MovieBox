package com.calvin.box.movie

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.calvin.box.movie.model.Fruittie
import com.calvin.box.movie.screens.fruitties.FruittieItem
import com.calvin.box.movie.xlab.BottomSheetExample

class MainActivity : ComponentActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //MovieApp()
            //ThemeApp()
            //SearchApp()
            MyMovieApp()
            //MovieDetailScreen()
            //com.calvin.box.movie.xlab.SearchScreen()
            //MovieFilterScreen()
            //MovieSearchScreen()
            //PhotosGrid()
            //MovieHistoryScreen()
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