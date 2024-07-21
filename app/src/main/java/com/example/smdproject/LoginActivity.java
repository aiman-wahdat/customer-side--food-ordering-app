package com.example.smdproject;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView tvSignUp;
    AppCompatButton btnLogin;
    EditText editTextEmail,editTextPassword;
    ProgressBar progressBar;
    current_user_singleton currentUser = current_user_singleton.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSignUp = findViewById(R.id.tvSignup);
        btnLogin = findViewById(R.id.btnLogin);
        editTextEmail=findViewById(R.id.ET_email);
        editTextPassword=findViewById(R.id.ET_password);
        progressBar=findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredEmail = editTextEmail.getText().toString().trim();
                String enteredPassword = editTextPassword.getText().toString().trim();
                if ( enteredEmail.isEmpty() || enteredPassword.isEmpty() ) {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);
                loginUser(enteredEmail, enteredPassword, "users");



            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignUpActivity when tvSignUp is clicked
                Intent intent = new Intent(LoginActivity.this, RegistrationDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
    // Method to authenticate user and perform login
    private void loginUser(String email, String password, String userType) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Login successful, now retrieve user data from database
                    retrieveUserData(email, userType);
                    currentUser.setUserId(email);
                })
                .addOnFailureListener(e -> {
                    // Login failed, show error message
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                });

    }

    // Method to retrieve user data from Firebase Realtime Database
    private void retrieveUserData(String email, String userType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(userType);

        // Use user's email as the key in the database (replace '.' with '_' for the key)
        String emailKey = email.replace(".", "_");

        DatabaseReference userNodeRef = userRef.child(emailKey);

        userNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data found, retrieve it
                    User user = dataSnapshot.getValue(User.class);
                    SharedPreferences sPref = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    // Now you can use 'user' object for further operations (e.g., navigate to user's dashboard)
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("isLogin", true);
                    editor.putString("userId",email);
                    editor.apply();

                    //Toast.makeText(LoginActivity.this,"inLogin "+sPref.getBoolean("isLogin",false),Toast.LENGTH_SHORT).show();

                    // Example: Navigate to user's dashboard activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // User data not found
                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error occurred while retrieving data
                Toast.makeText(LoginActivity.this, "Failed to retrieve user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                btnLogin.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}