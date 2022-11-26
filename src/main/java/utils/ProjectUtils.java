package utils;

import models.User;

import java.security.SecureRandom;
import java.util.Base64;

public class ProjectUtils {
    public static String PROJECT_NAME = "DIZIONARIO JAVA";
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generateRandomString (int l, String initialChars) {
        String alphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";
        StringBuilder s = new StringBuilder(l);
        int i;
        for ( i=0; i<l; i++) {
            int ch = (int)(alphaNumericStr.length() * Math.random());
            s.append(alphaNumericStr.charAt(ch));
        }
        return (initialChars + s).substring(0, l);
    }

    public static String createToken(String initialChars) {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String encodeToString = base64Encoder.encodeToString(randomBytes);
        return (initialChars + encodeToString).substring(0, 32);
    }
}
