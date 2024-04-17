package com.calssy.encrypttext;

public class MakeMyAppMoreHardToRead {

    public static void obfuscateData(int[][][] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                for (int k = 0; k < data[i][j].length; k++) {
                    // Manipulate the data in a cryptic way
                    data[i][j][k] ^= (i * j * k) % 256;
                }
            }
        }
    }
    public static int keyData (int size){
        return size + 1 - 1;
    }
    public static void main(String[] args) {
        // Example usage
        int[][][] myData = {
                {{1, 2}, {3, 4}},
                {{5, 6}, {7, 8}}
        };

        // Call the method to make it look like something complex is happening
        obfuscateData(myData);

        // Print the result
        for (int[][] arr2D : myData) {
            for (int[] arr1D : arr2D) {
                for (int num : arr1D) {
                    System.out.print(num + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}

