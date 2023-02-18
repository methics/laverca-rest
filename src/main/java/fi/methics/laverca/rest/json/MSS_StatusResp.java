//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MSS_StatusResp extends MSS_AbstractMessage {

    @SerializedName("MobileUser")
    public MobileUser MobileUser;

    @SerializedName("MSS_Signature")
    public MSS_Signature MSS_Signature;

    @SerializedName("Status")
    public Status Status;

    @SerializedName("ServiceResponses")
    public List<ServiceResponses> ServiceResponses;
    
}
