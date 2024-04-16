package com.calssy.encrypttext;

import static com.calssy.encrypttext.CryptoUtil.encrypt;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ImageProcessor {
    // Listener interface to handle Bitmap retrieval results
    public interface OnBitmapRetrievedListener {
        void onBitmapRetrieved(Bitmap bitmap);
        void onBitmapRetrievalFailed(Exception e);
    }

    public Bitmap encode(Bitmap image, String message) {
        Bitmap mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true);

        int messageIndex = 0;
        int messageLength = message.length();

        int[] pixels = new int[mutableBitmap.getWidth() * mutableBitmap.getHeight()];
        mutableBitmap.getPixels(pixels, 0, mutableBitmap.getWidth(), 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());

        for (int i = 0; i < pixels.length && messageIndex < messageLength; i++) {
            int pixel = pixels[i];

            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            char messageChar = message.charAt(messageIndex);

            red = (red & 0xFE) | ((messageChar >> 7) & 0x1);
            green = (green & 0xFE) | ((messageChar >> 6) & 0x1);
            blue = (blue & 0xFE) | ((messageChar >> 5) & 0x1);

            pixels[i] = Color.rgb(red, green, blue);

            messageChar = message.charAt(++messageIndex);

            red = (red & 0xFE) | ((messageChar >> 4) & 0x1);
            green = (green & 0xFE) | ((messageChar >> 3) & 0x1);
            blue = (blue & 0xFE) | ((messageChar >> 2) & 0x1);

            pixels[++i] = Color.rgb(red, green, blue);

            messageChar = message.charAt(messageIndex++);

            red = (red & 0xFE) | ((messageChar >> 1) & 0x1);
            blue = (blue & 0xFE) | (messageChar & 0x1);

            pixels[++i] = Color.rgb(red, green, blue);

            messageIndex++;
        }

        mutableBitmap.setPixels(pixels, 0, mutableBitmap.getWidth(), 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());

        return mutableBitmap;
    }

    public String decode(Bitmap image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        StringBuilder message = new StringBuilder();

        for (int i = 0; i < pixels.length; i += 3) {
            int pixel1 = pixels[i];
            int pixel2 = pixels[i + 1];
            int pixel3 = pixels[i + 2];

            int red1 = Color.red(pixel1);
            int green1 = Color.green(pixel1);
            int blue1 = Color.blue(pixel1);

            int red2 = Color.red(pixel2);
            int green2 = Color.green(pixel2);
            int blue2 = Color.blue(pixel2);

            int red3 = Color.red(pixel3);
            int blue3 = Color.blue(pixel3);

            char messageChar = (char) (((red1 & 0x1) << 7) | ((green1 & 0x1) << 6) | ((blue1 & 0x1) << 5)
                    | ((red2 & 0x1) << 4) | ((green2 & 0x1) << 3) | ((blue2 & 0x1) << 2)
                    | ((red3 & 0x1) << 1) | (blue3 & 0x1));

            message.append(messageChar);
        }

        return message.toString();
    }

    void saveBitmapToFirebaseStorage(Bitmap bitmap) throws Exception {
        String base64String = ImageUtil.convert(bitmap);
        String encryptedString = encrypt(base64String);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("images");

        // Check if "coin" file already exists
        databaseReference.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // "coin" file already exists, update its value
                    databaseReference.child("coin").setValue(encryptedString);
//                    showToast("Image updated in Firebase Storage");
                } else {
                    // "coin" file doesn't exist, add a new entry
                    databaseReference.child("coin").setValue(encryptedString);
//                    showToast("Image saved to Firebase Storage");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                showToast("Failed to save image to Firebase Storage");
            }
        });
    }


    // Method to retrieve encrypted image data from Firebase Storage, decrypt it, and convert it to a Bitmap
    public Bitmap retrieveBitmapFromFirebaseStorage() throws Exception {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("images");
        // Get the encrypted image data from Firebase Storage
        DataSnapshot dataSnapshot = databaseReference.child("coin").get().getResult();

        if (dataSnapshot.exists()) {
            // Image data found, retrieve and decrypt it
            String encryptedString = dataSnapshot.getValue(String.class);
            String decryptedString = CryptoUtil.decrypt(encryptedString);
            // Convert the decrypted string back to a Bitmap
            return ImageUtil.convert(decryptedString);
        } else {
            // Handle case where image data doesn't exist
            throw new Exception("Image data not found");
        }
    }

}
