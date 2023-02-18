//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

public class AP_Info {
    
    public AP_Info() {
        super();
    }
    
    public AP_Info(String apid, String appwd) {
        this.AP_ID  = apid;
        this.AP_PWD = appwd;
    }
    
    @SerializedName("AP_ID")
    public String AP_ID;
    
    @SerializedName("AP_PWD")
    public String AP_PWD;

    @SerializedName("AP_TransID")
    public String AP_TransID;
    
    @SerializedName("Instant")
    public String Instant;

}
