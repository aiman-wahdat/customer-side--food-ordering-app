package com.example.smdproject;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RestaurantAdminRegistrationFragment extends Fragment {

    private static final int REQUEST_IMAGE_GALLERY = 1;

    private ImageView ivUploadImage;
    private Bitmap selectedBitmap;
    private String imageUrl;
    TextInputEditText etName, etEmail, etPassword, etConfirmPassword, etAddress,ET_deliverTime,ET_deliverFee;
    Button btnSignUp;
    FirebaseAuth mAuth;



    public RestaurantAdminRegistrationFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_restaurant_admin_registration, container, false);

        mAuth = FirebaseAuth.getInstance();

        etName = view.findViewById(R.id.ET_name);
        etEmail = view.findViewById(R.id.ET_email);
        etPassword = view.findViewById(R.id.ET_password);
        etConfirmPassword = view.findViewById(R.id.ET_Confirmpassword);
        etAddress = view.findViewById(R.id.ET_address);
        btnSignUp = view.findViewById(R.id.btn_SignUp);
        ET_deliverFee = view.findViewById(R.id.ET_deliverFee);
        ET_deliverTime = view.findViewById(R.id.ET_deliverTime);
        ivUploadImage = view.findViewById(R.id.ivUploadImage);

        ivUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            // Image selected from gallery
            if (data != null && data.getData() != null) {
                try {
                    // Assign the selected bitmap to the member variable
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    ivUploadImage.setImageBitmap(selectedBitmap);

                    // Convert Bitmap to byte array and save to Firebase Storage and Realtime Database
                    //saveImageToFirebase(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveImageToFirebase(String name,String email,String password,String address, int deliveryTime,float deliveryFee) {
        // Get Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a unique filename for the image using current timestamp
        String imageName = System.currentTimeMillis() + ".jpg";

        String emailKey = email.replace(".", "_");

        // Construct the StorageReference path with restaurant's email ID
        String storagePath = "restaurant_images/" + emailKey + "/" + imageName;

        // Create StorageReference for the image
        StorageReference imageRef = storageRef.child(storagePath);


        // Convert Bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Upload image data to Firebase Storage
        imageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, now get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        saveUsertoDatabase(name,email,password,address,deliveryTime,deliveryFee);
                        // Save image URL to Firebase Realtime Database (or perform other actions)
                        //saveImageUrlToDatabase(imageUrl,email);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Failed to upload image: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
    /*private void saveImageUrlToDatabase(String imageUrl,String emailKey) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Restaurants");

        // Reference the restaurant's node in the database
        DatabaseReference restaurantNodeRef = usersRef.child(emailKey);

        // Update the image URL for the restaurant
        restaurantNodeRef.child("imageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Image URL saved successfully
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() -> {
                            // Toast.makeText(getContext(), "Image URL saved successfully", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        // Toast.makeText(getContext(), "Failed to save image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }*/
    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String deliveryFeeStr =ET_deliverFee.getText().toString().trim();
        String deliveryTimeStr  = ET_deliverTime.getText().toString().trim();

        float deliveryFee;
        int deliveryTime;

        deliveryFee = Float.parseFloat(deliveryFeeStr);
        deliveryTime = Integer.parseInt(deliveryTimeStr);

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || address.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etPassword.setError("Passwords do not match");
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            saveImageToFirebase(name,email,password,address,deliveryTime,deliveryFee);
                        } else {
                            Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Authentication failed: " , Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void saveUsertoDatabase(String name,String email,String password,String address, int deliveryTime,float deliveryFee){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Restaurants");

        // Assuming user registration is successful and you have user data
        double rating = 0.0;
        float floatRating = (float) rating;
        Restaurant newUser = new Restaurant(name, deliveryFee, deliveryTime, floatRating, password, email, address, imageUrl);  // Create a new User object

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
                            //Toast.makeText(getContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        // Toast.makeText(getContext(), "Failed to register user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }});
    }

}