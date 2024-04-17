package com.calssy.encrypttext;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AddTextFragment extends Fragment {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private EditText editTextLarge;
    private Button btnInsert;
    private ImageView imageView;
    private ImageProcessor imageProcessor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_text, container, false);

        // Initialize views
        imageView = view.findViewById(R.id.imageView);
        editTextLarge = view.findViewById(R.id.editTextLarge);
        btnInsert = view.findViewById(R.id.btnInsert);

        // Initialize ImageProcessor
        imageProcessor = new ImageProcessor();

        // Set click listener for the button
        btnInsert.setOnClickListener(v -> {
            // Get the text from the EditText
            String text = editTextLarge.getText().toString();

            Bitmap currentImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            Bitmap encodedImage = ImageProcessor.encode(currentImage, text);

            // Save the encoded image to external storage
            try {
                imageProcessor.saveBitmapToFirebaseStorage(encodedImage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Set the new image to the ImageView
            imageView.setImageBitmap(encodedImage);

            // Show a toast message
            showToast();
        });

        return view;
    }

    private void showToast() {
        Toast.makeText(getActivity(), "Text inserted into image and saved to external storage", Toast.LENGTH_SHORT).show();
    }
}
