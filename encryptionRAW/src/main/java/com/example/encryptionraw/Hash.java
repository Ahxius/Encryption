package com.example.encryptionraw;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.*;

public class Hash {
    public static String[] md4(String input) {
        JSONObject output = call("md4", input);
        return new String[] {(String) output.get("Digest")};
    }

    public static String[] md5(String input) {
        JSONObject output = call("md5", input);
        return new String[] {(String) output.get("Digest")};
    }

    public static String[] sha1(String input) {
        JSONObject output = call("sha1", input);
        return new String[] {(String) output.get("Digest")};
    }

    public static String[] sha256(String input) {
        JSONObject output = call("sha256", input);
        return new String[] {(String) output.get("Digest")};
    }

    public static String[] highway64(String input) {
        JSONObject output = call("highway64", input, "random");
        return new String[] {(String) output.get("Digest"), (String) output.get("Key")};
    }

    public static String[] highway64(String input, String key) {
        JSONObject output = call("highway64", input, key);
        return new String[] {(String) output.get("Digest"), (String) output.get("Key")};
    }

    public static String[] highway128(String input) {
        JSONObject output = call("highway128", input, "random");
        return new String[] {(String) output.get("Digest"), (String) output.get("Key")};
    }

    public static String[] highway128(String input, String key) {
        JSONObject output = call("highway128", input, key);
        return new String[] {(String) output.get("Digest"), (String) output.get("Key")};
    }

    public static String[] highway256(String input) {
        JSONObject output = call("highway", input, "random");
        return new String[] {(String) output.get("Digest"), (String) output.get("Key")};
    }

    public static String[] highway256(String input, String key) {
        JSONObject output = call("highway", input, key);
        return new String[] {(String) output.get("Digest"), (String) output.get("Key")};
    }

    private static JSONObject call(String algorithm, String input, String key) {
        JSONObject output = null;
        try {
            URL url = new URL(String.format("https://api.hashify.net/hash/%s?value=%s&key=%s",
                    algorithm, input, key));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader raw = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new JSONObject(new JSONTokener(connection.getInputStream()));
            raw.close();
        } catch (Exception e) { e.printStackTrace(); }
        if (output == null) throw new NullPointerException();
        return output;
    }

    private static JSONObject call(String algorithm, String input) {
        JSONObject output = null;
        try {
            URL url = new URL(String.format("https://api.hashify.net/hash/%s/hex?value=%s",
                    algorithm, input));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader raw = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new JSONObject(new JSONTokener(connection.getInputStream()));
            raw.close();
        } catch (Exception e) { e.printStackTrace(); }
        if (output == null) throw new NullPointerException();
        return output;
    }
}
