//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * FiCom NoSpamCode AdditionalService element that allows delivering NoSpamCode input by the user.
 */
public class NoSpamCode {

    // "yes" or "no"
    @SerializedName("Verify")
    public String Verify = "no";
    
    @SerializedName("Code")
    public String Code;
    
    public NoSpamCode(boolean verify, String code) {
        this.Verify = verify ? "yes" : "no";
        this.Code = code;
    }
    
}
