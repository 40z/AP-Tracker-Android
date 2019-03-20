package com.movsoft.aptracker.scenes.list

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.movsoft.aptracker.R

class TrackerListAdapter: RecyclerView.Adapter<TrackerListAdapter.ViewHolder>(), Observer<List<TrackerItemViewModel>> {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.text_view)
        var bottomDivider: View = view.findViewById(R.id.bottom_divider)
    }

    private var items = listOf<TrackerItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.cell_tracker_item, parent,false)
        val viewHolder = ViewHolder(cell)
        viewHolder.textView = cell.findViewById(R.id.text_view)
        return ViewHolder(cell)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.itemNameText
        holder.view.setOnClickListener { item.onTap() }
        holder.bottomDivider.visibility = if (position == items.lastIndex) GONE else VISIBLE
    }

    override fun onChanged(t: List<TrackerItemViewModel>?) {
        items = t ?: listOf()
        notifyDataSetChanged()
    }

    class SwipeController: ItemTouchHelper.Callback() {
        private var swipeBack = false

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return makeMovementFlags(0, LEFT or RIGHT)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //TODO
        }

        override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
            if (swipeBack) {
                swipeBack = false
                return 0
            }

            return super.convertToAbsoluteDirection(flags, layoutDirection)
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            if (actionState == ACTION_STATE_SWIPE) {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dy: Float, actionState: Int, isCurrentlylActive: Boolean) {
            recyclerView.setOnTouchListener { view, motionEvent ->
                swipeBack = motionEvent.action == MotionEvent.ACTION_CANCEL || motionEvent.action == MotionEvent.ACTION_UP
                return@setOnTouchListener false
            }
        }
    }
}