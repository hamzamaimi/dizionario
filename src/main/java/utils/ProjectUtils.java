package utils;

public class ProjectUtils {
    public static String PROJECT_NAME = "DIZIONARIO JAVA";

    public static String generateRandomString (int l) {
        String alphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";
        StringBuilder s = new StringBuilder(l);
        int i;
        for ( i=0; i<l; i++) {
            int ch = (int)(alphaNumericStr.length() * Math.random());
            s.append(alphaNumericStr.charAt(ch));
        }
        return s.toString();
    }
}
