package com.codingwithumair.app.darklify.ui.mainScreen.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.codingwithumair.app.darklify.R

@Composable
fun WallpaperSettingDialog(
	modifier: Modifier = Modifier
){
	Dialog(onDismissRequest = {}) {
		Surface(
			tonalElevation = 4.dp,
			shadowElevation = 4.dp,
			shape = RoundedCornerShape(15.dp)
		){
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceEvenly,
				modifier = modifier
					.fillMaxWidth()
					.height(100.dp)
					.padding(16.dp)
			){
				LoadingAnimation()
				Column(
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.Start
				) {
					Text(
						text = stringResource(id = R.string.applying_wallpaper),
						fontSize = 18.sp,
						fontFamily = FontFamily.Cursive,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.primary
					)
					Text(
						text = stringResource(id = R.string.please_wait),
						fontSize = 14.sp,
						fontFamily = FontFamily.Cursive,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.primary
					)
				}
			}
		}
	}
}

@Composable
fun LoadingAnimation(
	modifier: Modifier = Modifier,
	colors: List<Color> = listOf(
		Color(0xFFF4B400),
		Color(0xFF0F9D58),
		Color(0xFFDB4437),
		Color(0xFF4285F4)
	),
	strokeWidth: Dp = 4.dp
) {
	val expansionDuration by remember { mutableIntStateOf(700) }
	val infiniteTransition = rememberInfiniteTransition(label = "")


	val currentColorIndex by infiniteTransition.animateValue(
		initialValue = 0,
		targetValue = colors.size,
		typeConverter = Int.VectorConverter,
		animationSpec = infiniteRepeatable(
			repeatMode = RepeatMode.Restart,
			animation = tween(
				durationMillis = 2*expansionDuration*colors.size,
				easing = LinearEasing
			)
		), label = ""
	)

	val progress by infiniteTransition.animateFloat(
		initialValue = 0.1f,
		targetValue = 0.8f,
		animationSpec = infiniteRepeatable(
			repeatMode = RepeatMode.Reverse,
			animation = tween(
				durationMillis = expansionDuration,
				easing = LinearEasing
			)
		), label = ""
	)

	val rotation by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			repeatMode = RepeatMode.Restart,
			animation = tween(
				durationMillis = expansionDuration,
				easing = LinearEasing
			)
		), label = ""
	)

	CircularProgressIndicator(
		modifier = modifier
			.rotate(rotation),
		progress = { progress },
		color = colors[currentColorIndex],
		strokeWidth = strokeWidth
	)
}
