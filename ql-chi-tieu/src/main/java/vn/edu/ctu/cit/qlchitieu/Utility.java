package vn.edu.ctu.cit.qlchitieu;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Utility {

    public static String MD5Encrypt(String str){
        try {
            byte[] byeData = str.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] afterHash = md.digest(byeData);
            String strHash = new BigInteger(1, afterHash).toString(16).toUpperCase(Locale.US);
            int num = 32 - strHash.length();
            for(int i = 0; i < num; i++)
            {
                strHash = "0"+strHash;
            }
            return strHash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
