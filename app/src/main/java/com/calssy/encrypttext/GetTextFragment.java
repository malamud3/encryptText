package com.calssy.encrypttext;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class GetTextFragment extends Fragment {

    private ImageView imageView;
    private Button btnRetrieveText;
    private ImageProcessor imageProcessor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_text, container, false);

        // Initialize views
        imageView = view.findViewById(R.id.imageView);
        btnRetrieveText = view.findViewById(R.id.btnRetrieveText);

        // Initialize ImageProcessor
        imageProcessor = new ImageProcessor();

        // Set up button click listener
        btnRetrieveText.setOnClickListener(v -> retrieveTextFromImage());

        // Retrieve bitmap from Firebase Storage
        retrieveBitmapFromFirebaseStorage();

        return view;
    }

    // Method to retrieve text from image
    private void retrieveTextFromImage() {
        // Get the current image from the ImageView
        Bitmap currentImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        new Thread(() -> {
            final String decodedText = imageProcessor.decode(currentImage);

            requireActivity().runOnUiThread(() -> {
                TextView textView = requireView().findViewById(R.id.textViewRetrievedText);
                if (decodedText != null && !decodedText.isEmpty()) {
                    // Display the retrieved text in the TextView
                    textView.setText("Retrieved Text: " + decodedText);
                } else {
                    // Unable to retrieve text
                    if(decodedText.isEmpty()){
                        textView.setText("Empty");
                    } else {
                        textView.setText("Unable to retrieve text from the image.");
                    }
                }
            });
        }).start();
    }
    // Listener interface to handle Bitmap retrieval results

// Method to retrieve bitmap from Firebase Storage
private void retrieveBitmapFromFirebaseStorage() {
    imageProcessor.retrieveBitmapFromFirebaseStorage(new ImageProcessor.BitmapCallback() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap) {
            if (bitmap != null) {
                Log.e("FirebaseStorage", "Retrieved bitmap ");
                // Set the retrieved bitmap to the ImageView
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e("FirebaseStorage", "Retrieved bitmap is null");
                Toast.makeText(requireContext(), "Failed to retrieve image from Firebase Storage", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(Exception e) {
            // Log the error
            Log.e("FirebaseStorage", "Failed to retrieve image from Firebase Storage", e);
            // Show error message to the user
            Toast.makeText(requireContext(), "Failed to retrieve image from Firebase Storage", Toast.LENGTH_SHORT).show();
        }
    });
}

}
