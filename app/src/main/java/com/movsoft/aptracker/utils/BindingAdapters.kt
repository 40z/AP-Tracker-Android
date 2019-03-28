package com.movsoft.aptracker.utils

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.movsoft.aptracker.scenes.list.TrackedItemViewModel
import com.movsoft.aptracker.scenes.list.TrackerListAdapter

@BindingAdapter("tracked_items")
fun RecyclerView.setTrackedItems(items: LiveData<List<TrackedItemViewModel>>) {
    val adapter = adapter
    if (adapter is TrackerListAdapter) {
        adapter.refresh(items.value ?: listOf())
    }
}

@BindingAdapter("on_editor_action_listener")
fun EditText.setEditorActionListener(listener: TextView.OnEditorActionListener?) {
    setOnEditorActionListener(listener)
}
