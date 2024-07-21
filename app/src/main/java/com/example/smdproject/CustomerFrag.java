package com.example.smdproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class CustomerFrag extends Fragment {


    TextInputEditText etName, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    Button btnSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    public CustomerFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(), "Welcome", Toast.LENGTH_LONG).show();
        View view = inflater.inflate(R.layout.fragment_customer, container, false);

        mAuth = FirebaseAuth.getInstance();

        etName = view.findViewById(R.id.ET_name);
        etEmail = view.findViewById(R.id.ET_email);
        etPassword = view.findViewById(R.id.ET_password);
        etConfirmPassword = view.findViewById(R.id.ET_Confirmpassword);
        etPhone = view.findViewById(R.id.ET_Phone);
        etAddress = view.findViewById(R.id.ET_address);
        btnSignUp = view.findViewById(R.id.btn_SignUp);
        progressBar=view.findViewById(R.id.progressBar);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etPassword.setError("Passwords do not match");
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.GONE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Assuming user registration is successful and you have user data
        User newUser = new User(name, email, address, password, phone);  // Create a new User object

        // Use user's email as the key in the database (replace '.' with '_' for the key)
        String emailKey = newUser.getEmail().replace(".", "_");

        // Reference the user's node in the database
        DatabaseReference userNodeRef = usersRef.child(emailKey);

        // Set the user data in the database under the user's node
        userNodeRef.setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    // User data saved successfully
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to register user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Registration success
                            // You can save additional user information to Firebase Realtime Database or Firestore here
                            Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(getContext(), "Authentication failed: " , Toast.LENGTH_SHORT).show();
                    }
                });
    }

}