package com.calvin.box.movie.feature.followed

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/6
 */
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.calvin.box.movie.bean.Keep
import com.calvin.box.movie.feature.history.DeleteConfirmationDialog
import com.calvin.box.movie.navigation.LocalNavigation
import com.calvin.box.movie.theme.BackHandler
import com.calvin.box.movie.utils.UrlProcessor
import io.github.aakira.napier.Napier
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.request.header
import io.ktor.client.utils.CacheControl
import kmpImagePicker.clickableSingle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Photo(
val id:Int,
//val url:String="https://picsum.photos/seed/${(0..100000).random()}/256/256"
val keep: Keep,
)

class FollowedScreen:Screen
{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val enableSync = false
        val nv = LocalNavigation.current
        val viewModel: KeepScreenModel = getScreenModel()
        val keeps by viewModel.keeps.collectAsState()
        val  photos by remember { derivedStateOf { keeps.mapIndexed { index, keep -> Photo(index, keep) }} }
        val  selectedIds:MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet())}
        val inSelectionMode by  viewModel.inSelectionMode.collectAsState()
        val hasSelectedIds by remember { derivedStateOf { selectedIds.value.isNotEmpty()}}
        var showDeleteDialog by remember { mutableStateOf(false) }
        val state = rememberLazyGridState()
        val autoScrollSpeed = remember { mutableStateOf(0f)}
        BackHandler{
            if (inSelectionMode) {
                viewModel.exitSelectionMode()
                selectedIds.value = emptySet()
            } else {
                nv.back()
            }
        }
        if(hasSelectedIds){
            if(!inSelectionMode){
                viewModel.toggleSelectionMode()
            }
        }
        LaunchedEffect(autoScrollSpeed.value){
            if(autoScrollSpeed.value !=0f){
                while(isActive){
                    state.scrollBy(autoScrollSpeed.value)
                    delay(10)
                }
            }
        }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("收藏", fontSize = 24.sp) },
                    actions = {
                        if (inSelectionMode) {
                            IconButton(
                                onClick = {
                                    photos.onEach {
                                        selectedIds.value += it.id
                                    }
                                },
                            ) {
                                Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                            }
                            IconButton(onClick = {
                                photos.onEach {
                                    selectedIds.value -= it.id
                                }
                            },
                            ) {
                                Icon(Icons.Default.Deselect, contentDescription = "Deselect All")
                            }
                            IconButton(onClick = {
                                if (selectedIds.value.isNotEmpty()) {
                                    showDeleteDialog = true
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("没有选中的收藏项")
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        } else {
                            if(enableSync){
                                IconButton(onClick = {
                                    Napier.d { "xbox.history, sync clicked"}
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Sync Button"
                                    )
                                }
                            }
                            IconButton(onClick = {
                                Napier.d { "xbox.history, delete Btn clicked" }
                                viewModel.toggleSelectionMode()

                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Button"
                                )

                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyVerticalGrid(
                state = state,
                columns =GridCells.Adaptive(100.dp),
                verticalArrangement =Arrangement.spacedBy(4.dp),
                horizontalArrangement =Arrangement.spacedBy(4.dp),
                modifier =Modifier.photoGridDragHandler(
                    lazyGridState = state,
                    haptics =LocalHapticFeedback.current,
                    selectedIds = selectedIds,
                    autoScrollSpeed = autoScrollSpeed,
                    autoScrollThreshold =with(LocalDensity.current){40.dp.toPx()}
                )
                    .padding(innerPadding)
                    .fillMaxSize()

            ){
                items(photos, key ={ it.id }){ photo ->
                    val selected by remember { derivedStateOf { selectedIds.value.contains(photo.id)}}
                    ImageItem(
                        photo, inSelectionMode, selected,
                        onRadioBtnClick = {
                            //toggle，it is current selected state
                            if(it){
                                selectedIds.value -= photo.id
                            }else{
                                selectedIds.value += photo.id
                            }
                        },
                        Modifier
                            .semantics {
                                if(!inSelectionMode){
                                    onLongClick("Select"){
                                        viewModel.toggleSelectionMode()
                                        selectedIds.value += photo.id
                                        true
                                    }
                                }
                            }
                            .then(if(inSelectionMode){
                                Modifier.toggleable(
                                    value = selected,
                                    interactionSource = remember {MutableInteractionSource()},
                                    indication =null,// do not show a ripple
                                    onValueChange ={
                                        if(it){
                                            selectedIds.value += photo.id
                                        }else{
                                            selectedIds.value -= photo.id
                                        }
                                    }
                                )
                            }else Modifier)
                    )
                }
            }
            if (showDeleteDialog) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        val filtered =  photos.filter { selectedIds.value.contains(it.id) }
                        viewModel.setSelectedItems( filtered.map { it.keep })
                        viewModel.deleteSelectedItems()
                        showDeleteDialog = false
                        viewModel.toggleSelectionMode()
                        selectedIds.value = emptySet()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }


}

}

fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState,
    haptics: HapticFeedback,
    selectedIds: MutableState<Set<Int>>,
    autoScrollSpeed: MutableState<Float>,
    autoScrollThreshold: Float
)= pointerInput(Unit){
    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset):Int?=
        layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round()- itemInfo.offset)
        }?.key as?Int

    var initialKey:Int?=null
    var currentKey:Int?=null
    detectDragGesturesAfterLongPress(
        onDragStart ={ offset ->
            lazyGridState.gridItemKeyAtPosition(offset)?.let{ key ->
                if(!selectedIds.value.contains(key)){
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    initialKey = key
                    currentKey = key
                    selectedIds.value += key

                }
            }
        },
        onDragCancel ={ initialKey =null; autoScrollSpeed.value =0f},
        onDragEnd ={ initialKey =null; autoScrollSpeed.value =0f},
        onDrag ={ change, _ ->
            if(initialKey !=null){
                val distFromBottom =
                    lazyGridState.layoutInfo.viewportSize.height - change.position.y
                val distFromTop = change.position.y
                autoScrollSpeed.value =when{
                    distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                    distFromTop < autoScrollThreshold ->-(autoScrollThreshold - distFromTop)
                    else->0f
                }

                lazyGridState.gridItemKeyAtPosition(change.position)?.let{ key ->
                    if(currentKey != key){
                        selectedIds.value = selectedIds.value
                            .minus(initialKey!!..currentKey!!)
                            .minus(currentKey!!..initialKey!!)
                            .plus(initialKey!!..key)
                            .plus(key..initialKey!!)
                        currentKey = key
                    }
                }
            }
        }
    )
}

@Composable
private fun ImageItem(
    photo: Photo,
    inSelectionMode: Boolean,
    selected: Boolean,
    onRadioBtnClick:(Boolean)->Unit,
    modifier: Modifier = Modifier
){
    Surface(
        //modifier = modifier.aspectRatio(3/4f),
        tonalElevation =3.dp
    ){
        Box{
            val transition = updateTransition(selected, label ="selected")
            val padding by transition.animateDp(label ="padding"){ selected ->
                if(selected)10.dp else 0.dp
            }
            val roundedCornerShape by transition.animateDp(label ="corner"){ selected ->
                if(selected)16.dp else 0.dp
            }
            val url = photo.keep.vodPic
            val (processedUrl, headers) = remember(url) { UrlProcessor.processUrl(url) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {   }
            ) {
                KamelImage(
                    resource = asyncPainterResource(data = processedUrl){
                        requestBuilder { // this: HttpRequestBuilder
                            for(h in headers){
                                header(h.key, h.value)
                            }
                            cacheControl(CacheControl.MAX_AGE)
                        }
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3/4f)
                        //.background(Color.LightGray)
                        .padding(padding)
                        .clip(RoundedCornerShape(roundedCornerShape)),
                    onLoading = { /* 可以在这里添加加载状态的UI */ },
                    onFailure = { /* 可以在这里添加加载失败的UI */ }
                )
                BasicText(photo.keep.vodName, modifier = Modifier.padding(top = 4.dp))
                BasicText(photo.keep.siteName, modifier = Modifier.padding(top = 4.dp))
            }

            if(inSelectionMode){
                if(selected){
                    val bgColor =MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    Icon(
                        Icons.Filled.CheckCircle,
                        tint =MaterialTheme.colorScheme.primary,
                        contentDescription =null,
                        modifier =Modifier
                            .padding(4.dp)
                            .border(2.dp, bgColor,CircleShape)
                            .clip(CircleShape)
                            .background(bgColor)
                            .clickable {
                                onRadioBtnClick(selected)
                            }
                    )
                }else{
                    Icon(
                        Icons.Filled.RadioButtonUnchecked,
                        tint =Color.White.copy(alpha =0.7f),
                        contentDescription =null,
                        modifier =Modifier
                            .padding(6.dp)
                            .clickable {
                                onRadioBtnClick(selected)
                        }
                    )
                }
            }
        }
    }
}
