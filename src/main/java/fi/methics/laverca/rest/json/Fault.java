//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

public class Fault {
    
    @SerializedName("Code")
    public Code Code;
    
    @SerializedName("Reason")
    public String Reason;
    
    @SerializedName("Node")
    public String Node;
    
    @SerializedName("Role")
    public String Role;
    
    @SerializedName("Detail")
    public String Detail;
    
    @SerializedName("Details")
    public Details Details;
    
    @SerializedName("Help")
    public String Help;
    
    @SerializedName("SessionID")
    public String SessionID;
    
    @SerializedName("RollbackFault")
    public Fault RollbackFault;
    
    public static class Code {

        @SerializedName("Value")
        public String Value;

        @SerializedName("ValueNs")
        public String ValueNs;
        
        @SerializedName("SubCode")
        public Code SubCode;
    }
    
    public static class Details {

        @SerializedName("FaultURL")
        public String FaultURL;
        
        @SerializedName("Hostname")
        public String Hostname;    

        @SerializedName("FaultNode")
        public String FaultNode;    

        @SerializedName("HttpErrorCode")
        public String HttpErrorCode;
        
    }
}
