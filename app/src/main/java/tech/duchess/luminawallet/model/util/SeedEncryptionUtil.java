package tech.duchess.luminawallet.model.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import tech.duchess.luminawallet.model.persistence.account.EncryptedSeedPackage;

/**
 * Helper class to encrypt/decrypt seeds.
 */
public class SeedEncryptionUtil {
    private static final String SECRET_KEY_ALGO = "PBKDF2withHmacSHA1";
    private static final String SECRET_KEY_ALGO_ASSOCIATION = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String CHAR_SET = "UTF-8";
    private static final int ITERATION_COUNT = 1000; // Iteration count as recommended by PKCS#5
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = KEY_LENGTH / 8; // Same size as key output
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Checks the password length.
     *
     * @param password The password to check.
     * @return {@code True} if the password is valid length.
     */
    public static boolean checkPasswordLength(@Nullable String password) {
        return password != null
                && password.length() >= MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH;
    }

    /**
     * Encrypts a seed given a password.
     *
     * @param seed     The seed in plaintext.
     * @param password The password used to encrypt/decrypt this seed.
     * @return The object to store in the database, alongside the account identifier.
     */
    public static EncryptedSeedPackage encryptSeed(@NonNull String seed,
                                                   @NonNull String password)
            throws Exception {
        // Don't seed this. Rely on system entropy.
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        SecretKey key = deriveKeyPbkdf2(salt, password);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = new byte[cipher.getBlockSize()];
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
        byte[] encryptedSeed = cipher.doFinal(seed.getBytes(CHAR_SET));

        return new EncryptedSeedPackage(salt, iv, encryptedSeed);
    }

    /**
     * Decrypts a seed into plaintext. Be careful with this please.
     *
     * @param encryptedSeedPackage A representation of the encrypted seed, initialization vector,
     *                             and salt.
     * @param password             The plaintext password used to decrypt the seed.
     * @return The seed in plaintext.
     */
    public static String decryptSeed(@NonNull EncryptedSeedPackage encryptedSeedPackage,
                              @NonNull String password)
            throws Exception {
        SecretKey secretKey = deriveKeyPbkdf2(encryptedSeedPackage.getSalt(), password);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParams = new IvParameterSpec(encryptedSeedPackage.getInitializationVector());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        byte[] plaintext = cipher.doFinal(encryptedSeedPackage.getEncryptedSeed());
        return new String(plaintext, CHAR_SET);
    }

    private static SecretKey deriveKeyPbkdf2(byte[] salt,
                                             @NonNull String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGO);
        // Don't use the output of generateSecret() directly, as that's a PBEKey instance which does
        // not contain an initialized IV. The Cipher object expects that from a PBEKey.
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, SECRET_KEY_ALGO_ASSOCIATION);
    }
}
