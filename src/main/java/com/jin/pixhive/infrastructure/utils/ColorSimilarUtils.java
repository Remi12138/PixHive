package com.jin.pixhive.infrastructure.utils;

import java.awt.*;

/**
 * Calculate color similarity
 */
public class ColorSimilarUtils {

    private ColorSimilarUtils() {
        // Utility classes do not need to be instantiated
    }

    /**
     * calculate color Similarity
     *
     * @param color1
     * @param color2
     * @return similarity: 0~1 (1: same)
     */
    public static double calculateSimilarity(Color color1, Color color2) {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        // Calculate the Euclidean distance
        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));

        // Calculate similarity
        return 1 - distance / Math.sqrt(3 * Math.pow(255, 2));
    }

    /**
     * Calculate the similarity based on the hexadecimal color code
     *
     * @param hexColor1 e.g., 0xFF0000
     * @param hexColor2 e.g., 0xFE0101
     * @return similarity: 0~1 (1: same)
     */
    public static double calculateSimilarity(String hexColor1, String hexColor2) {
        Color color1 = Color.decode(hexColor1);
        Color color2 = Color.decode(hexColor2);
        return calculateSimilarity(color1, color2);
    }


    public static void main(String[] args) {
        Color color1 = Color.decode("0xFF0000");
        Color color2 = Color.decode("0xFE0101");
        double similarity = calculateSimilarity(color1, color2);

        System.out.println("Color similarity: " + similarity);

        double hexSimilarity = calculateSimilarity("0xFF0000", "0xFE0101");
        System.out.println("Hex similarity: " + hexSimilarity);
    }
}

