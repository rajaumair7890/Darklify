package com.codingwithumair.app.darklify.ui.mainScreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedFabMenu(
	icon: ImageVector,
	text: String,
	items: List<FabItem>,
	onItemClick: (FabItem) -> Unit
){

	var isFabExpanded by remember {
		mutableStateOf(false)
	}

	val fabTransition = updateTransition(targetState = isFabExpanded, label = "")

	val expandFabHorizontally by fabTransition.animateDp(
		transitionSpec = { tween(100, 0, LinearEasing) }, label = "",
		targetValueByState = {isExpanded ->
			if (isExpanded) 170.dp else 75.dp
		}
	)

	val shrinkFabVertically by fabTransition.animateDp(
		transitionSpec = { tween(100, 0, LinearEasing) }, label = "",
		targetValueByState = {isExpanded ->
			if (isExpanded) 60.dp else 75.dp
		}
	)

	val menuHeight by fabTransition.animateDp(
		transitionSpec = { tween(100, 50, LinearEasing) }, label = "",
		targetValueByState = {isExpanded ->
			if (isExpanded) (items.size * 55).dp else 0.dp
		}
	)


	ElevatedCard(
		modifier = Modifier.padding(4.dp),
		shape = RoundedCornerShape(16.dp),
		elevation = CardDefaults.elevatedCardElevation(4.dp),
		colors = CardDefaults.elevatedCardColors().copy(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		)
	) {
		AnimatedVisibility(
			visible = isFabExpanded,
			enter = fadeIn(tween(700, 100, LinearEasing)),
			exit = fadeOut(tween(500, 0, LinearEasing)),
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier
					.height(menuHeight)
			) {
				items.forEach { item ->

					Row(
						modifier = Modifier
							.width(expandFabHorizontally)
							.clickable { onItemClick(item) },
						horizontalArrangement = Arrangement.Center,
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(
							item.icon,
							contentDescription = null,
							modifier = Modifier.padding(start = 12.dp),
							tint = MaterialTheme.colorScheme.primary
						)
						Spacer(modifier = Modifier.weight(1f))
						Text(
							text = item.text,
							modifier = Modifier.padding(12.dp),
							fontFamily = FontFamily.Cursive,
							fontWeight = FontWeight.Bold,
							style = MaterialTheme.typography.bodyLarge,
							color = MaterialTheme.colorScheme.primary
						)
					}
				}
			}
		}

		FloatingActionButton(
			elevation = FloatingActionButtonDefaults.elevation(4.dp),
			containerColor = MaterialTheme.colorScheme.tertiary,
			onClick = { isFabExpanded = !isFabExpanded },
			modifier = Modifier
				.width(expandFabHorizontally)
				.height(shrinkFabVertically)
		) {
			Row(
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					icon,
					contentDescription = null,
					modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
					tint = MaterialTheme.colorScheme.onTertiary
				)
				AnimatedVisibility(
					visible = isFabExpanded,
					enter = fadeIn(tween(300, 100, LinearEasing)),
					exit = fadeOut(tween(100, 0, LinearEasing))
				) {
					Text(
						text = text,
						modifier = Modifier.padding(12.dp),
						color = MaterialTheme.colorScheme.onTertiary,
						fontFamily = FontFamily.Cursive,
						fontWeight = FontWeight.Bold,
						style = MaterialTheme.typography.headlineSmall
					)
				}
			}
		}
	}

}

data class FabItem(
	val icon: ImageVector,
	val text: String
)
