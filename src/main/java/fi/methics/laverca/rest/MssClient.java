//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import fi.methics.laverca.rest.json.JsonRequest;
import fi.methics.laverca.rest.json.JsonResponse;
import fi.methics.laverca.rest.json.MSS_ProfileReq;
import fi.methics.laverca.rest.json.MSS_SignatureReq;
import fi.methics.laverca.rest.json.MSS_SignatureResp;
import fi.methics.laverca.rest.json.Status.MobileUserCertificate;
import fi.methics.laverca.rest.util.DTBS;
import fi.methics.laverca.rest.util.MSS_SignatureReqBuilder;
import fi.methics.laverca.rest.util.RestClient;
import fi.methics.laverca.rest.util.RestClient.AuthnMode;
import fi.methics.laverca.rest.util.RestException;

/**
 * REST MSS Client class.
 * Example usage:
 * 
 * <pre>
 * MssClient client = MssClient.initWithPassword("TestAP", "hunter1", "http://localhost:9061/rest/service");
 * try {
 *     MSS_SignatureResp resp = client.authenticate("35847001001", "Authentication test", "http://alauda.mobi/digitalSignature");
 *     if (resp.isSuccess()) {
 *         System.out.println("Successfully authenticated " + resp.getSubjectDN()); 
 *     }
 * } catch (RestException e) {
 *     System.out.println("Failed to authenticate user", e);
 * }
 * </pre>
 */
public class MssClient {

    public static final String FORMAT_PKCS7       = "http://uri.etsi.org/TS102204/v1.1.2#PKCS7";
    public static final String FORMAT_CMS         = "http://uri.etsi.org/TS102204/v1.1.2#CMS-Signature";
    public static final String FORMAT_KIURU_PKCS1 = "http://www.methics.fi/KiuruMSSP/v3.2.0#PKCS1";
    public static final String FORMAT_FICOM_PKCS1 = "http://mss.ficom.fi/TS102204/v1.0.0#PKCS1";

    private static final String DEFAULT_APPWD = "x";
    
    private RestClient client;
    private String apid;
    private String appwd = DEFAULT_APPWD;
    
    private MssClient() { }

    /**
     * Initialize a new MSS client with an API_KEY authentication
     * 
     * @param apid    AP_ID of the AP
     * @param apikey  API_KEY of the AP
     * @param resturl REST service URL (e.g. http://localhost:9061/rest/service)
     * @return
     */
    public static MssClient initWithApiKey(final String apid, 
                                           final String apikey,
                                           final String resturl) {
        MssClient c = new MssClient();
        c.client = new RestClient();
        c.client.setApId(apid);
        c.client.setApiKey(apikey);
        c.client.setAuthnMode(AuthnMode.APIKEY);
        c.client.setRestUrl(resturl);
        return c;
    }

    /**
     * Initialize a new MSS client with username/password authentication
     * 
     * @param apname       AP Name
     * @param restpassword AP REST password
     * @param resturl      REST service URL (e.g. http://localhost:9061/rest/service)
     * @return
     */
    public static MssClient initWithPassword(final String apname, 
                                             final String restpassword,
                                             final String resturl)
    {
        MssClient c = new MssClient();
        c.client = new RestClient();
        c.client.setApName(apname);
        c.client.setPassword(restpassword);
        c.client.setAuthnMode(AuthnMode.PASSWORD);
        c.client.setRestUrl(resturl);
        return c;
    }
    
    /**
     * Authenticate a user with MSSP
     * @param message          Message shown to the user (e.g. "Please authenticate to Bank A Portal")
     * @param msisdn           Phone number of the user (in international format)
     * @param signatureprofile Signatureprofile of the wanted authentication key
     * @return Authentication response
     * @throws RestException if signature fails
     */
    public MSS_SignatureResp authenticate(final String msisdn, 
                                          final String message, 
                                          final String signatureprofile)
        throws RestException
    {
        try {
            JsonRequest jReq = new JsonRequest();
            final String dtbd = message;
            final DTBS   dtbs = new DTBS(dtbd);

            jReq.MSS_SignatureReq = new MSS_SignatureReq(msisdn, dtbs, null);
            jReq.MSS_SignatureReq.SignatureProfile = signatureprofile;
            jReq.MSS_SignatureReq.MSS_Format       = FORMAT_CMS;
            jReq.MSS_SignatureReq.AP_Info.AP_ID    = this.apid;
            jReq.MSS_SignatureReq.AP_Info.AP_PWD   = this.appwd;
            jReq.MSS_SignatureReq.AP_Info.AP_TransID = "A" + UUID.randomUUID().toString();
            
            JsonResponse jResp = this.client.sendReq(jReq);
            return jResp.MSS_SignatureResp;
        } catch (RestException e) {
            throw e;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
    
    /**
     * Get user Certificates with ProfileQuery
     * 
     * <p>Example usage:
     * <pre>
     * List&lt;X509Certificate&gt; certs = client.getCertificateChain("35847001001", "http://alauda.mobi/nonRepudiation");
     * X590Certificate    userCert = certs.get(0);
     * </pre>
     * 
     * @param msisdn            Phone number of the user (in international format)
     * @param signatureprofile  Signatureprofile of the wanted certificate chain
     * @return Certificate chain (first certificate is end-entity certificate)
     * @throws RestException
     */
    public List<X509Certificate> getCertificates(final String msisdn, 
                                                 final String signatureprofile) 
        throws RestException
    {
        try {
            JsonRequest jReq = new JsonRequest();
            jReq.MSS_ProfileReq = new MSS_ProfileReq(msisdn.toString());
            jReq.MSS_ProfileReq.AP_Info.AP_ID    = this.apid;
            jReq.MSS_ProfileReq.AP_Info.AP_PWD   = this.appwd;
            
            JsonResponse resp = this.client.sendReq(jReq);
            
            List<MobileUserCertificate> jsonChain = resp.MSS_ProfileResp.Status.StatusDetail.ProfileQueryExtension.MobileUserCertificate;
            List<X509Certificate>     resultChain = new ArrayList<>();

            int i = 0;
            for (MobileUserCertificate chain : jsonChain) {
                if (chain == null) continue;
                if (chain.SignatureProfiles == null) continue;
                if (chain.X509Certificate   == null) continue;
                if (chain.State != null && !chain.State.equals("ACTIVE")) continue; // ignore inactive certs
                
                System.out.println("Parsing chain " + ++i + " of size " + chain.X509Certificate.size());
                if (chain.SignatureProfiles.contains(signatureprofile)) {
                    List<X509Certificate> certs = new ArrayList<>();
                    for (String cert : chain.X509Certificate) {
                        CertificateFactory certFactory = null;
                        try {
                            certFactory = CertificateFactory.getInstance("X.509");
                            InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(cert));
                            X509Certificate x509cert = (X509Certificate) certFactory.generateCertificate(in);
                            certs.add(x509cert);
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    resultChain.addAll(certs);
                }
            }
            return resultChain;
        } catch (RestException e) {
            throw e;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
    
    /**
     * Sign datas. This is typically used for document signing, etc
     * 
     * <p>Recommended MimeType values:
     * <ul>
     * <li>{@link DTBS#MIME_SHA1}   for pre-computed SHA-1 hashes
     * <li>{@link DTBS#MIME_SHA256} for pre-computed SHA-256 hashes
     * <li>{@link DTBS#MIME_SHA384} for pre-computed SHA-384 hashes
     * <li>{@link DTBS#MIME_TEXTPLAIN} for plain text
     * <li>{@link DTBS#MIME_STREAM} for generic binary data
     * </ul>
     * 
     * @param msisdn   Phone number of the user (in international format)
     * @param message  Message shown to the user (e.g. "Please sign contract.pdf")
     * @param digest   Document digest
     * @param mimetype Mime-Type of the digest (e.g. "application/x-sha256") s
     * @param signatureprofile Signatureprofile of the wanted authentication key
     * @return raw CMS signature
     * @throws RestException if signature fails
     */
    public byte[] sign(final String msisdn,
                       final String message,
                       final byte[] digest, 
                       final String mimetype,
                       final String signatureprofile)
        throws RestException
    {
        try {
            JsonRequest jReq = new JsonRequest();
            final String dtbd = message;
            final DTBS   dtbs = new DTBS(digest, DTBS.ENCODING_BASE64, DTBS.MIME_SHA256);

            jReq.MSS_SignatureReq = new MSS_SignatureReq(msisdn, dtbs, dtbd);
            jReq.MSS_SignatureReq.SignatureProfile = signatureprofile;
            jReq.MSS_SignatureReq.MSS_Format       = FORMAT_CMS;
            jReq.MSS_SignatureReq.AP_Info.AP_ID    = this.apid;
            jReq.MSS_SignatureReq.AP_Info.AP_PWD   = this.appwd;
            jReq.MSS_SignatureReq.AP_Info.AP_TransID = "A" + UUID.randomUUID().toString();
            
            JsonResponse jResp = this.client.sendReq(jReq);
            return Base64.getDecoder().decode(jResp.MSS_SignatureResp.MSS_Signature.Base64Signature);
        } catch (RestException e) {
            throw e;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
    
    /**
     * Advanced method that can be used to send any MSS_SignatureReq to the MSSP.
     * @param req MSS_SignatureReq
     * @return MSS_SignatureREsp
     * @throws RestException if signature fails
     * @see MSS_SignatureReqBuilder
     */
    public MSS_SignatureResp sign(final MSS_SignatureReq req) throws RestException {
        try {
            JsonRequest jReq = new JsonRequest();
            jReq.MSS_SignatureReq = req;
            JsonResponse jResp = this.client.sendReq(jReq);
            return jResp.MSS_SignatureResp;
        } catch (RestException e) {
            throw e;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
    
    /**
     * Set the AP_PWD value used in the requests.
     * <p>Note that this value is different from the REST password
     * and the default value is enough in most cases.
     * @param appwd AP_PWD
     */
    public void setApPwd(final String appwd) {
        this.appwd = appwd;
    }
    
}
