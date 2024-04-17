package com.calssy.encrypttext;

import static com.calssy.encrypttext.CryptoUtil.encrypt;
import static com.calssy.encrypttext.MakeMyAppMoreHardToRead.keyData;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ImageProcessor {


    public static String filterInt(int size) {
        StringBuilder asciiChars = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i <= 127) { // Checking if the index i is within ASCII range
                asciiChars.append((char)i); // Appending ASCII character corresponding to the index i
            }
        }
        return asciiChars.toString();
    }


    public static Bitmap encode(Bitmap image, String message) {
        int bitmapKey= keyData(message.length());
        Bitmap mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true);
        int messageIndex = keyData(0);
        int[] pixels = new int[mutableBitmap.getWidth() * mutableBitmap.getHeight()];
        mutableBitmap.getPixels(pixels, 0, mutableBitmap.getWidth(), 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());

        for (int i = 0; i < pixels.length && messageIndex < message.length() * 8; i++) {
            int pixel = pixels[i];
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            for (int j = 0; j < 8 && messageIndex < message.length() * 8; j++) {
                char messageChar = message.charAt(messageIndex / 8);
                int bitIndex = 7 - (messageIndex % 8);
                red = (red & ~(1 << bitIndex)) | (((messageChar >> j) & 1) << bitIndex);
                green = (green & ~(1 << bitIndex)) | (((messageChar >> j) & 1) << bitIndex);
                blue = (blue & ~(1 << bitIndex)) | (((messageChar >> j) & 1) << bitIndex);
                messageIndex++;
            }
            filterInt(bitmapKey);
            pixels[i] = Color.rgb(red, green, blue);
        }

        mutableBitmap.setPixels(pixels, 0, mutableBitmap.getWidth(), 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight());
        return mutableBitmap;
    }

    public static String decode(Bitmap image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        StringBuilder message = new StringBuilder();

        for (int pixel : pixels) {
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            char messageChar = 0;

            for (int j = 0; j < 8; j++) {
                int bitIndex = 7 - j;
                messageChar |= (char) (((red >> bitIndex) & 1) << j);
                messageChar |= (char) (((green >> bitIndex) & 1) << j);
                messageChar |= (char) (((blue >> bitIndex) & 1) << j);
            }

            message.append(messageChar);
        }

        return filterNonASCII(message.toString());
    }

    public static String filterNonASCII(String input) {
        StringBuilder asciiChars = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((int) c <= 127) {
                asciiChars.append(c);
            }
        }
        return getSubstring(asciiChars.toString());
    }

    public static String getSubstring(String str) {
        int index = str.indexOf('`');
        if (index != -1) {
            return getSubstring2(str.substring(0, index));
        } else {
            return str;
        }
    }
    public static String getSubstring2(String str) {
        int index = str.indexOf('@');
        if (index != -1) {
            return str.substring(0, index);
        } else {
            return str;
        }
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
