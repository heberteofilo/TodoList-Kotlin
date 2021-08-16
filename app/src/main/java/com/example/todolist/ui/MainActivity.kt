package com.example.todolist.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.datasource.TaskDataSource

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    // lazy vai esperar os atributos para fazer a instanciação
    private val adapter by lazy { TaskListAdapter() }

    private val requestTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            updateTaskList()
        } else if (result.resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(applicationContext, "Task canceled", Toast.LENGTH_LONG).show()
        else Toast.makeText(applicationContext, "Unreported task", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // atribuindo o adapter
        binding.rvTasks.adapter = adapter
        updateTaskList()
        insertListerners()
    }

    private fun updateTaskList() {
        val list = TaskDataSource.getList()

        if (list.isEmpty()) binding.incEmpty.emptyState.visibility = View.VISIBLE
        else binding.incEmpty.emptyState.visibility = View.GONE

        binding.rvTasks.adapter = adapter
        adapter.submitList(list)
        // DATA STORE
        // ROOM
    }


    private fun insertListerners() {

        binding.fab.setOnClickListener {
            // acessar outra view
//            startActivity(Intent(this,AddTaskActivity::class.java))
            val intent = Intent(this, AddTaskActivity::class.java)
            requestTask.launch(intent)
        }

        adapter.listenerEdit = {
            val editIntent = Intent(this, AddTaskActivity::class.java)
            editIntent.putExtra(AddTaskActivity.TASK_ID, it.id)
            requestTask.launch(editIntent)
        }

        adapter.listenerDelete = {
            TaskDataSource.deleteTask(it)
            updateTaskList()
        }
    }
}