package com.movsoft.aptracker.scenes.base

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.movsoft.aptracker.APTrackerApplication.DependencyProvider

open class APTrackerBaseAlertDialog(context: Context): AlertDialog(context) {

    protected val viewModelProvider: ViewModelProvider
        get() = ViewModelProvider(ViewModelStore(), DependencyProvider.viewModelFactory)
}