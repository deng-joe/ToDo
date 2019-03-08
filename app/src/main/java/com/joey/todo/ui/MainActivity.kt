package com.joey.todo.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joey.todo.R
import com.joey.todo.adapter.TaskAdapter
import com.joey.todo.room.Item
import com.joey.todo.viewmodel.TaskViewModel
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private var tag = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        initUI()
    }

    private fun initUI() {
        val recyclerView = findViewById<RecyclerView>(R.id.tasks)
        val taskAdapter = TaskAdapter(this)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        taskViewModel.allItems.observe(this, Observer { items ->
            items?.let { taskAdapter.setItems(it) }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.delete_all) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you really sure you want to delete all tasks?")
            builder.setPositiveButton("OK") {_, _ ->
                taskViewModel.deleteAll()
                Toasty.success(this, "All tasks deleted.", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            builder.show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val item = Item(
                    it.getStringExtra(NewTaskActivity.EXTRA_NAME),
                    it.getStringExtra(NewTaskActivity.EXTRA_DESC),
                    it.getStringExtra(NewTaskActivity.EXTRA_DATE)
                )

                taskViewModel.insert(item)
            }
        }
    }

    companion object {
        const val ADD_TASK_REQUEST_CODE = 1
        const val EDIT_TASK_REQUEST_CODE = 2
    }
}
