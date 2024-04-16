package com.calssy.encrypttext;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

        // Load the image from external storage
        loadBitmapFromExternalStorage("coin.png");

        // Set up button click listener
        btnRetrieveText.setOnClickListener(v -> retrieveTextFromImage());

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

                    }else {
                        textView.setText("Unable to retrieve text from the image.");
                    }
                }
            });
        }).start();
    }

    private void loadBitmapFromExternalStorage(String filename) {
//        Bitmap bitmap = imageProcessor.loadBitmapFromExternalStorage(filename);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//        }
    }
}
