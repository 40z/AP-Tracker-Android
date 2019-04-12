package com.movsoft.aptracker.scenes.list

import android.os.Bundle
import android.text.Editable
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
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.movsoft.aptracker.R
import com.movsoft.aptracker.databinding.ActivityMainBinding
import com.movsoft.aptracker.databinding.DialogTrackableOptionsBottomSheetBinding
import com.movsoft.aptracker.scenes.base.APTrackerBaseActivity
import com.movsoft.aptracker.scenes.base.ViewModelState
import com.movsoft.aptracker.scenes.base.ViewModelState.Placeholder
import com.movsoft.aptracker.scenes.base.ViewModelStatePlaceholder.SettingsNotValid
import com.movsoft.aptracker.scenes.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

interface TrackerListActionHandler: TextView.OnEditorActionListener {
    fun onSettingsTapped()
    fun onAddItemTapped()
    fun onDeleteItemTapped(trackedItemViewModel: TrackedItemViewModel)
    fun onEditItemTapped(trackedItemViewModel: TrackedItemViewModel)
    fun onOptionsForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout)
    fun onAverageForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout)
    fun onItemTapped(trackedItemViewModel: TrackedItemViewModel)
}

class TrackerListActivity : APTrackerBaseActivity(), TrackerListViewModel.Listener, TrackerListActionHandler {

    private lateinit var binding: ActivityMainBinding
    private var addItemDialog: AlertDialog? = null
    private var itemOptionsDialog: BottomSheetDialog? = null
    private lateinit var viewModel: TrackerListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = viewModelProvider.get(TrackerListViewModel::class.java)
        viewModel.listener = this

        binding.trackerList.adapter = TrackerListAdapter(this)
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
                onSettingsTapped()
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

    override fun onSettingsTapped() {
        val settingsIntent = SettingsActivity.newIntent(this)
        startActivity(settingsIntent)
    }

    override fun onAddItemTapped() {
        showAddTrackedItemDialog()
    }

    override fun onDeleteItemTapped(trackedItemViewModel: TrackedItemViewModel) {
        viewModel.deleteItem(trackedItemViewModel.itemIdentifier)
        itemOptionsDialog?.dismiss()
    }

    override fun onEditItemTapped(trackedItemViewModel: TrackedItemViewModel) {
        itemOptionsDialog?.dismiss()
        showEditTrackedItemDialog(trackedItemViewModel)
    }

    override fun onOptionsForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout) {
        cell.close(true)
        showOptionsDialog(trackedItemViewModel)
    }

    override fun onAverageForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout) {
        cell.close(true)
        viewModel.trackAverageItem(trackedItemViewModel.itemIdentifier)
    }

    override fun onItemTapped(trackedItemViewModel: TrackedItemViewModel) {
        viewModel.trackItem(trackedItemViewModel.itemIdentifier)
    }

    //------------------------------------------------------------------------------------------------------------------
    // OnEditorActionListene Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        addItemDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.performClick()
        return true
    }

    //------------------------------------------------------------------------------------------------------------------
    // UI
    //------------------------------------------------------------------------------------------------------------------

    private fun showOptionsDialog(trackedItemViewModel: TrackedItemViewModel) {
        itemOptionsDialog = BottomSheetDialog(this)
        val binding = DataBindingUtil.inflate<DialogTrackableOptionsBottomSheetBinding>(layoutInflater, R.layout.dialog_trackable_options_bottom_sheet, binding.trackerList, false)
        binding.handler = this
        binding.trackedItemViewModel = trackedItemViewModel
        itemOptionsDialog!!.setContentView(binding.root)
        itemOptionsDialog!!.show()
    }

    private fun showEditTrackedItemDialog(trackedItemViewModel: TrackedItemViewModel) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_trackable, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.edit_text)
        dialogEditText.setText(trackedItemViewModel.itemNameText, TextView.BufferType.EDITABLE)
        dialogEditText.setOnEditorActionListener(this)
        dialogEditText.requestFocus()

        addItemDialog = AlertDialog.Builder(this).create()
        addItemDialog!!.setTitle(getString(R.string.edit_item))
        addItemDialog!!.setView(dialogView)
        addItemDialog!!.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        addItemDialog!!.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.save)) { _, _ ->
            viewModel.editItem(trackedItemViewModel.itemIdentifier, dialogEditText.text.toString())
        }
        addItemDialog!!.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, _-> }
        addItemDialog!!.show()
    }

    private fun showAddTrackedItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_trackable, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.edit_text)
        dialogEditText.setOnEditorActionListener(this)
        dialogEditText.requestFocus()

        addItemDialog = AlertDialog.Builder(this).create()
        addItemDialog!!.setTitle(getString(R.string.add_new_item))
        addItemDialog!!.setView(dialogView)
        addItemDialog!!.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        addItemDialog!!.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.add)) { _, _ ->
            viewModel.addItem(dialogEditText.text.toString())
        }
        addItemDialog!!.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, _-> }
        addItemDialog!!.show()
    }

    private fun transitionToState(state: ViewModelState) {
        val showSettingsPlaceholder = state is Placeholder && state.type == SettingsNotValid
        binding.fab.visibility = if (!showSettingsPlaceholder) VISIBLE else GONE
        binding.settingsPlaceholderContainer.visibility = if (showSettingsPlaceholder) VISIBLE else GONE
    }
}
