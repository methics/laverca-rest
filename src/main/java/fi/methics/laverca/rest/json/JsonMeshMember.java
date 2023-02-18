//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

public class JsonMeshMember {
    
    @SerializedName("URI")
    public String URI;
    
    @SerializedName("DNSName")
    public String DNSName;
    
    @SerializedName("IPAddress")
    public String IPAddress;
    
    @SerializedName("IdentifierString")
    public String IdentifierString;
    
}
