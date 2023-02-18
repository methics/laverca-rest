//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class JsonRequest {
    
    protected static final Gson GSON = new GsonBuilder().create();
    
    @SerializedName("MSS_SignatureReq")
    public MSS_SignatureReq   MSS_SignatureReq;
    
    @SerializedName("MSS_StatusReq")
    public MSS_StatusReq   MSS_StatusReq;
    
    @SerializedName("MSS_ReceiptReq")
    public MSS_ReceiptReq   MSS_ReceiptReq;
    
    @SerializedName("MSS_ProfileReq")
    public MSS_ProfileReq   MSS_ProfileReq;
    
    public RequestType getRequestType() {
        if (this.MSS_SignatureReq != null) {
            return RequestType.MSS_SignatureReq;
        }
        if (this.MSS_StatusReq != null) {
            return RequestType.MSS_StatusReq;
        }
        if (this.MSS_ReceiptReq != null) {
            return RequestType.MSS_ReceiptReq;
        }
        if (this.MSS_ProfileReq != null) {
            return RequestType.MSS_ProfileReq;
        }
        return RequestType.UNKNOWN;
    }
    
    public String toJson() {
        return GSON.toJson(this);
    }
    
    @Override
    public String toString() {
        return this.toJson();
    }
    
    public enum RequestType {
        MSS_SignatureReq,
        MSS_StatusReq,
        MSS_ReceiptReq,
        MSS_ProfileReq,
        MSS_RegistrationReq,
        UseCaseManagementReq,
        UNKNOWN
    }
    
}
