//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved.
//
package fi.methics.laverca.rest.util;
import  fi.methics.laverca.rest.json.JsonMeshMember;
import java.util.UUID;

import eu.europa.esig.dss.model.MimeType;
import fi.methics.laverca.rest.enums.Encoding;
import fi.methics.laverca.rest.json.AP_Info;
import fi.methics.laverca.rest.json.MSSP_Info;
import fi.methics.laverca.rest.json.MSS_ReceiptReq;
import fi.methics.laverca.rest.json.Message;
import fi.methics.laverca.rest.json.MobileUser;

/**
 * Builder for MSS_ReceiptReq JSON objects.
 * <p>
 * Usage:
 * 
 * <pre>
 * MSS_ReceiptReqBuilder builder = new MSS_ReceiptReqBuilder();
 * builder.withMsisdn("35847001001");
 * MSS_ReceiptReq req = builder.build();
 * </pre>
 */
public class MSS_ReceiptReqBuilder {

    private String apId;
    private String apTransId;
    private String instant;
    private String msspIdUri;
    private String majorVersion = "1";
    private String minorVersion = "1";
    private String msspTransId;
    private Message message;
    private String msisdn;

    public MSS_ReceiptReqBuilder() {
    }

    /**
     * Build the MSS_ReceiptReq
     * 
     * @return MSS_ReceiptReq
     */
    public MSS_ReceiptReq build() {
        MSS_ReceiptReq req = new MSS_ReceiptReq();

        AP_Info apInfo = new AP_Info();
        apInfo.AP_ID = this.apId;
        apInfo.AP_TransID = this.apTransId != null ? this.apTransId : "A" + UUID.randomUUID().toString();
        apInfo.Instant = this.instant;
        req.AP_Info = apInfo;

        MSSP_Info msspInfo = new MSSP_Info();
        msspInfo.MSSP_ID = new JsonMeshMember();
        msspInfo.MSSP_ID.URI=this.msspIdUri;

        req.MSSP_Info = msspInfo;

        req.MajorVersion = this.majorVersion;
        req.MinorVersion = this.minorVersion;
        req.MSSP_TransID = this.msspTransId != null ? this.msspTransId : "MESSAGEONLY";
        req.Message = this.message;

        MobileUser mobileUser = new MobileUser();
        mobileUser.MSISDN = this.msisdn;
        req.MobileUser = mobileUser;

        return req;
    }

    public MSS_ReceiptReqBuilder withApId(String apId) {
        this.apId = apId;
        return this;
    }

    public MSS_ReceiptReqBuilder withApTransId(String apTransId) {
        this.apTransId = apTransId;
        return this;
    }

    public MSS_ReceiptReqBuilder withInstant(String instant) {
        this.instant = instant;
        return this;
    }

    public MSS_ReceiptReqBuilder withMsspIdUri(String msspIdUri) {
        this.msspIdUri = msspIdUri;
        return this;
    }

    public MSS_ReceiptReqBuilder withMsspTransId(String msspTransId) {
        this.msspTransId = msspTransId;
        return this;
    }

    public MSS_ReceiptReqBuilder withMessage(MimeType mimeType, Encoding encoding, String data) {
        Message msg = new Message();
        msg.setMimeType(mimeType);
        msg.setEncoding(encoding);
        msg.setData(data);
        this.message = msg;
        return this;
    }

    public MSS_ReceiptReqBuilder withMessage(Encoding encoding, String data) {
        Message msg = new Message();
        msg.setMimeType(MimeType.TEXT);
        msg.setEncoding(encoding);
        msg.setData(data);
        this.message = msg;
        return this;
    }

    public MSS_ReceiptReqBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public MSS_ReceiptReqBuilder withMajorVersion(String majorVersion) {
        this.majorVersion = majorVersion;
        return this;
    }

    public MSS_ReceiptReqBuilder withMinorVersion(String minorVersion) {
        this.minorVersion = minorVersion;
        return this;
    }

}
