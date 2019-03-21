package com.movsoft.aptracker.scenes

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.movsoft.aptracker.APTrackerApplication

@SuppressLint("Registered")
open class APTrackerBaseActivity: AppCompatActivity() {

    protected val viewModelProvider: ViewModelProvider
        get() = ViewModelProvider(this, (application as APTrackerApplication).viewModelFactory)
}