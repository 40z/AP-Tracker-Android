package com.movsoft.aptracker.scenes.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.movsoft.aptracker.R
import com.movsoft.aptracker.services.RafiTrackingServices
import com.movsoft.aptracker.services.SharedPreferencesTrackedItemServices
import kotlinx.android.synthetic.main.activity_main.*

class TrackerListActivity : AppCompatActivity(), TrackerListViewModel.Listener {

    lateinit var viewModel: TrackerListViewModel

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        recyclerView = findViewById(R.id.tracker_list)

        fab.setOnClickListener { view ->
            showAddTrackedItemDialog()
        }

        val trackingServices = RafiTrackingServices(this)
        val trackedItemProvider = SharedPreferencesTrackedItemServices(this)
        viewModel = TrackerListViewModel(trackingServices, trackedItemProvider, this)

        val touchHelper = ItemTouchHelper(TrackerListAdapter.SwipeController())
        touchHelper.attachToRecyclerView(recyclerView)

        val adapter = TrackerListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.trackedItems.observe(this, adapter)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    // TrackerListViewModel.Listener Conformance


    override fun showMessage(message: String) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showError(message: String) {
        val snackbar = Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
        val redColor = resources.getColor(android.R.color.holo_red_dark)
        snackbar.view.setBackgroundColor(redColor)
        snackbar.show()
    }


    // UI


    private fun showAddTrackedItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_trackable, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.edit_text)
        dialogEditText.requestFocus()

        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Add New Tracked Item")
        dialog.setView(dialogView)
        dialog.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add") { _, _ ->
            viewModel.addItem(dialogEditText.text.toString())
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") {_, _-> }
        dialog.show()
    }
}
