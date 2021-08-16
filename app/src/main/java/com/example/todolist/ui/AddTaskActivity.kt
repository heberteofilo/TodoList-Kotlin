package com.example.todolist.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.model.Task
import com.example.todolist.extensions.format
import com.example.todolist.extensions.text
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            // se não vir Id ele pega como padrão 0
            val taskId = intent.getIntExtra(TASK_ID, 0)
            // pode ser nulo
            TaskDataSource.findById(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilHour.text = it.hour
            }
        }

        insertListeners()
    }


    private fun insertListeners() {

        binding.toolbar.setOnClickListener {
            setResult(BACK_BUTTON)
            finish()
        }

        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                // Arrumar timezone
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                // extensions text
                // .editText?.text?.setText(Data(if).format())
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }
        binding.tilHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val minute = if (timePicker.minute in 0..9) "0${timePicker.minute}" else "${timePicker.minute}"
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else "${timePicker.hour}"
                binding.tilHour.text = "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, "TIME_PICKER_TAG")
        }

        binding.mbCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.mbCreateTask.setOnClickListener {
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
            // melhoria para a edição da task
                id = intent.getIntExtra(TASK_ID,0)
            )
            TaskDataSource.insertTask(task)
            // confirmar o result
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(BACK_BUTTON)
        finish()
    }

    companion object {
        const val BACK_BUTTON = 321
        const val TASK_ID = "task_id"
    }
}