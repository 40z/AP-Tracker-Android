package com.movsoft.aptracker.scenes.focus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.movsoft.aptracker.R
import com.movsoft.aptracker.databinding.ActivityFocusBinding
import com.movsoft.aptracker.scenes.base.APTrackerBaseActivity
import kotlinx.android.synthetic.main.activity_main.*

interface FocusActionHandler {
    fun onItemTapped()
}

class FocusActivity : APTrackerBaseActivity(), FocusViewModel.Listener, FocusActionHandler {

    private lateinit var binding: ActivityFocusBinding
    private lateinit var viewModel: FocusViewModel

    companion object {

        private val EXTRA_ITEM_IDENTIFIER = "EXTRA_ITEM_IDENTIFIER"

        /**
         * New default intent for item focus.
         */
        fun newIntent(context: Context, itemIdentifier: String): Intent {
            val intent = Intent(context, FocusActivity::class.java)
            intent.putExtra(EXTRA_ITEM_IDENTIFIER, itemIdentifier)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = viewModelProvider.get(FocusViewModel::class.java)
        viewModel.listener = this
        viewModel.refresh(intent.getStringExtra(EXTRA_ITEM_IDENTIFIER))

        binding = DataBindingUtil.setContentView(this, R.layout.activity_focus)
        binding.handler = this
        binding.viewModel = viewModel

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //------------------------------------------------------------------------------------------------------------------
    // FocusActionHandler Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun onItemTapped() {
        viewModel.trackItem()
    }

    //------------------------------------------------------------------------------------------------------------------
    // FocusViewModel.Listener Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun showMessage(message: String) {
        Snackbar.make(binding.content, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showError(message: String) {
        val snackbar = Snackbar.make(binding.content, message, Snackbar.LENGTH_LONG)
        val redColor = resources.getColor(android.R.color.holo_red_dark)
        snackbar.view.setBackgroundColor(redColor)
        snackbar.show()
    }
}