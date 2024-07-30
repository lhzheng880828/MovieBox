package com.calvin.box.movie.xlab

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/30
 */
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetExample() {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,

    )
    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheetContent() },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        showBottomSheet = true
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }
                ) {
                    Text("Show Bottom Sheet")
                }
            }
        }
    )
}

@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "This is the Bottom Sheet content",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You can add any content here",
            style = MaterialTheme.typography.body1
        )
    }
}