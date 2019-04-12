package com.movsoft.aptracker.scenes.list

import com.movsoft.aptracker.models.TrackedItem

class TrackedItemViewModel(trackedItem: TrackedItem) {
    val itemNameText: String = trackedItem.name.capitalize()
    val itemIdentifier: String = trackedItem.identifier
}
