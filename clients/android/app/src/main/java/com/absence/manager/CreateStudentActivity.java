package com.absence.manager;

import static com.absence.manager.R.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateStudentActivity extends AppCompatActivity {
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        Client client = Client.getInstance(); // Initialize your client here

        EditText nameField = findViewById(R.id.name_field);
        Button validateButton = findViewById(R.id.button_validate);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(CreateStudentActivity.this, "Please enter a name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean result = client.createStudent(name);
                if (result) {
                    Toast.makeText(CreateStudentActivity.this, "Student created successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateStudentActivity.this, "Failed to create student.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
