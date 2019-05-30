package com.movsoft.aptracker.scenes.add_item

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.movsoft.aptracker.R
import com.movsoft.aptracker.databinding.DialogNewTrackableBinding
import com.movsoft.aptracker.models.TrackedItemSettings.TrackingMode
import com.movsoft.aptracker.models.TrackedItemSettings.TrackingMode.SINGLE
import com.movsoft.aptracker.models.TrackedItemSettings.TrackingMode.TOGGLE
import com.movsoft.aptracker.scenes.base.APTrackerBaseAlertDialog

interface AddTrackedItemActionHandler : TextView.OnEditorActionListener {
    fun onTrackingToggleChanged(group: RadioGroup, id: Int)
}

open class AddTrackedItemDialog(context: Context, var handler: Handler?) :
    APTrackerBaseAlertDialog(context), AddTrackedItemActionHandler {

    interface Handler {
        fun onTrackedItemAddedOrUpdated()
    }

    private lateinit var binding: DialogNewTrackableBinding
    private lateinit var viewModel: AddTrackedItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_new_trackable, null, false)

        viewModel = getViewModel()
        viewModel.refresh()

        binding.viewModel = viewModel
        binding.handler = this
        binding.nameEditText.requestFocus()
        binding.trackingStyle.check(convertTrackingModeToId(viewModel.trackingStyleSelection))

        setView(binding.root)
        setTitle(viewModel.titleText)
        setButton(BUTTON_POSITIVE, viewModel.saveText, this::onSave)
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel)) { _, _ -> }
        window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        super.onCreate(savedInstanceState)
    }

    protected open fun getViewModel(): AddTrackedItemViewModel {
        return viewModelProvider.get(AddTrackedItemViewModelImpl::class.java)
    }

    private fun onSave(dialog: DialogInterface, id: Int) {
        viewModel.save()
        handler?.onTrackedItemAddedOrUpdated()
    }

    //------------------------------------------------------------------------------------------------------------------
    // AddTrackedItemActionHandler Conformance
    //------------------------------------------------------------------------------------------------------------------

    override fun onTrackingToggleChanged(group: RadioGroup, id: Int) {
        viewModel.trackingStyleSelection = convertIdToTrackingMode(id)
    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        getButton(BUTTON_POSITIVE)?.performClick()
        return true
    }

    //------------------------------------------------------------------------------------------------------------------
    // Helpers
    //------------------------------------------------------------------------------------------------------------------

    private fun convertIdToTrackingMode(id: Int): TrackingMode {
        return when (id) {
            R.id.tracking_style_toggle -> TOGGLE
            R.id.tracking_style_single -> SINGLE
            else -> TOGGLE
        }
    }

    private fun convertTrackingModeToId(mode: TrackingMode): Int {
        return when (mode) {
            TOGGLE -> R.id.tracking_style_toggle
            SINGLE -> R.id.tracking_style_single
        }
    }
}