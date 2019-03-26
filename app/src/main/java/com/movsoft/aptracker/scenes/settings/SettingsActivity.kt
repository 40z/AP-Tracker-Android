package com.movsoft.aptracker.scenes.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.movsoft.aptracker.R
import com.movsoft.aptracker.databinding.ActivitySettingsBinding
import com.movsoft.aptracker.scenes.base.APTrackerBaseActivity
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Handles view actions.
 */
interface SettingsActionHandler {
    fun onSaveTapped(view: View)
}

/**
 * Settings screen.
 */
class SettingsActivity: APTrackerBaseActivity(), SettingsActionHandler {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    companion object {

        /**
         * New default intent for showing settings.
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = viewModelProvider.get(SettingsViewModel::class.java)
        viewModel.refresh()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.handler = this
        binding.viewModel = viewModel

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (viewModel.userNameText.isNullOrEmpty()) {
            binding.userNameEditText.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //------------------------------------------------------------------------------------------------------------------
    // SettingsActionHandler Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun onSaveTapped(view: View) {
        viewModel.save()
        finish()
    }
}