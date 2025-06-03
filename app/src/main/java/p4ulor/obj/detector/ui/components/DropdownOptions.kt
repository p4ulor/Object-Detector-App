package p4ulor.obj.detector.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import p4ulor.obj.detector.R
import p4ulor.obj.detector.ui.components.utils.HorizontalPadding
import p4ulor.obj.detector.ui.components.utils.SmoothHorizontalDividerCustom
import p4ulor.obj.detector.ui.theme.AppTheme

/**
 * @param onNewOption a callback that contains the index of in the range of [options] the option
 * that's selected
 * @param preSelectedOption the index of an option from [options]
 * @param optionLabels a map where the keys are an [options] and the value is a custom label (could
 * be a description) of the option instead of using the [options]
 *
 * I did this because:
 * 1. The [ExposedDropdownMenuBox] is an [ExperimentalMaterial3Api]
 * 2. The interactive mode preview of the composable isn't working (at least occasionally)
 * 3. I wanted the click of the widget to have a rounded look on it, so I made the
 * [OutlinedTextField] have a button inside it and it looks great.
 * 4. This gives more customization options
 * - https://stackoverflow.com/questions/67111020/exposed-drop-down-menu-for-jetpack-compose
 */
@Composable
fun DropdownOptions(
    @StringRes label: Int,
    preSelectedOption: Int,
    options: List<String>,
    onNewOption: (Int) -> Unit,
    optionLabels: Map<String, String> = emptyMap(),
    horizontalPadding: Dp = HorizontalPadding,
){
    var isExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableIntStateOf(preSelectedOption) }

    val icon = if (isExpanded) MaterialIcons.ArrowDropUp else MaterialIcons.ArrowDropDown

    Column(
        Modifier.fillMaxWidth().padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints {
            OutlinedTextField(
                value = options[selectedOption],
                onValueChange = {  },
                label = { EzText(label) },
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
                            Text("${options[selectedOption]}")
                            Icon(icon, null)
                        }
                    }
                },
                maxLines = 1
            )

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                Modifier.width(this@BoxWithConstraints.maxWidth),
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(1.dp),
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = optionLabels.get(option) ?: option,
                                Modifier.padding(horizontal = horizontalPadding)
                            )
                       },
                        onClick = {
                            isExpanded = false
                            selectedOption = index
                            onNewOption(index)
                        },
                        Modifier.width(this@BoxWithConstraints.maxWidth),
                    )
                    if (index+1 != options.size) {
                        SmoothHorizontalDividerCustom(
                            width = this@BoxWithConstraints.maxWidth / 2,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DropdownOptionsPreview() = AppTheme(enableDarkTheme = true) {
    Surface(Modifier.fillMaxSize()) {
        DropdownOptions(
            label = R.string.model,
            preSelectedOption = 0,
            options = listOf("Model1", "Model2"),
            onNewOption = {},
            optionLabels = mapOf("Model1" to "Model1 (is the default)")
        )
    }
}