//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

public class MSS_ProfileReq extends MSS_AbstractMessage {

    @SerializedName("MobileUser")
    public MobileUser MobileUser;

    @SerializedName("Params")
    public String Params; 
    
    public MSS_ProfileReq(final String msisdn) {
        this.MobileUser = new MobileUser();
        this.MobileUser.MSISDN = msisdn;
        
        this.AP_Info = new AP_Info();
    }
    
}
