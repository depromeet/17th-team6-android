package com.dpm.sixpack.presentation.example

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.example.contract.ExampleIntent
import com.dpm.sixpack.presentation.example.contract.ExampleSideEffect
import com.dpm.sixpack.presentation.example.contract.ExampleState
import com.dpm.sixpack.presentation.theme.SixpackTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun ExampleScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    vm: ExampleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = vm.collectAsState()

    vm.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ExampleSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is ExampleSideEffect.NavigateNext -> {
                onNextClick()
            }

            is ExampleSideEffect.NavigateBack -> {
                onBackClick()
            }
        }
    }

    ExampleScreenContent(
        state = state.value,
        onEvent = vm::onEvent,
    )
}

@Composable
private fun ExampleScreenContent(
    state: ExampleState,
    onEvent: (ExampleIntent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "Count: ${state.count}",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(onClick = { onEvent(ExampleIntent.Decrement(1)) }) {
                        Text("Decrement")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { onEvent(ExampleIntent.Increment(1)) }) {
                        Text("Increment")
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(onClick = { onEvent(ExampleIntent.clickBack) }) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { onEvent(ExampleIntent.clickNext) }) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewExampleScreen() {
    SixpackTheme {
        ExampleScreenContent(
            state = ExampleState(count = 0, isLoading = false),
            onEvent = {}
        )
    }
}
