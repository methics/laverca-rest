//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MSS_ReceiptResp extends MSS_AbstractMessage {

    @SerializedName("Status")
    public Status Status;

    @SerializedName("ServiceResponses")
    public List<ServiceResponses> ServiceResponses;
    
}
