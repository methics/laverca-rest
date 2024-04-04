//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

/**
 * FiCom EventID AdditionalService element that delivers AP generated EventID to display to user.
 */
public class EventID {

    @SerializedName("Value")
    public String Value;
    
    public EventID(String value) {
        this.Value = value;
    }
    
}
