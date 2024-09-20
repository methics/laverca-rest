//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved.
//
package fi.methics.laverca.rest.json;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import fi.methics.laverca.rest.util.DTBS;

public class MSS_SignHashRequest extends MSS_RegistrationReq {

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

    public MSS_SignHashRequest(final String msisdn, final DTBS dtbs, final String dtbd) {
        this.MessagingMode = "synch";
        this.MobileUser = new MobileUser();
        this.MobileUser.MSISDN = msisdn;

        
        this.DataToBeSigned = new Data(dtbs);

       
        if (dtbd != null) {
            this.DataToBeDisplayed = new Data();
            this.DataToBeDisplayed.Data = dtbd;
            this.DataToBeDisplayed.Encoding = DTBS.ENCODING_UTF8;  
            this.DataToBeDisplayed.MimeType = DTBS.MIME_TEXTPLAIN;  
        }

       
        this.AdditionalServices = new ArrayList<>();
        this.AP_Info = new AP_Info();  
    }

    /**
     * Add an additional service to this request
     * @param as AdditionalService
     */
    public void addAdditionalService(AdditionalService as) {
        if (this.AdditionalServices == null) {
            this.AdditionalServices = new ArrayList<>();
        }
        this.AdditionalServices.add(as);
    }

    /**
     * Inner class to represent DataToBeSigned or DataToBeDisplayed
     */
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
                // Set data with Base64 encoding of the DTBS bytes
                this.Data = Base64.getEncoder().encodeToString(dtbs.toBytes());
                this.Encoding = dtbs.getEncoding();
                this.MimeType = dtbs.getMimetype();
                if (this.MimeType == null) {
                    this.MimeType = DTBS.MIME_TEXTPLAIN;  // Default to text/plain if MIME type is not provided
                }
            }
        }
    }
}
