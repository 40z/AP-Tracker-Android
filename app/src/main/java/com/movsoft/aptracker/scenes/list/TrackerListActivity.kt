package com.movsoft.aptracker.scenes.list

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.movsoft.aptracker.R
import com.movsoft.aptracker.databinding.ActivityMainBinding
import com.movsoft.aptracker.scenes.base.APTrackerBaseActivity
import com.movsoft.aptracker.scenes.base.ViewModelState
import com.movsoft.aptracker.scenes.base.ViewModelState.Placeholder
import com.movsoft.aptracker.scenes.base.ViewModelStatePlaceholder.SettingsNotValid
import com.movsoft.aptracker.scenes.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

interface TrackerListActionHandler: TextView.OnEditorActionListener {
    fun onSettingsTapped(view: View)
    fun onAddItemTapped(view: View)
}

class TrackerListActivity : APTrackerBaseActivity(), TrackerListViewModel.Listener, TrackerListActionHandler {

    private lateinit var binding: ActivityMainBinding
    private var dialog: AlertDialog? = null
    private lateinit var viewModel: TrackerListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = viewModelProvider.get(TrackerListViewModel::class.java)
        viewModel.listener = this

        binding.trackerList.adapter = TrackerListAdapter()
        binding.trackerList.layoutManager = LinearLayoutManager(this)

        viewModel.state.observe(this, Observer { transitionToState(it) })

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.handler = this
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                onSettingsTapped(binding.trackerList)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // TrackerListViewModel.Listener Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun showMessage(message: String) {
        Snackbar.make(binding.trackerList, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showError(message: String) {
        val snackbar = Snackbar.make(binding.trackerList, message, Snackbar.LENGTH_LONG)
        val redColor = resources.getColor(android.R.color.holo_red_dark)
        snackbar.view.setBackgroundColor(redColor)
        snackbar.show()
    }

    //------------------------------------------------------------------------------------------------------------------
    // TrackerListActionHandler Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun onSettingsTapped(view: View) {
        val settingsIntent = SettingsActivity.newIntent(this)
        startActivity(settingsIntent)
    }

    override fun onAddItemTapped(view: View) {
        showAddTrackedItemDialog()
    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.performClick()
        return true
    }

    //------------------------------------------------------------------------------------------------------------------
    // UI
    //------------------------------------------------------------------------------------------------------------------

    private fun showAddTrackedItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_trackable, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.edit_text)
        dialogEditText.setOnEditorActionListener(this)
        dialogEditText.requestFocus()

        dialog = AlertDialog.Builder(this).create()
        dialog!!.setTitle("Add New Tracked Item")
        dialog!!.setView(dialogView)
        dialog!!.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog!!.setButton(AlertDialog.BUTTON_POSITIVE, "Add") { _, _ ->
            viewModel.addItem(dialogEditText.text.toString())
        }
        dialog!!.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") {_, _-> }
        dialog!!.show()
    }

    private fun transitionToState(state: ViewModelState) {
        val showSettingsPlaceholder = state is Placeholder && state.type == SettingsNotValid
        binding.fab.visibility = if (!showSettingsPlaceholder) VISIBLE else GONE
        binding.settingsPlaceholderContainer.visibility = if (showSettingsPlaceholder) VISIBLE else GONE
    }
}
