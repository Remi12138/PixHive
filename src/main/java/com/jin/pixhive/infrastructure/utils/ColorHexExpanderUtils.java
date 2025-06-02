package com.jin.pixhive.infrastructure.utils;

/**
 * Fetch color theme via CI API may return 5-digit Hex RGB, expand to 6-digit standard RGB
 */
public class ColorHexExpanderUtils {
    private ColorHexExpanderUtils() {
        // Utility classes do not need to be instantiated
    }

    public static String expandHexColor(String compressed) {
        // remove prefix: 0x
        String input = compressed.startsWith("0x") ? compressed.substring(2) : compressed;
        int length = input.length();
        // len = 3, return
        if (length == 3) {
            return "0x000000";
        }
        int index = 0;
        StringBuilder expanded = new StringBuilder();

        // process R G B
        for (int i = 0; i < 3; i++) {
            char current = input.charAt(index);
            if (current == '0') {
                expanded.append("00");
                index++;
            } else {
                // not the last
                if (index + 1 < length) {
                    expanded.append(current).append(input.charAt(index + 1));
                    index += 2;
                } else {
                    // if last character, add 0
                    expanded.append(current).append('0');
                    index += 2;
                }
            }
        }

        return "0x" + expanded.toString();
    }

    public static void main(String[] args) {
        System.out.println(expandHexColor("000"));     // 0x000000
        System.out.println(expandHexColor("0a00"));    // 0x00a000
        System.out.println(expandHexColor("a0b40"));   // 0xa0b400
        System.out.println(expandHexColor("0ab0"));    // 0x00ab00
        System.out.println(expandHexColor("00ab"));   // 0x0000ab
        System.out.println(expandHexColor("0ab00"));  // 0x00ab00
    }
}

