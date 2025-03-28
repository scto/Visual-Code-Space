/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.activities.base

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.compose.LocalDarkMode
import com.teixeira.vcspace.core.components.common.VCSpaceLargeTopBar
import com.teixeira.vcspace.core.settings.Settings.General.rememberFollowSystemTheme
import com.teixeira.vcspace.core.settings.Settings.General.rememberIsDarkMode
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.ToastHost
import com.teixeira.vcspace.ui.rememberToastHostState
import com.teixeira.vcspace.ui.theme.VCSpaceTheme
import com.teixeira.vcspace.utils.isStoragePermissionGranted
import kiwi.orbit.compose.ui.controls.Scaffold

abstract class BaseComposeActivity : AppCompatActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VCSpaceTheme {
                val systemBarFollowThemeState by rememberSaveable { mutableStateOf(true) }

                val followSystemTheme by rememberFollowSystemTheme()
                val localDarkTheme by rememberIsDarkMode()
                val systemDarkTheme = isSystemInDarkTheme()

                val darkTheme by remember(followSystemTheme, localDarkTheme, systemDarkTheme) {
                    mutableStateOf(if (followSystemTheme) systemDarkTheme else localDarkTheme)
                }

                LaunchedEffect(darkTheme, systemBarFollowThemeState) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { darkTheme || !systemBarFollowThemeState },
                        navigationBarStyle = SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { darkTheme || !systemBarFollowThemeState }
                    )
                }

                val storagePermissionsState = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                var hasPermission by remember { mutableStateOf(isStoragePermissionGranted()) }

                LifecycleResumeEffect(hasPermission) {
                    hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Environment.isExternalStorageManager()
                    } else {
                        storagePermissionsState.allPermissionsGranted
                    }

                    onPauseOrDispose { }
                }

                ProvideBaseCompositionLocals(darkMode = darkTheme) {

                    if (hasPermission) {
                        MainScreen()
                    } else {
                        SetupPermissionScreen(storagePermissionsState)
                    }

                    ToastHost()
                }
            }
        }
    }

    @Composable
    private fun ProvideBaseCompositionLocals(
        darkMode: Boolean,
        content: @Composable () -> Unit
    ) {
        val toastHostState = rememberToastHostState()

        CompositionLocalProvider(
            LocalLifecycleScope provides lifecycleScope,
            LocalLayoutInflater provides layoutInflater,
            LocalToastHostState provides toastHostState,
            LocalDarkMode provides darkMode,
            content = content
        )
    }

    @Composable
    protected abstract fun MainScreen()

    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
    @Composable
    private fun SetupPermissionScreen(permissionsState: MultiplePermissionsState) {
        Scaffold(
            topBar = {
                VCSpaceLargeTopBar(
                    title = {
                        Text(
                            text = stringResource(strings.app_name),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
                )
            }
        ) { innerPadding ->
            BackHandler(onBack = AppUtils::exitApp)

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(strings.file_storage_access),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                ElevatedCard {
                    Text(
                        text = stringResource(strings.file_storage_access_message),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {

                    OutlinedButton(
                        onClick = AppUtils::exitApp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(stringResource(strings.exit))
                    }

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                try {
                                    val intent =
                                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                    intent.data = "package:${packageName}".toUri()
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    val intent =
                                        Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                    startActivity(intent)
                                }
                            } else {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(stringResource(strings.file_storage_access_grant))
                    }
                }
            }
        }
    }
}

@Composable
fun ObserveLifecycleEvents(onStateChanged: (Lifecycle.Event) -> Unit) {
    val currentOnStateChanged by rememberUpdatedState(onStateChanged)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            currentOnStateChanged(event)
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
