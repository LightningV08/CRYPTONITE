package lightningv08.cryptonite;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class PasswordSafetyCheck {

    public static final int MIN_PASSWORD_LENGTH = 8;

    public static String checkPasswordLength(String password, Context context) {
        if (password.length() < MIN_PASSWORD_LENGTH)
            return context.getString(R.string.your_password_is_too_short);
        else return "";
    }

    public static String checkPasswordLetters(String password, Context context) {
        int i;
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            return context.getString(R.string.your_password_does_not_contain_numeric_characters);
        }
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            return context.getString(R.string.your_password_does_not_contain_lowercase_characters);
        }
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            return context.getString(R.string.your_password_does_not_contain_uppercase_characters);
        }
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('_', '!', '@', '#', '$', '%').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            return context.getString(R.string.your_password_does_not_contain_special_symbols);
        }
        return "";
    }

    public static String checkPasswordInRockyou(String password, Context context) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("rockyou1.txt")));
        } catch (IOException e) {
            throw new RuntimeException("rockyou1.txt not found probably", e);
        }
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException("IOException with rockyou1.txt file", e);
            }
            if (password.equals(line)) {
                return context.getString(R.string.your_password_in_rockyou_passwords);
            }
        }
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("rockyou2.txt")));
        } catch (IOException e) {
            throw new RuntimeException("rockyou2.txt not found probably", e);
        }
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException("IOException with rockyou2.txt file", e);
            }
            if (password.equals(line)) {
                return context.getString(R.string.your_password_in_rockyou_passwords);
            }
        }
        return "";
    }

    public static String checkPassword(String password, Context context) {
        String checkPasswordLengthResult = checkPasswordLength(password, context);
        if (!checkPasswordLengthResult.isEmpty()) return checkPasswordLengthResult;
        String checkPasswordLettersResult = checkPasswordLetters(password, context);
        if (!checkPasswordLettersResult.isEmpty()) return checkPasswordLettersResult;
        String checkPasswordInRockyouResult = checkPasswordInRockyou(password, context);
        if (!checkPasswordInRockyouResult.isEmpty()) return checkPasswordInRockyouResult;
        return "";
    }
}
