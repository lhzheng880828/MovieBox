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
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class Photo(
val id:Int,
val url:String="https://picsum.photos/seed/${(0..100000).random()}/256/256"
)

class FollowedScreen(
):Screen
{

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val  photos: List<Photo> = List(100){ Photo(it) }
        val  selectedIds:MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet())}
        val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty()}}
        val state = rememberLazyGridState()
        val autoScrollSpeed = remember { mutableStateOf(0f)}
        LaunchedEffect(autoScrollSpeed.value){
            if(autoScrollSpeed.value !=0f){
                while(isActive){
                    state.scrollBy(autoScrollSpeed.value)
                    delay(10)
                }
            }
        }
        var showDeleteButton by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("最近观看", fontSize = 24.sp) },
                    actions = {
                       IconButton(onClick = { /* Sync action */ }) {
                           Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Button"
                            )
                        }
                       IconButton(onClick = {
                            showDeleteButton = !showDeleteButton
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Button"
                            )

                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyVerticalGrid(
                state = state,
                columns =GridCells.Adaptive(minSize =128.dp),
                verticalArrangement =Arrangement.spacedBy(3.dp),
                horizontalArrangement =Arrangement.spacedBy(3.dp),
                modifier =Modifier.photoGridDragHandler(
                    lazyGridState = state,
                    haptics =LocalHapticFeedback.current,
                    selectedIds = selectedIds,
                    autoScrollSpeed = autoScrollSpeed,
                    autoScrollThreshold =with(LocalDensity.current){40.dp.toPx()}
                ).padding(innerPadding)
            ){
                items(photos, key ={ it.id }){ photo ->
                    val selected by remember { derivedStateOf { selectedIds.value.contains(photo.id)}}
                    ImageItem(
                        photo, inSelectionMode, selected,
                        Modifier
                            .semantics {
                                if(!inSelectionMode){
                                    onLongClick("Select"){
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
    modifier: Modifier = Modifier
){
    Surface(
        modifier = modifier.aspectRatio(1f),
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
            KamelImage(
                resource = asyncPainterResource(photo.url),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .padding(padding)
                    .clip(RoundedCornerShape(roundedCornerShape)),
                onLoading = { /* 可以在这里添加加载状态的UI */ },
                onFailure = { /* 可以在这里添加加载失败的UI */ }
            )
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
                    )
                }else{
                    Icon(
                        Icons.Filled.RadioButtonUnchecked,
                        tint =Color.White.copy(alpha =0.7f),
                        contentDescription =null,
                        modifier =Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
