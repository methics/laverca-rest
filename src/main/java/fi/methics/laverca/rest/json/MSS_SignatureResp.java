//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x500.X500Name;

import com.google.gson.annotations.SerializedName;

import fi.methics.laverca.rest.util.LavercaRDNStyle;
import fi.methics.laverca.rest.util.X509Util;

/**
 * Generic signature and authentication response.
 * This message contains primarily:
 * <ul>
 * <li>A StatusCode indicating response status
 * <li>A Signature
 * <li>A list of AdditionalService responses
 * </ul>
 * 
 * <p>Usage example:
 * <pre>
 * MSS_SignatureResp resp = mssClient.authenticate(...)
 * if (!resp.isSuccessful()) {
 *     // Handle unsuccessful authentication or signature
 *     throw new Exception();
 * } else {
 *     byte[] signature = resp.getRawSignature();
 *     String givenname = resp.getSubjectAttribute("GIVENNAME");
 *     String surname   = resp.getSubjectAttribute("SURNAME");
 * }
 * </pre>
 */
public class MSS_SignatureResp extends MSS_AbstractMessage {

    public static final String STATUS_SIGNATURE       = "500";
    public static final String STATUS_VALID_SIGNATURE = "502";
    
    @SerializedName("MSSP_TransID")
    public String MSSP_TransID;
    
    @SerializedName("MobileUser")
    public MobileUser MobileUser;

    @SerializedName("MSS_Signature")
    public MSS_Signature MSS_Signature;

    @SerializedName("SignatureProfile")
    public String SignatureProfile;
    
    @SerializedName("Status")
    public Status Status;

    @SerializedName("ServiceResponses")
    public List<ServiceResponses> ServiceResponses;
    
    /**
     * Check if the signature was successful.
     * Verifies that the StatusCode in the response is 500 or 502.
     * @return true if signature was successful
     */
    public boolean isSuccessful() {
        if (this.Status == null) return false;
        if (this.Status.StatusCode == null) return false;
        if (this.Status.StatusCode.Value == null) return false;
        return Status.StatusCode.Value.equals(STATUS_SIGNATURE) || Status.StatusCode.Value.equals(STATUS_VALID_SIGNATURE);
    }
    
    /**
     * Get the user's Subject DN from the signature (when using CMS)
     * @return Subject DN
     */
    public String getSubjectDN() {
        X509Certificate cert = this.getCertificate();
        if (cert == null) return null;
        return LavercaRDNStyle.INSTANCE.toString(cert.getSubjectX500Principal());
    }
    
    /**
     * Get MSISDN (phone number) from the response
     * @return MSISDN
     */
    public String getMsisdn() {
        if (this.MobileUser == null) return null;
        return this.MobileUser.MSISDN;
    }
    
    /**
     * Get MSISDN (phone number) from the response
     * @return MSISDN
     */
    public String getUserIdentifier() {
        if (this.MobileUser == null) return null;
        return this.MobileUser.UserIdentifier;
    }
    
    /**
     * Get an attribute from the user's Subject DN
     * <p>Example usage:
     * <pre>
     * String givenname = resp.getSubjectAttribute("GIVENNAME");
     * System.out.println(givenname); // "John"
     * </pre>
     * @param attrName Attribute name
     * @return Subject attribute value or null
     */
    public String getSubjectAttribute(String attrName) {
        X509Certificate cert = this.getCertificate();
        String       subject = this.getSubjectDN();
        if (cert    == null) return null;
        if (subject == null) return null;
        return LavercaRDNStyle.INSTANCE.getAttribute(new X500Name(subject), attrName);
    }
    
    /**
     * Get the user's certificate from the signature
     * @return certificate
     */
    public X509Certificate getCertificate() {
        List<X509Certificate> certs = X509Util.readCerts(this.getCmsSignedData());
        if (certs == null) return null;
        if (certs.isEmpty()) return null;
        return certs.get(0);
    }
    
    /**
     * Get the raw signature from this response
     * @return signature as byte[] or null if response contains none
     */
    public byte[] getRawSignature() {
        if (this.MSS_Signature == null) return null;
        if (this.MSS_Signature.Base64Signature == null) return null;
        return Base64.getDecoder().decode(this.MSS_Signature.Base64Signature);
    }
    
    /**
     * Get CMS SignedData
     * @return SignedData or null if not available
     */
    public SignedData getCmsSignedData() {
        return X509Util.parseCmsSignature(this.getRawSignature());
    }

}
