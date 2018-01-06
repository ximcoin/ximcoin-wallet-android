package tech.duchess.luminawallet.model.persistence.account;

public class EncryptedSeedPackage {
    private final byte[] salt;
    private final byte[] initializationVector;
    private final byte[] encryptedSeed;

    public EncryptedSeedPackage(byte[] salt, byte[] initializationVector, byte[] encryptedSeed) {
        this.salt = salt;
        this.initializationVector = initializationVector;
        this.encryptedSeed = encryptedSeed;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public byte[] getEncryptedSeed() {
        return encryptedSeed;
    }
}
