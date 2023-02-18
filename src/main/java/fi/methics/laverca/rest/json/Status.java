//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Status {

    @SerializedName("StatusCode")
    public StatusCode StatusCode;

    @SerializedName("StatusMessage")
    public String StatusMessage;

    @SerializedName("StatusDetail")
    public StatusDetail StatusDetail;
    
    public static class StatusCode {
        
        public StatusCode() {
            // Empty
        }
        
        public StatusCode(final String code) {
            this.Value = code;
        }
        
        public StatusCode(final Number code) {
            if (code != null) {
                this.Value = "" + code.longValue();
            }
        }
        
        @SerializedName("Value")
        public String Value;

        @SerializedName("StatusCode")
        public StatusCode StatusCode;
    }
    
    public static class StatusDetail {
        
        public StatusDetail() {
            // Empty
        }
        
        public StatusDetail(final String value) {
            this.StatusDetail = value;
        }
        
        //@SerializedName("ReceiptRequestExtension")
        //public ReceiptRequestExtension ReceiptRequestExtension;

        //@SerializedName("ReceiptResponseExtension")
        //public ReceiptResponseExtension ReceiptResponseExtension;
        
        @SerializedName("ProfileQueryExtension")
        public ProfileQueryExtension ProfileQueryExtension;
        
        @SerializedName("StatusDetail")
        public String StatusDetail;

    }
    
    public static class ProfileQueryExtension {

        @SerializedName("MobileUserCertificate")
        public List<MobileUserCertificate> MobileUserCertificate;
        
        @SerializedName("AutoActivation")
        public Boolean AutoActivation;
        
        @SerializedName("ServerSideSignature")
        public Boolean ServerSideSignature;
        
        @SerializedName("RecoveryCodeCreated")
        public Boolean RecoveryCodeCreated;
        
        //@SerializedName("MobileUser")
        //public Sscds Sscds;
        
        //@SerializedName("MobileUser")
        //public MobileUserV2 MobileUser;
    }
    
    public static class MobileUserCertificate {

        @SerializedName("X509Certificate")
        public List<String> X509Certificate;
        
        @SerializedName("X509SubjectName")
        public List<String> X509SubjectName;
        
        @SerializedName("Algorithm")
        public String Algorithm;
        
        @SerializedName("State")
        public String State;
        
        @SerializedName("SignatureProfiles")
        public List<String> SignatureProfiles;
        
    }
    
}
