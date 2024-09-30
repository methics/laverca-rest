//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved.
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;

public class MobileUser {

    /**
     * Default constructor
     */
    public MobileUser() {
        super();
    }

    public MobileUser(String msisdn) {
        this();
        this.MSISDN = msisdn;
    }

    @SerializedName("UserIdentifier")
    public String UserIdentifier;

    @SerializedName("MSISDN")
    public String MSISDN;

    public String getUserIdentifier() {
        return UserIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        UserIdentifier = userIdentifier;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String mSISDN) {
        MSISDN = mSISDN;
    }

}
