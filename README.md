# EncryptText App - Steganography for Android

EncryptText is an Android application that demonstrates steganography by allowing users to encode and decode text messages hidden within bitmap images. This application features a bottom navigation interface with multiple fragments for adding and retrieving hidden messages. It also supports secure storage and retrieval of these encoded bitmap images from Firebase Realtime Database with additional AES encryption.

## Project Overview

Steganography, the practice of hiding secret messages within other non-secret data, is a key concept in this project. With EncryptText, users can:

- Encode text messages within bitmap images.
- Retrieve and decode the hidden messages from bitmap images.
- Securely store and retrieve these encoded images from Firebase Realtime Database.
- Encrypt and decrypt stored data using AES encryption with predefined parameters.

## Project Structure

- `MainActivity.java`: The main activity with a bottom navigation view, allowing users to switch between different steganography operations.
- `AddTextFragment.java` and `GetTextFragment.java`: Fragments for encoding text into images and retrieving text from images, respectively.
- `ImageProcessor.java`: Contains the steganography logic for encoding and decoding messages in bitmap images.
- `CryptoUtil.java`: Handles encryption and decryption tasks.
- `ImageUtil.java`: Utility functions for converting between bitmap and Base64, along with other related operations.
- `EncodeDecodeTest.java`: JUnit test cases for verifying the steganography implementation.

## Features

- **Steganography**: Encode and decode text messages within bitmap images. The encoded images can be visually similar to regular images but contain hidden text data.
- **Firebase Integration**: Securely store and retrieve encoded images in Firebase Realtime Database, enabling data persistence.
- **AES Encryption**: Encrypt and decrypt data using AES with specific salt and initialization vector, adding an extra layer of security.
- **Bottom Navigation**: Simple navigation between different steganography operations.

## Getting Started

To run the project locally, ensure you have the following:

1. [Android Studio](https://developer.android.com/studio) installed on your system.
2. A Firebase project configured with Realtime Database for storing and retrieving encoded images.
3. Appropriate permissions in your AndroidManifest.xml:
   ```xml
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
How to Use

Add Text Fragment: Navigate to this fragment to encode a text message into a bitmap image. You can then store the encoded image in Firebase Realtime Database for later retrieval.
Get Text Fragment: Navigate here to retrieve a bitmap image from Firebase and decode the hidden text message within it.
Testing

Unit tests for verifying the steganography implementation are provided in EncodeDecodeTest.java. To run the tests:

Open Android Studio.
Right-click on EncodeDecodeTest and select "Run EncodeDecodeTest".
Confirm that all test cases pass.
Contributing

Contributions to this steganography project are welcome! If you'd like to contribute, please fork the repository and submit a pull request with a detailed description of your changes.

License

This project is licensed under the MIT License.
