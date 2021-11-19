package ca.cmpt276.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityTasksBinding;

public class TasksActivity extends AppCompatActivity {

    ArrayList<String> tasksNames = new ArrayList<>();
    private static final String TASKS_NAMES_PREFS = "TasksNamesPrefs";
    private static final String TASKS_PREFS = "TasksPrefs";
    ArrayList<String> childrenNames = new ArrayList<>();
    private static final String NAME_PREF = "NamePrefs";
    private static final String NAMES_PREF = "NamesSizePref";
    private Button addTaskButton;
    private Button removeTaskButton;
    private Button editTaskButton;
    private EditText taskName;
    private ListView tasksList;
    private String nameOfTask;
    private int positionOfTask;
    ArrayAdapter<String> tasksAdapter;


    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, TasksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.cmpt276.as3.parentapp.databinding.ActivityTasksBinding binding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // https://www.geeksforgeeks.org/how-to-change-the-color-of-action-bar-in-an-android-app/
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(getString(R.string.yellow_brown_color)));
        actionBar.setBackgroundDrawable(colorDrawable);

        loadTasksNames();
        loadChildrenData();

        tasksList = findViewById(R.id.listTasks);
        taskName = findViewById(R.id.editTaskName);
        addTaskButton = findViewById(R.id.btnAddTask);
        removeTaskButton = findViewById(R.id.btnRemoveTask);
        editTaskButton = findViewById(R.id.btnEditTask);

        // populate list of tasks
        populateTasksList();

        handleAddTaskButton();
        handleEditTaskButton();
        handleRemoveTaskButton();

        saveTasksNames();
        saveChildrenData();
    }

    private void saveTasksNames() {
        SharedPreferences sharedPreferences = getSharedPreferences(TASKS_NAMES_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasksNames);
        editor.putString(TASKS_PREFS, json);
        editor.apply();
    }

    private void loadTasksNames() {
        SharedPreferences sharedPreferences = getSharedPreferences(TASKS_NAMES_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TASKS_PREFS, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        tasksNames = gson.fromJson(json, type);

        if (tasksNames == null) {
            tasksNames = new ArrayList<>();
        }
    }

    private void populateTasksList() {
        tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasksNames);
        tasksList.setAdapter(tasksAdapter);
        tasksList.setOnItemClickListener((parent, view, position, id) -> {
            taskName.setText(tasksNames.get(position));
            // sets cursor to the right of name when name is clicked upon
            if (taskName.getText().length() > 0) {
                taskName.setSelection(taskName.getText().length());
            }
        });
    }

    private void handleAddTaskButton() {
        addTaskButton.setOnClickListener(v -> {
            addTask();
            // clear edit text after adding name
            taskName.getText().clear();
            saveTasksNames();
        });
    }

    private void handleEditTaskButton() {
        editTaskButton.setOnClickListener(v -> {
            editTask();
            // clear edit text after editing name
            taskName.getText().clear();
            saveTasksNames();
        });
    }

    private void handleRemoveTaskButton() {
        removeTaskButton.setOnClickListener(v -> {
            removeTask();
            // clear edit text after removing name
            taskName.getText().clear();
            saveTasksNames();
        });
    }

    private void addTask() {
        nameOfTask = taskName.getText().toString();

        if (!nameOfTask.equals("")) {
            tasksAdapter.add(nameOfTask);
            // refresh
            tasksAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, getString(R.string.non_empty_name), Toast.LENGTH_SHORT).show();
        }
    }

    private void editTask() {
        nameOfTask = taskName.getText().toString();
        positionOfTask = tasksList.getCheckedItemPosition();

        if (!nameOfTask.equals("")) {
            tasksAdapter.remove(tasksNames.get(positionOfTask));
            tasksAdapter.insert(nameOfTask, positionOfTask);
            // refresh
            tasksAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, getString(R.string.non_empty_name), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeTask() {
        positionOfTask = tasksList.getCheckedItemPosition();

        if (positionOfTask >= 0) {
            tasksAdapter.remove(tasksNames.get(positionOfTask));
            // refresh
            tasksAdapter.notifyDataSetChanged();
        }
    }

    // save & load inspired by https://www.youtube.com/watch?v=jcliHGR3CHo&t=343s
    private void saveChildrenData() {
        SharedPreferences sharedPreferences = getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(childrenNames);
        editor.putString(NAME_PREF, json);
        editor.apply();
    }

    private void loadChildrenData() {
        SharedPreferences sharedPreferences = getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NAME_PREF, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        childrenNames = gson.fromJson(json, type);

        if (childrenNames == null) {
            childrenNames = new ArrayList<>();
        }
    }

}