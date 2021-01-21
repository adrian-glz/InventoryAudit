/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.sounds.inventoryaudit.util;

import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author jmendoza
 */
public class Base64Encoding {
    
    public static String fEncode(String cCad) {
        byte[] encoded = Base64.encodeBase64(cCad.getBytes());     
        return new String(encoded);
    }

    public static String fEncode(byte[] cCad) {
        byte[] encoded = Base64.encodeBase64(cCad);     
        return new String(encoded);
    }
    
    public static String fDecode(String cCad) {
        byte[] decoded = Base64.decodeBase64(cCad.getBytes());      
        return new String(decoded);
    }

    public static String fDecode(byte[] cCad) {
        byte[] decoded = Base64.decodeBase64(cCad);      
        return new String(decoded);
    }

    public static byte[] fDecode(String cCad, int i) {
        byte[] decoded = Base64.decodeBase64(cCad.getBytes());      
        return decoded;
    }
    
}
