//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved.
//
package fi.methics.laverca.rest.util;

import java.util.ArrayList;
import java.util.List;

import fi.methics.laverca.rest.json.Input;
import fi.methics.laverca.rest.json.MSS_RegistrationReq;

/**
 * Builder for MSS_RegistrationReq JSON objects
 * <p>
 * Usage:
 * 
 * <pre>
 * MSS_RegistrationReqBuilder builder = new MSS_SignatureReqBuilder("mids", "GetMobileUser");
 * builder.withTargetMsisdn("35847001001");
 * MSS_RegistrationReq req = builder.build();
 * </pre>
 */
public class MSS_RegistrationReqBuilder {

    public static final int DEFAULT_TIMEOUT = 60000; // 60 s

    private String name;
    private String namespace;

    private List<Input> inputParams = new ArrayList<>();

    public MSS_RegistrationReqBuilder(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    /**
     * Build the MSS_SignatureReq
     * 
     * @return MSS_SignatureReq
     */
    public MSS_RegistrationReq build() {
        MSS_RegistrationReq req = new MSS_RegistrationReq(this.namespace, this.name);
        req.UseCase.Inputs = this.inputParams;
        return req;
    }

    public MSS_RegistrationReqBuilder withParam(String name, String value) {
        this.inputParams.add(new Input(name, value));
        return this;
    }

    public MSS_RegistrationReqBuilder withOptionalParam(String name, String value) {
        if (value != null) {
            this.inputParams.add(new Input(name, value));
        }
        return this;
    }

    public MSS_RegistrationReqBuilder withTargetMsisdn(String msisdn) {
        this.inputParams.add(new Input("targetmsisdn", msisdn));
        return this;
    }

    public MSS_RegistrationReqBuilder withTargetIccid(String iccid) {
        this.inputParams.add(new Input("targeticcid", iccid));
        return this;
    }

    public MSS_RegistrationReqBuilder withTargetImsi(String imsi) {
        this.inputParams.add(new Input("targetimsi", imsi));
        return this;
    }

    public MSS_RegistrationReqBuilder withTargetApId(String apid) {
        this.inputParams.add(new Input("targetapid", apid));
        return this;
    }

}
