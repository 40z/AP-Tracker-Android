package com.movsoft.aptracker.scenes.add_item

import android.content.Context

class EditTrackedItemDialog(context: Context, private val itemIdentifier: String, handler: Handler?) :
    AddTrackedItemDialog(context, handler) {

    override fun getViewModel(): AddTrackedItemViewModel {
        return viewModelProvider.get(EditTrackedItemViewModel::class.java).apply {
            itemIdentifier = this@EditTrackedItemDialog.itemIdentifier
        }
    }
}
