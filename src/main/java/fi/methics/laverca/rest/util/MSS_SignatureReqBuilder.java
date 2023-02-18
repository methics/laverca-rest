//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved.
//
package fi.methics.laverca.rest.util;

import java.util.UUID;

import fi.methics.laverca.rest.json.MSS_SignatureReq;

/**
 * Builder for MSS_SignatureReq JSON objects
 * <p>Usage:
 * <pre>
 * MSS_SignatureReqBuilder builder = new MSS_SignatureReqBuilder();
 * builder.withMsisdn("35847001001");
 * MSS_SignatureReq req = builder.build();
 * </pre>
 */
public class MSS_SignatureReqBuilder {

    private String msisdn;
    private String dtbd;
    private DTBS   dtbs;
    
    private String signatureprofile;
    private String apid;
    private String appwd;
    private String mssFormat;
    
    public MSS_SignatureReqBuilder() {
        
    }
    
    public MSS_SignatureReq build() {
        MSS_SignatureReq req = new MSS_SignatureReq(this.msisdn, this.dtbs, this.dtbd);
        req.SignatureProfile = this.signatureprofile;
        req.MSS_Format       = this.mssFormat;
        req.AP_Info.AP_ID    = this.apid;
        req.AP_Info.AP_PWD   = this.appwd;
        req.AP_Info.AP_TransID = "A" + UUID.randomUUID().toString();
        return req;
    }
    
    public MSS_SignatureReqBuilder withApid(String apid) {
        this.apid = apid;
        return this;
    }
    
    public MSS_SignatureReqBuilder withAppwd(String appwd) {
        this.appwd = appwd;
        return this;
    }
    
    public MSS_SignatureReqBuilder withDtbd(String dtbd) {
        this.dtbd = dtbd;
        return this;
    }

    public MSS_SignatureReqBuilder withDtbs(DTBS dtbs) {
        this.dtbs = dtbs;
        return this;
    }
    
    public MSS_SignatureReqBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }
    
    public MSS_SignatureReqBuilder withSignatureProfile(String signatureprofile) {
        this.signatureprofile = signatureprofile;
        return this;
    }
    
}
