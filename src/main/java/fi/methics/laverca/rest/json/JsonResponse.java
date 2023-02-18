//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class JsonResponse {
        
    protected static final Gson GSON = new GsonBuilder().create();
    
    @SerializedName("Fault")
    public Fault Fault;

    @SerializedName("MSS_SignatureResp")
    public MSS_SignatureResp MSS_SignatureResp;

    @SerializedName("MSS_StatusResp")
    public MSS_StatusResp    MSS_StatusResp;

    @SerializedName("MSS_ReceiptResp")
    public MSS_ReceiptResp   MSS_ReceiptResp;

    @SerializedName("MSS_ProfileResp")
    public MSS_ProfileResp   MSS_ProfileResp;
    
    /**
     * @param resp
     * @return
     */
    public static JsonResponse fromString(String resp) {
        return GSON.fromJson(resp, JsonResponse.class);
    }
    
    /**
     * Get the JSON response type from this message
     * @return
     */
    public ResponseType getResponseType() {
        if (this.Fault != null) {
            return ResponseType.Fault;
        }
        if (this.MSS_SignatureResp != null) {
            return ResponseType.MSS_SignatureResp;
        }
        if (this.MSS_StatusResp != null) {
            return ResponseType.MSS_StatusResp;
        }
        if (this.MSS_ReceiptResp != null) {
            return ResponseType.MSS_ReceiptResp;
        }
        if (this.MSS_ProfileResp != null) {
            return ResponseType.MSS_ProfileResp;
        }
        
        return ResponseType.UNKNOWN;
    }
    
    /**
     * Is the response a fault?
     * @return
     */
    public boolean isFault() {
        return this.getResponseType() == ResponseType.Fault;
    }
    
    /**
     * Get Fault message (e.g. UNKNOWN_CLIENT) 
     * @return Fault message (INTERNAL_ERROR if not available)
     */
    public String getFaultMessage() {
        if (!this.isFault())    return "INTERNAL_ERROR";
        if (this.Fault == null) return "INTERNAL_ERROR";
        return this.Fault.Reason;
    }
    
    /**
     * Get Fault message (e.g. "Unknown user") 
     * @return Fault message or null if not available
     */
    public String getFaultDetail() {
        if (!this.isFault())    return "Operation failed";
        if (this.Fault == null) return "Operation failed";
        return this.Fault.Detail;
    }
    
    /**
     * Get Fault code
     * @return Fault code. Defaults to "900" if not found.
     */
    public String getFaultCode() {
        if (this.Fault            == null) return "900";
        if (this.Fault.Code       == null) return "900";
        if (this.Fault.Code.Value == null) return "900";
        return this.Fault.Code.Value;
    }
    
    public String toJson() {
        return GSON.toJson(this);
    }
    
    @Override
    public String toString() {
        return this.toJson();
    }

    public enum ResponseType {
        Fault,
        MSS_SignatureResp,
        MSS_StatusResp,
        MSS_ReceiptResp,
        MSS_ProfileResp,
        MSS_RegistrationResp,
        UseCaseManagementResp,
        UNKNOWN
    }
}