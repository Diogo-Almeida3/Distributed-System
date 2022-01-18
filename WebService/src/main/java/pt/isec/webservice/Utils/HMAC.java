package pt.isec.webservice.Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {
    static public byte[] calcHmacSha256(String chave, String msg){
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec sKeySpec = new SecretKeySpec(chave.getBytes(),
                    "HmacSHA256");
            mac.init(sKeySpec);
            hmacSha256 = mac.doFinal(msg.getBytes());
        } catch (Exception ex) {
            throw new RuntimeException("hmac-sha256 calculation failed", ex);
        }
        return hmacSha256;
    }
}
