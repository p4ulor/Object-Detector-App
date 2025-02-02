package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import p4ulor.mediapipe.ui.components.utils.SmoothHorizontalDividerCustom
import p4ulor.mediapipe.ui.theme.AppTheme

/**
 * I did this because:
 * 1. The [ExposedDropdownMenuBox] is an [ExperimentalMaterial3Api]
 * 2. The interactive mode preview of the composable isn't working (at least ocassionaly)
 * 3. I wanted the click of the widget to have a rounded look on it, so I made the
 * [OutlinedTextField] have a button inside it.
 * 4. This gives more customization options
 * - https://stackoverflow.com/questions/67111020/exposed-drop-down-menu-for-jetpack-compose
 */
@Composable
fun DropdownOptions(
    label: String,
    preSelectedOption: String,
    options: List<String>,
    horizontalPadding: Dp,
    onNewOption: (String) -> Unit
){
    var isExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(preSelectedOption) }

    val icon = if (isExpanded) MaterialIcons.ArrowDropUp else MaterialIcons.ArrowDropDown

    Column(
        Modifier.fillMaxWidth().padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {  },
                label = { Text(label) },
                readOnly = true,
                trailingIcon = {
                    Button( // Turn around to make the text clickable. Modifier.clickable doesnt work
                        onClick = {
                            isExpanded = !isExpanded
                        },
                        Modifier.fillMaxWidth(),
                        colors = ButtonColors(Color.Transparent, MaterialTheme.colorScheme.onSurface, Color.Transparent, Color.Transparent)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("$selectedOption")
                            Icon(icon, null)
                        }
                    }
                },
                maxLines = 1
            )

            // This dropdown has a white background that I didn't find a way to change the color
            // when the border is curved. The API is experimental after all

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                Modifier.width(this@BoxWithConstraints.maxWidth),
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(1.dp),
            ) {
                options.forEachIndexed { index, option ->
                    Box(Modifier.fillMaxSize().background(Color.Black))
                    DropdownMenuItem(
                        { Text(text = option, Modifier.padding(horizontal = horizontalPadding)) },
                        onClick = {
                            isExpanded = false
                            selectedOption = option
                            onNewOption(option)
                        },
                        Modifier.width(this@BoxWithConstraints.maxWidth),
                    )
                    if(index+1!=options.size){
                        SmoothHorizontalDividerCustom(
                            width = this@BoxWithConstraints.maxWidth / 2,
                            thickness = 0.5.dp
                        )
                    }
                    Box(Modifier.fillMaxSize().background(Color.Black))
                }
            }
        }
    }
}

@Preview
@Composable
fun DropdownOptionsPreview() = AppTheme(enableDarkTheme = true) {
    Surface(Modifier.fillMaxSize()) {
        DropdownOptions(
            label = "Model",
            preSelectedOption = "Model1",
            options = listOf("Model1", "Model2"),
            horizontalPadding = 8.dp,
            onNewOption = {}
        )
    }
}