//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

public class MSSP_Info {
    
    @SerializedName("Instant")
    public String Instant;
    
    @SerializedName("MSSP_ID")
    public JsonMeshMember MSSP_ID;
}
