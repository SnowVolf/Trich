package ru.svolf.trich.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.svolf.trich.ui.settings.composition.SettingsSwitch
import ru.svolf.trich.uikit.R
import ru.svolf.trich.uikit.components.FloatingToolbar

@Composable
fun SettingsScreen(
    onBoardsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = stringResource(R.string.settings_title),
                onBackClick = onBackClick,
                onBoardsClick = onBoardsClick,
                onHistoryClick = onHistoryClick,
                onDraftsClick = onDraftsClick,
                onFavoritesClick = onFavoritesClick,
                onSettingsClick = { } // Current
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding())) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(
                        top = padding.calculateTopPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                Text(
                    stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    stringResource(R.string.settings_passcode_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.passcode,
                    onValueChange = viewModel::updatePasscode,
                    label = { Text(stringResource(R.string.settings_passcode_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = viewModel::activatePasscode,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.passcodeActivated
                ) {
                    Text(
                        if (state.passcodeActivated) stringResource(R.string.settings_passcode_active) else stringResource(
                            R.string.settings_passcode_activate
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                SettingsSwitch(
                    title = stringResource(R.string.settings_dark_theme),
                    checked = state.isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                val permissionLauncher =
                    androidx.activity.compose.rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        if (isGranted || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
                            viewModel.toggleBackgroundCheckFavorites()
                        }
                    }

                SettingsSwitch(
                    title = "Фоновая проверка новых постов",
                    checked = state.backgroundCheckFavorites,
                    onCheckedChange = {
                        if (!state.backgroundCheckFavorites && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.toggleBackgroundCheckFavorites()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    stringResource(R.string.settings_font_size, state.fontSize),
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = state.fontSize.toFloat(),
                    onValueChange = { viewModel.setFontSize(it.toInt()) },
                    valueRange = 12f..24f,
                    steps = 11,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
