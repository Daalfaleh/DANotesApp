package com.dunesaero.missionnotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunesaero.missionnotes.data.preferences.ThemeSetting
import com.dunesaero.missionnotes.data.preferences.ThemeSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeSettingsManager: ThemeSettingsManager
) : ViewModel() {

    val themeSetting: StateFlow<ThemeSetting> = themeSettingsManager.themeSettingFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeSetting.SYSTEM // Default until DataStore loads
        )

    fun setThemeSetting(newSetting: ThemeSetting) {
        viewModelScope.launch {
            themeSettingsManager.setThemeSetting(newSetting)
        }
    }

    fun getNextThemeSetting(): ThemeSetting {
        return when (themeSetting.value) {
            ThemeSetting.SYSTEM -> ThemeSetting.LIGHT
            ThemeSetting.LIGHT -> ThemeSetting.DARK
            ThemeSetting.DARK -> ThemeSetting.SYSTEM
        }
    }
}
