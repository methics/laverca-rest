//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;

public class X509Util {

    
    /**
     * Convert a byte array to a CMS SignedData object
     * @param bytes byte array
     * @return CMS SignedData object or null if parsing fails
     */
    public static SignedData parseCmsSignature(byte[] bytes) {

        if (bytes == null) {
            return null;
        }

        ASN1Object asn1 = null;
        try (ASN1InputStream is = new ASN1InputStream(bytes)) {
            asn1 = is.readObject();
        } catch (IOException ioe) {
            return null;
        }

        ContentInfo ci = ContentInfo.getInstance(asn1);

        ASN1ObjectIdentifier typeId = ci.getContentType();
        if(!typeId.equals(PKCSObjectIdentifiers.signedData)) {
            return null;
        }
        
        return SignedData.getInstance(ci.getContent());
    }
    
    /**
     * Convert a DER certificate to X509Certificate
     * @param der Certificate to convert
     * @return Converted certificate as X509Certificate. Returns null if the conversion failed or input is null. 
     */
    public static X509Certificate DERtoX509Certificate(final byte[] der) {
        if (der == null) {
            return null;
        }
        
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(der);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate)cf.generateCertificate(bis);
        } catch (CertificateException e) {
            return null;
        }
    }
    
    /**
     * Read all certificates from a SignedData
     * @param sd data
     * @return all X509 certificates or an empty list
     */
    public static List<X509Certificate> readCerts(final SignedData sd) {
        if (sd == null) {
            return Collections.emptyList();
        }

        List<X509Certificate> certs = new ArrayList<X509Certificate>();

        ASN1Set certSet = sd.getCertificates();
        Enumeration<?> en = certSet.getObjects();
        while(en.hasMoreElements()) {
            Object o = en.nextElement();
            try {
                byte[] certDer = ((ASN1Sequence)o).getEncoded();
                X509Certificate cert = X509Util.DERtoX509Certificate(certDer);
                if (cert != null) {
                    certs.add(cert);                    
                }
            } catch (IOException e) {
                continue;
            }
        }

        return certs;
    }
    
}
