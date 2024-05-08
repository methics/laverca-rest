//
//  (c) Copyright 2003-2024 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.json;

import com.google.gson.annotations.SerializedName;


/**
 * MSS AdditionalService that can be used to amend the request
 */
public class AdditionalService {
    
    public static final AdditionalService VALIDATION_AS = new AdditionalService("http://uri.etsi.org/TS102204/v1.1.2#validate");

    public static final String BATCH_AS         = "http://www.methics.fi/KiuruMSSP/v5.0.0#batchsign";
    public static final String NO_SPAM_AS       = "http://mss.ficom.fi/TS102204/v1.0.0#noSpam";
    public static final String EVENT_ID_AS      = "http://mss.ficom.fi/TS102204/v1.0.0#eventId";
    public static final String USER_LANG_AS     = "http://mss.ficom.fi/TS102204/v1.0.0#userLang";
    public static final String PERSON_ID_AS     = "http://mss.ficom.fi/TS102204/v1.0.0#personIdentity";
    public static final String VALIDATE_AS      = "http://mss.ficom.fi/TS102204/v1.0.0#validate";
    public static final String DISPLAY_NAME_AS  = "http://mss.ficom.fi/TS102204/v1.0.0#displayName";
    
    @SerializedName("Description")
    public String Description; // URI controlling parse of the rest of the data
    
    @SerializedName("EventID")
    public EventID EventID;
    
    @SerializedName( "NoSpamCode")
    public NoSpamCode NoSpamCode;
    
    @SerializedName("UserLang")
    public UserLang UserLang;
    
    /**
     * Default constructor
     */
    public AdditionalService() {
        // Do nothing
    }
    
    /**
     * Construct an AdditionalServices object with the given description
     * @param description
     */
    public AdditionalService(final String description) {
        this.Description = description;
    }
    
    /**
     * Create a new Validation AdditionalService.
     * @return AS
     */
    public static AdditionalService createValidationService() {
        return VALIDATION_AS;
    }

    /**
     * Create a new Validation AdditionalService.
     * @param eventid AP generated EventID to display to user
     * @return AS
     */
    public static AdditionalService createEventIdService(String eventid) {
        AdditionalService as = new AdditionalService(EVENT_ID_AS);
        as.EventID = new EventID(eventid);
        return as;
    }

    /**
     * Create a new NoSpamCode AdditionalService.
     * @param validate Should the NoSpamCode be validated?
     * @param nospam   The NoSpamCode entered by user
     * @return AS
     */
    public static AdditionalService createNoSpamCodeService(boolean validate, String nospam) {
        AdditionalService as = new AdditionalService(NO_SPAM_AS);
        as.NoSpamCode = new NoSpamCode(validate, nospam);
        return as;
    }

    /**
     * Create a new UserLang AdditionalService.
     * @param language Language to use. 2 characters.
     * @return AS
     */
    public static AdditionalService createLanguageService(String language) {
        AdditionalService as = new AdditionalService(USER_LANG_AS);
        as.UserLang = new UserLang(language);
        return as;
    }
    
}
