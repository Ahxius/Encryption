package com.example.encryptionraw;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public abstract class Encrypt {
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String bacon(String input, char ifTrue, char ifFalse) {
        if (ifTrue == ifFalse) throw new IllegalArgumentException();
        HashMap<Character, String> reference = new HashMap<>();
        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < ALPHABET.length; i++) {
            StringBuilder binary = new StringBuilder(Integer.toBinaryString(i));
            for (int j = 0; j < binary.length(); j++) {
                if (binary.charAt(j) == '0') binary.setCharAt(j, ifFalse);
                else binary.setCharAt(j, ifTrue);
            }
            binary.insert(0, Character.toString(ifFalse).repeat(Math.max(0, 5 - binary.length())));
            reference.put(ALPHABET[i], binary.toString());
        }
        for (char character : input.toCharArray()) output.add(reference.get(Character.toLowerCase(character)) == null ?
                String.valueOf(character) : reference.get(Character.toLowerCase(character)));
        if (output.contains(null)) throw new IllegalArgumentException();
        return String.join(" ", output);
    }

    public static String bacon(String input) {
        HashMap<Character, String> reference = new HashMap<>();
        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < ALPHABET.length; i++) {
            StringBuilder binary = new StringBuilder(Integer.toBinaryString(i));
            binary.insert(0, "0".repeat(Math.max(0, 5 - binary.length())));
            reference.put(ALPHABET[i], binary.toString());
        }
        for (char character : input.toCharArray()) output.add(reference.get(Character.toLowerCase(character)) == null ?
                String.valueOf(character) : reference.get(Character.toLowerCase(character)));
        if (output.contains(null)) throw new IllegalArgumentException();
        return String.join(" ", output);
    }

    public static String caesar(String input) {
        StringBuilder output = new StringBuilder();
        for (char letter : input.toCharArray())
            if ((letter < 91 && letter > 87) || (letter > 119 && letter < 123)) output.append((char) (letter - 23));
            else if ((letter > 63 && letter < 91) || (letter > 96 && letter < 123)) output.append((char) (letter + 3));
            else output.append(letter);
        return output.toString();
    }

    public static String caesar(String input, int shift) {
        StringBuilder output = new StringBuilder();
        for (char letter : input.toCharArray()) {
            if ((letter < 91 && letter > (90 - shift)) || (letter > (122 - shift) && letter < 123))
                output.append((char) (letter - (26 - shift)));
            else if ((letter > 63 && letter < 91) || (letter > 96 && letter < 123)) output.append((char) (letter + shift));
            else output.append(letter);
        }
        return output.toString();
    }

    public static String[] monoalphabetic(String input, String key) {
        if (key.length() != 26) throw new IllegalArgumentException("Key must be exactly 26 characters long.");
        StringBuilder output = new StringBuilder();
        HashMap<Character, Character> keyMap = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            if (ALPHABET[i] == key.toCharArray()[i])
                throw new IllegalArgumentException("Key cannot have any characters in it's original place.");
            keyMap.put(ALPHABET[i], key.toCharArray()[i]);
        }
        for (char letter : input.toCharArray()) output.append(keyMap.get(letter));
        return new String[] {output.toString(), "", ""};
    }

    public static String[] monoalphabetic(String input) {
        HashMap<Character, Character> keyMap = new HashMap<>();
        StringBuilder output = new StringBuilder(), keyOutput = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            char newCharacter;
            do newCharacter = (char) (97 + (int) (Math.random() * 26));
            while (keyMap.containsValue(newCharacter));
            keyMap.put(ALPHABET[i], newCharacter);
        }
        for (char letter : input.toCharArray()) output.append(keyMap.get(letter));
        for (char letter : keyMap.values()) keyOutput.append(letter);
        return new String[] {output.toString(), keyOutput.toString()};
    }

    // the following methods were created by the help of https://baeldung.com
    public static String[] aes(String input) {
        try {
            SecretKey key = generateKey();
            byte[] initializerIV = new byte[16];
            new SecureRandom().nextBytes(initializerIV);
            IvParameterSpec iv = new IvParameterSpec(initializerIV);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(input.getBytes());
            return new String[]{Base64.getEncoder().encodeToString(cipherText),
                    Base64.getEncoder().encodeToString(key.getEncoded()),
                    Base64.getEncoder().encodeToString(iv.getIV())};
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static String[] aes(String input, String password, String salt) {
        try {
            SecretKey key = generateKey(password, salt);
            byte[] initializerIV = new byte[16];
            new SecureRandom().nextBytes(initializerIV);
            IvParameterSpec iv = new IvParameterSpec(initializerIV);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(input.getBytes());
            return new String[] {Base64.getEncoder().encodeToString(cipherText), "", ""};
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    private static SecretKey generateKey(String password, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

}