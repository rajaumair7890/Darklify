package com.codingwithumair.app.darklify.ui.mainScreen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.codingwithumair.app.darklify.R
import com.codingwithumair.app.darklify.ui.mainScreen.components.AnimatedFabMenu
import com.codingwithumair.app.darklify.ui.mainScreen.components.FabItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
	enabled: Boolean,
	onEnabledChange: (Boolean) -> Unit,
	lightWallpaperBitmap: Bitmap?,
	darkWallpaperBitmap: Bitmap?,
	onLightWallpaperChange: (Uri) -> Unit,
	onDarkWallpaperChange: (Uri) -> Unit,
	listOfWallpapers: List<Triple<Int, Int, Int>>,
	selectedItem: Int,
	onSelectedItemChange: (Int) -> Unit,
	modifier: Modifier = Modifier
){

	val context = LocalContext.current

	val lightWallpaperImagePicker = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent(),
		onResult = { uri ->
			if(uri != null){
				onLightWallpaperChange(uri)
			}
		}
	)

	val darkWallpaperImagePicker = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent(),
		onResult = { uri ->
			if(uri != null){
				onDarkWallpaperChange(uri)
			}
		}
	)

	val fabItems = remember {
		listOf(
			FabItem(Icons.Default.Edit, context.resources.getString(R.string.light_wallpaper)),
			FabItem(Icons.Default.Edit, context.resources.getString(R.string.dark_wallpaper))
		)
	}

	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	Scaffold(
		floatingActionButton = {
			AnimatedFabMenu(
				icon = Icons.Default.Edit,
				text = stringResource(id = R.string.customize),
				items = fabItems,
				onItemClick = {
					if(it == fabItems.first()){
						lightWallpaperImagePicker.launch("image/*")
					}else{
						darkWallpaperImagePicker.launch("image/*")
					}
				}
			)
		},
		topBar = {
			ElevatedCard(
				elevation = CardDefaults.elevatedCardElevation(4.dp)
			){
				LargeTopAppBar(
					title = {
						Text(
							stringResource(id = R.string.app_name),
							color = MaterialTheme.colorScheme.primary,
							//modifier = Modifier.padding(horizontal = 6.dp),
							fontWeight = FontWeight.Bold,
							fontFamily = FontFamily.Cursive,
							fontSize = 40.sp
						)
					},
					actions = {
						AnimatedVisibility(
							scrollBehavior.state.collapsedFraction < 0.1
						){
							Text(
								text = stringResource(id = R.string.enable_auto),
								color = MaterialTheme.colorScheme.primary,
								//modifier = Modifier.padding(horizontal = 6.dp),
								fontWeight = FontWeight.Bold,
								fontFamily = FontFamily.Cursive,
								fontSize = 24.sp
							)
						}
						Switch(
							checked = enabled,
							onCheckedChange = onEnabledChange,
							colors = SwitchDefaults.colors().copy(
								uncheckedBorderColor = MaterialTheme.colorScheme.secondary,
								uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
								uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
								uncheckedIconColor = MaterialTheme.colorScheme.secondary
							),
							modifier = Modifier.padding(12.dp)
						)
					},
					scrollBehavior = scrollBehavior,
					colors = TopAppBarDefaults.largeTopAppBarColors().copy(
						containerColor = TopAppBarDefaults.largeTopAppBarColors().scrolledContainerColor
					),
					modifier = Modifier.clip(RoundedCornerShape(25.dp))
				)
			}
		},
		modifier = modifier
			.fillMaxSize()
	) { padding ->
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.surface,
			tonalElevation = 4.dp
		){
			LazyColumn(
				modifier = Modifier
					.padding(padding)
					.nestedScroll(scrollBehavior.nestedScrollConnection)
			) {
				item{
					Box(modifier = Modifier.size(12.dp))
				}
				if(lightWallpaperBitmap != null || darkWallpaperBitmap != null){
					item{
						DarkAndLightCard(
							id = 0,
							lightWallpaperImage = lightWallpaperBitmap,
							darkWallpaperImage = darkWallpaperBitmap,
							isSelected = selectedItem == 0,
							onClick = onSelectedItemChange
						)
					}
				}
				items(listOfWallpapers, key = {it.first}){wallpaperTriple ->
					DarkAndLightCard(
						id = wallpaperTriple.first,
						lightWallpaperImage = painterResource(id = wallpaperTriple.second),
						darkWallpaperImage = painterResource(wallpaperTriple.third),
						isSelected = selectedItem == wallpaperTriple.first,
						onClick = onSelectedItemChange
					)
				}
			}
		}
	}
}


@Composable
fun DarkAndLightCard(
	id: Int,
	onClick: (Int) -> Unit,
	isSelected: Boolean,
	lightWallpaperImage: Any?,
	darkWallpaperImage: Any?,
	modifier: Modifier = Modifier
){
	ElevatedCard(
		shape = RoundedCornerShape(16.dp),
		elevation = CardDefaults.elevatedCardElevation(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(12.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable { onClick(id) }
		){
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(12.dp)
			) {
				ImageAndTextColumn(
					image = lightWallpaperImage,
					text = stringResource(id = R.string.light_wallpaper),
					modifier = Modifier.weight(1f)
				)
				ImageAndTextColumn(
					image = darkWallpaperImage,
					text = stringResource(id = R.string.dark_wallpaper),
					modifier = Modifier.weight(1f)
				)
			}
			if(isSelected){
				Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier
						.matchParentSize()
						.background(Color.Black.copy(0.3f))
				){
					Image(
						Icons.Default.CheckCircle,
						contentDescription = null,
						modifier = Modifier.size(100.dp),
						alpha = 0.4f
					)
				}
			}
		}

	}
}

@Composable
fun ImageAndTextColumn(
	image: Any?,
	text: String,
	modifier: Modifier = Modifier
){
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		ImageCard(
			image = image
		)
		Text(
			text = text,
			fontFamily = FontFamily.Cursive,
			fontSize = 20.sp,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.primary
		)
	}
}

@Composable
fun ImageCard(
	image: Any?,
	modifier: Modifier = Modifier
){
	val configuration = LocalConfiguration.current
	Card(
		modifier = if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
			modifier.size(width = 165.dp, height = 320.dp)
		}else{
			modifier.size(width = 320.dp, height = 165.dp)
		},
	) {
		Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
			when(image){
				is Bitmap -> {
					AsyncImage(
						model = image,
						contentDescription = null,
						contentScale = ContentScale.Crop,
						alignment = Alignment.Center,
						modifier = Modifier.fillMaxSize()
					)
				}
				is Painter -> {
					Image(
						painter = image,
						contentDescription = null,
						contentScale = ContentScale.Crop,
						alignment = Alignment.Center,
						modifier = Modifier.fillMaxSize()
					)
				}
			}
			if(image == null){
				Text(stringResource(id = R.string.no_image_selected))
			}
		}
	}
}

