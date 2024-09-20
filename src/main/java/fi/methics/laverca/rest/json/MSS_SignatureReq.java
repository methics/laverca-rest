//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import fi.methics.laverca.rest.util.DTBS;


public class MSS_SignatureReq extends MSS_AbstractMessage {

    @SerializedName("MessagingMode")
    public String MessagingMode;
    
    @SerializedName("ValidityDate")
    public String ValidityDate;
    
    @SerializedName("TimeOut")
    public String TimeOut;

    @SerializedName("SignatureProfile")
    public String SignatureProfile;

    @SerializedName("MobileUser")
    public MobileUser MobileUser;

    @SerializedName("DataToBeSigned")
    public Data DataToBeSigned;
    
    @SerializedName("DataToBeDisplayed")
    public Data DataToBeDisplayed;

    @SerializedName("AdditionalServices")
    public List<AdditionalService> AdditionalServices;
        
    @SerializedName("MSS_Format")
    public String MSS_Format;
    
    public MSS_SignatureReq(final String msisdn, final DTBS dtbs, final String dtbd) {
        this.MessagingMode = "synch";
        this.MobileUser = new MobileUser();
        this.MobileUser.MSISDN = msisdn;
        
        this.DataToBeSigned = new Data();
        this.DataToBeSigned.Data     = dtbs.getText();
        this.DataToBeSigned.Encoding = dtbs.getEncoding();
        this.DataToBeSigned.MimeType = dtbs.getMimetype();
        
        if (dtbd != null) {
            this.DataToBeDisplayed = new Data();
            this.DataToBeDisplayed.Data = dtbd;
        }
        
        this.AdditionalServices = new ArrayList<>();
        this.AP_Info = new AP_Info();
    }
    
    /**
     * Add a new AdditionalService request to this MSS_SignatureReq
     * @param as AdditionalService
     */
    public void addAdditionalService(AdditionalService as) {
        if (this.AdditionalServices == null) {
            this.AdditionalServices = new ArrayList<>();
        }
        this.AdditionalServices.add(as);
    }
    
    public static class Data {
        
        @SerializedName("MimeType")
        public String MimeType;
        
        @SerializedName("Encoding")
        public String Encoding;
        
        @SerializedName("Data")
        public String Data;
        
        public Data() {
            
        }
        
        public Data(DTBS dtbs) {
            if (dtbs != null) {
                
                if (dtbs.isData()) {
                    this.Data     = Base64.getEncoder().encodeToString(dtbs.toBytes());
                    this.Encoding = "BASE64";
                } else {
                    this.Data     = dtbs.getText();
                    this.Encoding = dtbs.getEncoding();
                }
                this.MimeType = dtbs.getMimetype();
                if (this.MimeType == null) {
                    this.MimeType = "text/plain";
                }
            }
        }
        
    }
}