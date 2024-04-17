package com.calssy.encrypttext;

import static com.calssy.encrypttext.CryptoUtil.encrypt;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ImageProcessor {


    public Bitmap encode(Bitmap image, String message) {
        if (message.length() > image.getWidth() * image.getHeight() / 3) {
            throw new IllegalArgumentException("Message is too long to be encoded in the image");
        }

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

            if (++messageIndex < messageLength) {
                messageChar = message.charAt(messageIndex);

                red = (red & 0xFE) | ((messageChar >> 4) & 0x1);
                green = (green & 0xFE) | ((messageChar >> 3) & 0x1);
                blue = (blue & 0xFE) | ((messageChar >> 2) & 0x1);

                pixels[++i] = Color.rgb(red, green, blue);
            }

            if (++messageIndex < messageLength) {
                messageChar = message.charAt(messageIndex);

                red = (red & 0xFE) | ((messageChar >> 1) & 0x1);
                blue = (blue & 0xFE) | (messageChar & 0x1);

                pixels[++i] = Color.rgb(red, green, blue);
            }

            messageIndex++;
        }

        mutableBitmap.setPixels(pixels, 0, mutableBitmap.getWidth(), 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());

        return mutableBitmap;
    }

    public String decode(Bitmap image) {
        StringBuilder message = new StringBuilder();

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];

            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            char messageChar = (char) (((red & 0x1) << 7) | ((green & 0x1) << 6) | ((blue & 0x1) << 5));

            if (++i < pixels.length) {
                pixel = pixels[i];

                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                messageChar |= (char) (((red & 0x1) << 4) | ((green & 0x1) << 3) | ((blue & 0x1) << 2));
            }

            if (++i < pixels.length) {
                pixel = pixels[i];

                red = Color.red(pixel);
                blue = Color.blue(pixel);

                messageChar |= (char) (((red & 0x1) << 1) | (blue & 0x1));
            }

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
               Log.d("FireBase", "Failed to save image to Firebase Storage");
            }
        });
    }

    public interface BitmapCallback {
        void onBitmapLoaded(Bitmap bitmap);
        void onError(Exception e);
    }

    public void retrieveBitmapFromFirebaseStorage(BitmapCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("images");
        databaseReference.child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String encryptedString = dataSnapshot.getValue(String.class);
                    try {
                        String decryptedString = CryptoUtil.decrypt(encryptedString);
                        Bitmap bitmap = ImageUtil.convert(decryptedString);
                        callback.onBitmapLoaded(bitmap);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                } else {
                    callback.onError(new Exception("Image data not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
}
