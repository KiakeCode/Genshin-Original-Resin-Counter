package com.example.genshin_original_resin_counter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.genshin_original_resin_counter.ui.theme.GenshinOriginalResinCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var counter by remember {
        mutableStateOf("0")
    }

    var input by remember {
        mutableStateOf("0")
    }
    var textsize = 21f

    var focusManager = LocalFocusManager.current

    @Composable
    fun UniversalTextStyleBold(): TextStyle = TextStyle(
        fontWeight = FontWeight.W700, fontSize = TextUnit(value = textsize, type = TextUnitType.Sp),
        color = MaterialTheme.colorScheme.secondary
    )

    @Composable
    fun UniversalTextSize(): TextUnit = TextUnit(
        textsize, TextUnitType.Sp
    )

    GenshinOriginalResinCounterTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Original Resin Numbers: ", fontSize = TextUnit(
                                    textsize, TextUnitType.Sp
                                )
                            )


                            BasicTextField(
                                value = input,
                                onValueChange = {
                                    input = if (input.contains("^0".toRegex())) it.replace(
                                        regex = "^0".toRegex(), replacement = ""
                                    )
                                    else if (input.length == 3 && it.length > 3) input
                                    else if (it.isNotEmpty() && it.toInt() > 200) "200"
                                    else it
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                                ),
                                maxLines = 1,
                                modifier = Modifier.width(IntrinsicSize.Min),
                                keyboardActions = KeyboardActions(onNext = {
                                    if (input.isEmpty()) input = "0"
                                    focusManager.clearFocus(
                                        force = true
                                    )
                                }),
                                textStyle = UniversalTextStyleBold()
                            )
                            Text("/200", fontSize = UniversalTextSize())
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }) { e ->
            Column(
                Modifier
                    .padding(e)
                    .fillMaxWidth()
                    .border(1.dp, Color.Red)
            ) {
                Text("/200", fontSize = UniversalTextSize())


            }
        }
    }
}