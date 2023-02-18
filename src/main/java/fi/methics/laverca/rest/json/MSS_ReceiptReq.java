//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

public class MSS_ReceiptReq extends MSS_AbstractMessage {

    @SerializedName("MSSP_TransID")
    public String MSSP_TransID;

    @SerializedName("MobileUser")
    public MobileUser MobileUser;

    @SerializedName("Status")
    public Status Status;

}
