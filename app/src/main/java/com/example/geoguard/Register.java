package com.example.geoguard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText editTextEmail, editTextName, editTextPass;
    Button btRegister, btLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    DatabaseReference databaseReference;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Reference to 'users' node

        editTextEmail = findViewById(R.id.etEmail);
        editTextPass = findViewById(R.id.etPass);
        editTextName = findViewById(R.id.etName);
        btRegister = findViewById(R.id.btRegister1);
        btLogin = findViewById(R.id.btLogin1);
        progressBar = findViewById(R.id.progressBar);

        btLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });

        btRegister.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String etEmail = editTextEmail.getText().toString().trim();
            String etName = editTextName.getText().toString().trim();
            String etPass = editTextPass.getText().toString().trim();

            if (TextUtils.isEmpty(etName)) {
                Toast.makeText(Register.this, "Enter Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(etEmail)) {
                Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(etPass)) {
                Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(etEmail, etPass)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();

                                // Store user data in Realtime Database
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", etName);
                                userMap.put("email", etEmail);
                                userMap.put("userId", userId);

                                databaseReference.child(userId).setValue(userMap)
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_LONG).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());

                                startActivity(new Intent(Register.this, MainActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(Register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        });
    }
}
