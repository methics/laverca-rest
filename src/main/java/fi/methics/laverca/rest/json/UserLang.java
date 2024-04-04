//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * FiCom UserLang AdditionalService element used to deliver user language.
 */
public class UserLang {

    @SerializedName("Value")
    public String Value;
    
    public UserLang(String value) {
        this.Value = value;
    }
    
    
}
