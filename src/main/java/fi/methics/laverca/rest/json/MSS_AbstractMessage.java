//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class MSS_AbstractMessage {

    protected static final Gson GSON = new GsonBuilder().create();
    
    @SerializedName("MajorVersion")
    public String MajorVersion;
    
    @SerializedName("MinorVersion")
    public String MinorVersion;
    
    @SerializedName("AP_Info")
    public AP_Info  AP_Info;
    
    @SerializedName("MSSP_Info")
    public MSSP_Info MSSP_Info;
    
    @Override
    public String toString() {
        return GSON.toJson(this);
    }

}
