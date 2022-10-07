//    public static String aes(String input, String key, String iv) {
//        String output = "";
//        if ()
//        return output;
//    }

public static String tdes(String input, boolean tripleKey) {
        StringBuilder keyOne = new StringBuilder(), keyTwo = new StringBuilder();
        if (tripleKey) for (int i = 0; i < 192; i++) {
        if (Math.random() > 0.5) keyOne.append(ALPHABET[(int) (Math.random() * ALPHABET.length)]);
        else keyOne.append(NUMBERS[(int) (Math.random() * NUMBERS.length)]);
        } else for (int i = 0; i < 128; i++) {
        if (Math.random() > 0.5) keyOne.append(ALPHABET[(int) (Math.random() * ALPHABET.length)]);
        else keyOne.append(NUMBERS[(int) (Math.random() * NUMBERS.length)]);
        }
        for (int i = 1; i <= keyOne.length(); i++) if (i % 8 != 0) keyTwo.append(keyOne.charAt(i - 1));
        keyOne = keyTwo;
        System.out.println(keyTwo);
        for (int k = 58, j = 0; k <= 64; k++, j++) for (int i = k; i > 0; i -= 8) keyOne.setCharAt(j, keyTwo.charAt(i));
        System.out.println(keyOne);
        return null;
        }

public static void permute(String key) {
        String permutedKey = "";
        int[] table = {57, 49, 41, 33, 25, 17, 9,
        1, 58, 50, 42, 34, 26, 18, 10, 2,
        59, 51, 43, 35, 27, 19, 11, 3, 60,
        52, 44, 36, 63, 55, 47, 39, 31, 23,
        15, 7, 62, 54, 46, 38, 30, 22, 14,
        6, 61, 53, 45, 37, 29, 21, 13, 5,
        28, 20, 12, 4};
        }