package com.example.parkingcompose.viewmodels

import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import java.util.*
import android.app.Activity


class LanguageViewModel : ViewModel() {
    fun setLocale(activity: Activity, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        activity.recreate()  // Reiniciar la actividad para aplicar los cambios
    }
}

