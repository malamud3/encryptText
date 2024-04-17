package com.calssy;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import com.calssy.encrypttext.ImageProcessor;


@RunWith(AndroidJUnit4.class)
public class Test_encode_decode {
    private ImageProcessor imageProcessor = new ImageProcessor();

    @Test
    public void testEncode() {
        // Create a Bitmap image
        Bitmap image = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.setPixel(i, j, Color.rgb(i, j, i + j));
            }
        }

        // Create a message string
        String message = "Hello, World!";

        // Call the encode function
        Bitmap encodedImage = imageProcessor.encode(image, message);

        // Check that the returned image is not null and has the same dimensions as the input image
        assertNotNull(encodedImage);
        assertEquals(image.getWidth(), encodedImage.getWidth());
        assertEquals(image.getHeight(), encodedImage.getHeight());
    }

    @Test
    public void testDecode() {
        // Create a Bitmap image
        Bitmap image = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.setPixel(i, j, Color.rgb(i, j, i + j));
            }
        }

        // Create a message string
        String message = "Hello";

        // Call the encode function
        Bitmap encodedImage = ImageProcessor.encode(image, message);

        // Call the decode function
        String decodedMessage = ImageProcessor.decode(encodedImage);
        System.out.println("Decoded message size: " + decodedMessage.length());
        System.out.println(decodedMessage);

        // Check that the returned message is equal to the original message
        assertEquals(message, decodedMessage);
    }
}