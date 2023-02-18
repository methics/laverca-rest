//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;


import com.google.gson.annotations.SerializedName;

public class AdditionalServices {
    
    public static final AdditionalServices VALIDATION_AS = new AdditionalServices("http://uri.etsi.org/TS102204/v1.1.2#validate");

    /**
     * Default constructor
     */
    public AdditionalServices() {
        // Do nothing
    }
    
    /**
     * Construct an AdditionalServices object with the given description
     * @param description
     */
    public AdditionalServices(final String description) {
        this.Description = description;
    }
    
    @SerializedName("Description")
    public String Description; // URI controlling parse of the rest of the data

}
