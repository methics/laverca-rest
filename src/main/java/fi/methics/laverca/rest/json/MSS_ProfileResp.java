//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MSS_ProfileResp extends MSS_AbstractMessage {

    @SerializedName("SignatureProfile")
    public List<String> SignatureProfile;

    @SerializedName("Status")
    public Status Status;
    
}
