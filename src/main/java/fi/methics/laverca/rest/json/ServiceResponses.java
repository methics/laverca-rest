//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;


import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ServiceResponses {
    
    @SerializedName("Description")
    public String Description;

    @SerializedName("Status")
    public Status Status;

    @SerializedName("Roles")
    public List<String> Roles;
    
}
