package com.PMSystems;

import java.io.*;
import java.util.*;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import com.PMSystems.logger.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class JKSManager {

    private static final String ALOGRITHM = "DESede";
//    private static final String ALOGRITHM = "DESede/ECB/PKCS5Padding";
    private static final int KEY_STRENGTH = 168;

    private static char[] JKS_PASSWORD = "test".toCharArray();
    private static String JKS_FILE = "bridgemailsystem.keystore";

    private static KeyStore keystore;

    static {
        try {
            JKS_PASSWORD = PMSResources.getInstance().getJKSPassword().toCharArray();
            JKS_FILE = PMSDefinitions.RESOURCE_DIR+"bridgemailsystem.keystore";

            keystore = getKeyStore(JKS_FILE);
            if(keystore==null) {
                keystore = initKeyStore(JKS_FILE);
            }

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        System.out.println("===== JKSManager Initialized =====");
    }

    private JKSManager() {
    }

    /**
     *
     * @param userId
     * @param password
     * @return
     */
    public static byte[] encrypt(String userId, String password) {

        String enc="";
        userId = userId.toLowerCase();
        try {
            SecretKey key = null;
            synchronized(keystore) {
                key = getSecretKey(userId);
            }
            System.out.println("password in public encryption = "+password);
            byte[] encArray = encrypt(key, password);
            enc = new String(encArray);
            System.out.println("[Encrypted]: "+enc);
            return encArray;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return new byte[0];
    }

    
    /**
    *
    * @param userId
    * @param password
    * @return
    */
   public static byte[] encryptReadOnly(String userId, String password) {

       String enc="";
       userId = userId.toLowerCase();
       try {
           SecretKey key = null;
           synchronized(keystore) {
               key = getSecretKeyFromFile(userId);
           }
           System.out.println("password in public encryption = "+password);
           byte[] encArray = encrypt(key, password);
           enc = new String(encArray);
           System.out.println("[Encrypted]: "+enc);
           return encArray;

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return new byte[0];
   }
    
    /**
     *
     * @param userId
     * @param password
     * @return
     */
    public static String decrypt(String userId, byte[] password) {

        String dec="";
        userId = userId.toLowerCase();
        try {
            SecretKey key = null;
            synchronized(keystore) {
                key = getSecretKey(userId);
            }
            dec = decrypt(key, password);
//            System.out.println("[Decrypted]: "+dec);
            return dec;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return dec;
    }

    /**
     * @param userId
     * @return
     */
    private synchronized static SecretKey getSecretKeyFromFile(String userId) {

        try {
            KeyStore myKeyStore = getKeyStore(JKS_FILE);
            SecretKey key = (SecretKey)myKeyStore.getKey(userId, JKS_PASSWORD);
            return key;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        String str = "[JKSManager-secretkeyfromfile()]: SecretKey Not Found, UserId:"+userId;
        System.out.println(str);
        WebServerLogger.getLogger().log(new LogEntry("","",str));
        return null;
    }

    /**
     * @param userId
     * @param password
     * @return
     */
    public static String decryptReadOnly(String userId, byte[] password) {

        String dec="";
        userId = userId.toLowerCase();
        try {
            SecretKey key = null;
            key = getSecretKeyFromFile(userId);
            if(key==null)
                return "";

            dec = decrypt(key, password);
            return dec;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return dec;
    }

    /**
     *
     * @param userId
     * @param originalPass
     * @param encryptedPass
     * @return
     */
    public static boolean verifyEncryption(String userId, String originalPass, byte[] encryptedPass) {

        String dec="";
        userId = userId.toLowerCase();
        try {
            SecretKey key = null;
            synchronized(keystore) {
                key = getSecretKey(userId);
            }
            dec = decrypt(key, encryptedPass);
            return originalPass.equals(dec);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    
    /**
    *
    * @param userId
    * @param originalPass
    * @param encryptedPass
    * @return
    */
   public static boolean verifyReadOnlyEncryption(String userId, String originalPass, byte[] encryptedPass) {

       String dec="";
       userId = userId.toLowerCase();
       try {
           dec = decryptReadOnly(userId, encryptedPass);
           return originalPass.equals(dec);

       } catch (Exception ex) {
           WebServerLogger.getLogger().log(ex);
           ex.printStackTrace();
       }
       return false;
   }

    /**
     * @param userID
     * @return
     */
    public static boolean regenerateKeyForUser(String userID) {

        boolean generated = false;
        userID = userID.toLowerCase();
        try {
            SecretKey key = generateKey();
            System.out.println("[JKSManager-regenerateKeyForUser(]: UserId: "+userID+", SecretKey: " + new String(key.getEncoded()));
            keystore.setKeyEntry(userID, key, JKS_PASSWORD, null);
            generated = saveKeyStore(keystore);

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return generated;
    }

    /**
     * @param userId
     * @return
     */
    private static SecretKey getSecretKey(String userId) {
        SecretKey key = null;
        try {
            //--- Is Alias exists ---
            java.util.Enumeration enumer = keystore.aliases();
            boolean found = false;
            while (enumer.hasMoreElements()) {
                String element = (String)enumer.nextElement();
//                System.out.println("[Alias]: "+element);
                if(element.equals(""+userId)) {
                    System.out.println("UserId: "+userId+" exists in keystore.");
                    found = true;
                }
            }

            //--Get 3Des Seceret Key--
            if(found) {
                key = (SecretKey)keystore.getKey(userId, JKS_PASSWORD);
                System.out.println("[Found-Key]: "+new String(key.getEncoded()));
            } else {
                key = generateKey();
                System.out.println("[New-Key]: "+new String(key.getEncoded()));
                keystore.setKeyEntry(userId, key, JKS_PASSWORD, null);
                saveKeyStore(keystore);
            }
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return key;
    }

    /**
     *
     * @param keyfile
     * @return
     */
    private static KeyStore getKeyStore(String keyfile) {

        System.out.println("== Get Keystore: "+keyfile+" ===");
        try {
            InputStream keyStoreInput = new FileInputStream(keyfile);
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(keyStoreInput, JKS_PASSWORD);
            keyStoreInput.close();
            return keyStore;

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param keyStore
     * @return
     */
    private static boolean saveKeyStore(KeyStore keyStore) {
        try {
            //--- write to file---
            OutputStream keyStoreOutput = new FileOutputStream(JKS_FILE);
            keyStore.store(keyStoreOutput, JKS_PASSWORD);
            keyStoreOutput.close();

            return true;
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param keystoreFileName
     * @return
     */
    private static KeyStore initKeyStore(String keystoreFileName) {
        System.out.println("== Initalizing Keystore: "+keystoreFileName+" ===");
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(null, JKS_PASSWORD);
            keyStore.store(new FileOutputStream(keystoreFileName), JKS_PASSWORD);

            return keyStore;
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Get a key generator for Triple DES (a.k.a DESede)
        KeyGenerator keygen = KeyGenerator.getInstance(ALOGRITHM);
        keygen.init(KEY_STRENGTH);
        return keygen.generateKey();
    }

    /**
     *
     * @return
     */
    private static Cipher getCipher() {
        Cipher c = null;
        try {
//            c = Cipher.getInstance("DESede/ECB/PKCS5Padding","SunJCE");
            c = Cipher.getInstance(ALOGRITHM);
        }
        catch (Exception e) {
            WebServerLogger.getLogger().log(e);
            e.printStackTrace();
            // An exception here probably means the JCE provider hasn't
            // been permanently installed on this system by listing it
            // in the $JAVA_HOME/jre/lib/security/java.security file.
            // Therefore, we have to install the JCE provider explicitly.
            System.err.println("Installing SunJCE provider.");
            Provider sunjce = new com.sun.crypto.provider.SunJCE();
            Security.addProvider(sunjce);
        }
        return c;
    }
    /**
     * Use the specified TripleDES key to decrypt bytes ready from the input
     * stream and write them to the output stream. This method uses uses Cipher
     * directly to show how it can be done without CipherInputStream and
     * CipherOutputStream.
     */
    private static String decrypt(SecretKey key, byte[] text) throws
        NoSuchAlgorithmException, InvalidKeyException, IOException,
        IllegalBlockSizeException, NoSuchPaddingException,
        BadPaddingException {
        // Create and initialize the decryption engine
        String decrypted = "";
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decByte = cipher.doFinal(text);
//            decrypted = decByte.toString();
            decrypted = new String(decByte);
//            decrypted = new String(decByte,"UTF-8");

        } catch (Exception e) {
            WebServerLogger.getLogger().log(e);
            e.printStackTrace();
        }
        return decrypted;
    }

    /**
     *
     * @param key
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws IOException
     */
    private static byte[] encrypt(SecretKey key, String text) throws
        NoSuchAlgorithmException, InvalidKeyException,
        NoSuchPaddingException, IOException {

        try {
            // Create and initialize the encryption engine

            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, key);
//            byte[] pass = hex2byte(text);
            System.out.println("password going to be encrypt = "+text);
            byte[] encpt = cipher.doFinal(text.getBytes());
//          byte[] plaintext = encryptStr.getBytes("UTF-8");
//          byte[] plaintext = encryptStr.getBytes("ASCII");
//          byte[] plaintext = encryptStr.getBytes("ISO-8859-1");
//            byte[] passBytes =
//            byte[] encpt = cipher.doFinal(text.getBytes("UTF-8"));
            return encpt;

        } catch (Exception e) {
            WebServerLogger.getLogger().log(e);
            e.printStackTrace();
        }
        return new byte[0];
    }
//
// converting bytes to hex string
//  public static String byte2hex(byte[] b) {
//    String hs="";
//    String stmp="";
//    for (int n=0;n<b.length;n++){
//      stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
//      if (stmp.length()==1)
//        hs=hs+"0"+stmp;
//      else hs=hs+stmp;
//    }
//  return hs.toUpperCase();
//  }
//  // converting to hexstring to bytes
//  public static byte[] hex2byte(String strhex){
//
//    if(strhex==null) return null;
//      int l = strhex.length();
//
//      if(l %2 ==1) return null;
//        byte[] b = new byte[l/2];
//        for(int i = 0 ; i < l/2 ;i++){
//          b[i] = (byte)Integer.parseInt(strhex.substring(i *2,i*2 +2),16);
//        }
//  return b;
//  }
//


    /**
     *
     * @param bt
     * @param file
     */
    private static void writeToFile(byte bt[], File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            out.write(bt);
            out.close();
        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param file
     * @return
     */
    private static byte[] readFromFile(String file) {

        byte[] bt = new byte[0];
        try {
            InputStream is = new FileInputStream(file);
            byte[] ary = new byte[8];
            int ct = is.read(ary);
            while (ct != -1) {
                bt = arraycopy(ary, bt);
                ary = new byte[8];
                ct = is.read(ary);
            }
            is.close();

        } catch (Exception ex) {
            WebServerLogger.getLogger().log(ex);
            ex.printStackTrace();
        }
        return bt;
    }

    private static byte[] arraycopy(byte[] src, byte[] dest) {

        byte[] tmp = new byte[dest.length+src.length];
        int i=0;
        for(; i<dest.length; i++)
            tmp[i] = dest[i];

        for(int j=0; j<src.length; j++,i++)
            tmp[i] = src[j];

        return tmp;
    }

}