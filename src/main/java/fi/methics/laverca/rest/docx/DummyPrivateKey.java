package fi.methics.laverca.rest.docx;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

/**
 * Dummy RSA PrivateKey object used to satisfy Apache POI
 */
public class DummyPrivateKey implements RSAPrivateKey {

    private static final long serialVersionUID = 1L;

    @Override
    public String getAlgorithm() {
        return "RSA";
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return null;
    }

    @Override
    public BigInteger getModulus() {
        return null;
    }

    @Override
    public BigInteger getPrivateExponent() {
        return null;
    }

}
