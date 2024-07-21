package com.example.smdproject;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    EditText etName,etAddress,etPhone;
    TextView etEmail;
    Button btnLogout , saveUserInformationButton;

    current_user_singleton currentUser = current_user_singleton.getInstance();

    public ProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        etName = rootView.findViewById(R.id.profileName);
        etEmail = rootView.findViewById(R.id.profileEmail);
        etAddress = rootView.findViewById(R.id.profileAddress);
        etPhone = rootView.findViewById(R.id.profilePhoneNumber);
        saveUserInformationButton = rootView.findViewById(R.id.saveUserInformationButton);
        btnLogout=rootView.findViewById(R.id.btnLogout);

        String userId = currentUser.getUserId();
        String emailKey = userId.replace(".", "_");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Reference the user's node in the database
        DatabaseReference userNodeRef = usersRef.child(emailKey);

        // Retrieve the user data from the database
        userNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data found, dataSnapshot contains the user object
                    User user = dataSnapshot.getValue(User.class);
                    etEmail.setText(user.getEmail());
                    etName.setText(user.getName());
                    etPhone.setText(user.getPhone());
                    etAddress.setText(user.getAddress());

                } else {
                    // User data doesn't exist
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to retrieve user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveUserInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etName.getText().toString().trim();
                String newAddress = etAddress.getText().toString().trim();
                String newPhone = etPhone.getText().toString().trim();

                // Check if any field is empty
                if (newName.isEmpty()  || newAddress.isEmpty() || newPhone.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if any field is empty
                }

                // Update user information in the database
                userNodeRef.child("name").setValue(newName);
                userNodeRef.child("address").setValue(newAddress);
                userNodeRef.child("phone").setValue(newPhone)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "User information updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to update user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sPref = requireContext().getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putBoolean("isLogin", false);
                editor.putString("userId","none");
                editor.apply();
                currentUser.logout();
                Intent intent = new Intent(requireActivity(),splashScreen.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });


        return rootView;
    }
}