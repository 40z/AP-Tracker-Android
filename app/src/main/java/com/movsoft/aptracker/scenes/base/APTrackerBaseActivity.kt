package com.movsoft.aptracker.scenes.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.movsoft.aptracker.APTrackerApplication.DependencyProvider

@SuppressLint("Registered")
open class APTrackerBaseActivity: AppCompatActivity() {

    protected val viewModelProvider: ViewModelProvider
        get() = ViewModelProvider(this, DependencyProvider.viewModelFactory)
}