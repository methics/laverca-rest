//
//  (c) Copyright 2003-2021 Methics Oy. All rights reserved. 
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import fi.methics.laverca.rest.json.JsonRequest;
import fi.methics.laverca.rest.json.JsonResponse;
import fi.methics.laverca.rest.json.MSS_ProfileReq;
import fi.methics.laverca.rest.json.MSS_SignatureReq;
import fi.methics.laverca.rest.json.Status.MobileUserCertificate;
import fi.methics.laverca.rest.util.DTBS;
import fi.methics.laverca.rest.util.RestClient;
import fi.methics.laverca.rest.util.RestClient.AuthnMode;
import fi.methics.laverca.rest.util.RestException;

public class MssClient {

    public static final String FORMAT_PKCS7       = "http://uri.etsi.org/TS102204/v1.1.2#PKCS7";
    public static final String FORMAT_CMS         = "http://uri.etsi.org/TS102204/v1.1.2#CMS-Signature";
    public static final String FORMAT_KIURU_PKCS1 = "http://www.methics.fi/KiuruMSSP/v3.2.0#PKCS1";
    public static final String FORMAT_FICOM_PKCS1 = "http://mss.ficom.fi/TS102204/v1.0.0#PKCS1";

    private static final String DEFAULT_APPWD = "x";
    
    private static final Log log = LogFactory.getLog(MssClient.class);
    
    private RestClient client;
    private String apid;
    private String appwd = DEFAULT_APPWD;
    
    private MssClient() { }
    
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
     * Get user Certificates with ProfileQuery
     * @param msisdn MSISDN of the user
     * @param signatureprofile Signatureprofile of the wanted chain
     * @return Certificates
     * @throws RestException
     */
    public List<X509Certificate> getCertificateChain(final String msisdn, final String signatureprofile) throws RestException {
        log.debug("Fetching profile of MSISDN " + msisdn);
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
                            log.warn("Failed to parse certificate " + cert, e);
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
     * Sign hash of a document
     * @param digest Document digest (application/x-sha256)
     * @param msisdn MSISDN
     * @param signatureprofile
     * @return
     * @throws RestException
     */
    public byte[] signDocument(final byte[] digest, final String msisdn, final String signatureprofile) throws RestException {
        log.debug("Signing document with MSISDN " + msisdn);
        try {
            JsonRequest jReq = new JsonRequest();
            final String dtbd = "Please sign document";
            final DTBS   dtbs = new DTBS(digest, DTBS.ENCODING_BASE64, DTBS.MIME_SHA256);

            jReq.MSS_SignatureReq = new MSS_SignatureReq(msisdn, dtbs, dtbd);
            jReq.MSS_SignatureReq.SignatureProfile = signatureprofile;
            jReq.MSS_SignatureReq.MSS_Format       = FORMAT_KIURU_PKCS1;
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
     * Set the AP_PWD value used in the requests.
     * @param appwd AP_PWD
     */
    public void setApPwd(final String appwd) {
        this.appwd = appwd;
    }
    
}
