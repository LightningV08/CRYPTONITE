package lightningv08.cryptonite.passwordcheck;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lightningv08.cryptonite.R;

public class PasswordSafetyCheck {

    public static final int MIN_PASSWORD_LENGTH = 8;

    public static String checkPasswordLength(String password, Context context) {
        if (password.length() < MIN_PASSWORD_LENGTH)
            return context.getString(R.string.your_password_is_too_short) + '\n';
        else return "";
    }

    public static String checkPasswordLetters(String password, Context context) {
        int i;
        String res = "";
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            res += context.getString(R.string.your_password_does_not_contain_numeric_characters) + '\n';
        }
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            res += context.getString(R.string.your_password_does_not_contain_lowercase_characters) + '\n';
        }
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            res += context.getString(R.string.your_password_does_not_contain_uppercase_characters) + '\n';
        }
        for (i = 0; i < password.length(); i++) {
            if (Arrays.asList('_', '!', '@', '#', '$', '%').contains(password.charAt(i))) {
                break;
            }
        }
        if (i == password.length()) {
            res += context.getString(R.string.your_password_does_not_contain_special_symbols) + '\n';
        }
        return res;
    }

    public static String checkPasswordInRockyou(String password, Context context) {
        BufferedReader reader;
        String filename;
        for (int i = 1; i <= 10; i++) {
            filename = i + ".txt";
            try {
                reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            } catch (IOException e) {
                throw new RuntimeException(filename + " not found probably");
            }
            List<String> lines = reader.lines().collect(Collectors.toList());
            int l = 0, r = lines.size();
            while (r - l > 1) {
                int mid = (l + r) / 2;
                int compare = password.compareTo(lines.get(mid));
                if (compare < 0) {
                    r = mid;
                } else if (compare > 0) {
                    l = mid;
                } else {
                    return context.getString(R.string.your_password_in_rockyou_passwords);
                }
            }
        }
        return "";
    }

    public static String checkPassword(String password, Context context) {
        String res = "";
        res += checkPasswordLength(password, context);
        res += checkPasswordLetters(password, context);
        res += checkPasswordInRockyou(password, context);
        return res;
    }
}
