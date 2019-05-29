package com.movsoft.aptracker.scenes.list

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.michaelflisar.changelog.ChangelogBuilder
import com.movsoft.aptracker.R
import com.movsoft.aptracker.databinding.ActivityMainBinding
import com.movsoft.aptracker.databinding.DialogTrackableOptionsBottomSheetBinding
import com.movsoft.aptracker.scenes.add_item.AddTrackedItemDialog
import com.movsoft.aptracker.scenes.add_item.EditTrackedItemDialog
import com.movsoft.aptracker.scenes.base.APTrackerBaseActivity
import com.movsoft.aptracker.scenes.base.ViewModelState
import com.movsoft.aptracker.scenes.base.ViewModelState.Placeholder
import com.movsoft.aptracker.scenes.base.ViewModelStatePlaceholder.SettingsNotValid
import com.movsoft.aptracker.scenes.focus.FocusActivity
import com.movsoft.aptracker.scenes.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

interface TrackerListActionHandler {
    fun onSettingsTapped()
    fun onAddItemTapped()
    fun onDeleteItemTapped(trackedItemViewModel: TrackedItemViewModel)
    fun onEditItemTapped(trackedItemViewModel: TrackedItemViewModel)
    fun onFocusTapped(trackedItemViewModel: TrackedItemViewModel)
    fun onOptionsForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout)
    fun onAverageForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout)
    fun onItemTapped(trackedItemViewModel: TrackedItemViewModel): Boolean
}

class TrackerListActivity : APTrackerBaseActivity(), TrackerListViewModel.Listener, TrackerListActionHandler, AddTrackedItemDialog.Handler {

    private lateinit var binding: ActivityMainBinding
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
        viewModel.shouldShowWhatsNew.observe(this, Observer { invalidateOptionsMenu() })

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.handler = this
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val showWhatsNew = viewModel.shouldShowWhatsNew.value ?: false
        val menuResource = if (showWhatsNew) R.menu.menu_whats_new else R.menu.menu_main
        menuInflater.inflate(menuResource, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                onSettingsTapped()
                true
            }
            R.id.action_whats_new -> {
                showWhatsNewDialog()
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
    // AddTrackedItemDialog.Handler Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun onTrackedItemAddedOrUpdated() {
        viewModel.refresh()
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

    override fun onFocusTapped(trackedItemViewModel: TrackedItemViewModel) {
        itemOptionsDialog?.dismiss()
        val focusIntent = FocusActivity.newIntent(this, trackedItemViewModel.itemIdentifier)
        startActivity(focusIntent)
    }

    override fun onOptionsForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout) {
        cell.close(true)
        showOptionsDialog(trackedItemViewModel)
    }

    override fun onAverageForItemTapped(trackedItemViewModel: TrackedItemViewModel, cell: SwipeRevealLayout) {
        cell.close(true)
        viewModel.trackAverageItem(trackedItemViewModel.itemIdentifier)
    }

    override fun onItemTapped(trackedItemViewModel: TrackedItemViewModel): Boolean {
        val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(200)
        viewModel.trackItem(trackedItemViewModel.itemIdentifier)
        return false
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
        val dialog = EditTrackedItemDialog(this, trackedItemViewModel.itemIdentifier, this)
        dialog.show()
    }

    private fun showAddTrackedItemDialog() {
        val dialog = AddTrackedItemDialog(this, this)
        dialog.show()
    }

    private fun showWhatsNewDialog() {
        ChangelogBuilder()
            .withTitle(getString(R.string.whats_new))
            .withMinVersionToShow(viewModel.lastUsedVersionCode)
            .buildAndShowDialog(this, false)
        viewModel.markWhatsNewAsSeen()
    }

    private fun transitionToState(state: ViewModelState) {
        val showSettingsPlaceholder = state is Placeholder && state.type == SettingsNotValid
        binding.fab.visibility = if (!showSettingsPlaceholder) VISIBLE else GONE
        binding.settingsPlaceholderContainer.visibility = if (showSettingsPlaceholder) VISIBLE else GONE
    }
}
