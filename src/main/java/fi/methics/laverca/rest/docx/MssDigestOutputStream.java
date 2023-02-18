package fi.methics.laverca.rest.docx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;

import fi.methics.laverca.rest.MssClient;
import fi.methics.laverca.rest.util.DTBS;

public class MssDigestOutputStream extends OutputStream {
    
    private MessageDigest md;
    
    private MssClient client;
    private String    msisdn;
    private String    sigprof;
    private String    message;
    
    public MssDigestOutputStream(MssClient client, String msisdn, String message, String sigprof) {
        this.client  = client;
        this.msisdn  = msisdn;
        this.sigprof = sigprof;
        this.message = message;
    }
    
    public byte[] sign() throws IOException, GeneralSecurityException {
        System.out.println("Sign called!!");
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        bos.write(md.digest());
        byte[] digest = bos.toByteArray();
        System.out.println("Signing digest " + Base64.getEncoder().encodeToString(digest));
        
        return client.sign(this.msisdn, message, digest, DTBS.MIME_SHA256, this.sigprof);
    }

    public void init() throws GeneralSecurityException {
        md = CryptoFunctions.getMessageDigest(HashAlgorithm.sha256);
    }
    
    @Override
    public void write(final int b) throws IOException {
        md.update((byte)b);
    }

    @Override
    public void write(final byte[] data, final int off, final int len) throws IOException {
        md.update(data, off, len);
    }

}
