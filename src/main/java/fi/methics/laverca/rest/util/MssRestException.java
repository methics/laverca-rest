//
//  (c) Copyright 2003-2024 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.util;

/**
 * MSS REST API exception
 * <p>Contains an error code and a message
 */
public class MssRestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String WRONG_PARAM                = "101";
    public static final String MISSING_PARAM              = "102";
    public static final String WRONG_DATA_LENGTH          = "103";
    public static final String UNAUTHORIZED_ACCESS        = "104";
    public static final String UNKNOWN_USER               = "105";
    public static final String INAPPROPRIATE_DATA         = "107";
    public static final String INCOMPATIBLE_INTERFACE     = "108";
    public static final String UNSUPPORTED_PROFILE        = "109";
    public static final String EXPIRED_TRANSACTION        = "208";
    public static final String OTA_ERROR                  = "209";
    public static final String USER_CANCEL                = "401";
    public static final String PIN_NR_BLOCKED             = "402";
    public static final String CARD_BLOCKED               = "403";
    public static final String NO_KEY_FOUND               = "404";
    public static final String NO_URL_FOUND               = "405";
    public static final String PB_SIGNATURE_PROCESS       = "406";
    public static final String REGISTRATION_NOK           = "407";
    public static final String NO_CERT_FOUND              = "422";
    public static final String CRL_PB                     = "423";
    public static final String CRL_EXPIRED                = "424";
    public static final String ERROR_CERTIFICATE          = "425";
    public static final String INVALID_SIGNATURE          = "503";
    public static final String UNABLE_TO_PROVIDE_SERVICES = "780";
    public static final String INTERNAL_ERROR             = "900";
    
    private final String code;     // Fault SubCode (e.g. "900")
    private final String message;  // Fault Message (e.g. "INTERNAL_ERROR")
    private final String detail;   // Fault Detail  (e.g. "No such MobileUser")
    
    /**
     * Create an MSS Exception with ErrorCode and Detail
     * @param code   MSS ErrorCode (e.g. "900")
     * @param detail Error Details (e.g. "No such MobileUser")
     */
    public MssRestException(String code, String detail) {
        super(detail);
        this.code    = code;
        this.detail  = detail;
        this.message = null;
    }
    
    /**
     * Create an MSS Exception with ErrorCode and Detail
     * @param code    MSS ErrorCode    (e.g. "900")
     * @param message MSS ErrorMessage (e.g. "INTERNAL_ERROR")
     * @param detail  Error Details    (e.g. "No such MobileUser")
     */
    public MssRestException(String code, String message, String detail) {
        super(detail);
        this.code    = code;
        this.detail  = detail;
        this.message = message;
    }
    /**
     * Create an MSS Exception with a Throwable.
     * <p>Sets the ErrorCode to {@link #INTERNAL_ERROR}</p>
     * @param t Any Throwable
     */
    public MssRestException(Throwable t) {
        super(t);
        this.code    = "900";
        this.message = INTERNAL_ERROR;
        this.detail  = null;
    }
    
    /**
     * Create an MSS Exception with given code and a Throwable
     * @param code MSS ErrorCode (e.g. "900")
     * @param t    Any Throwable
     */
    public MssRestException(String code, Throwable t) {
        super(t);
        this.code    = code;
        this.message = null;
        this.detail  = null;
    }
    
    /**
     * Get the MSS ErrorCode (e.g. "900")
     * @return MSS ErrorCode
     */
    public String getErrorCode() {
        if (this.code == null) return INTERNAL_ERROR;
        return this.code;
    }
    
    /**
     * Get MSS ErrorMessge (e.g. "INTERNAL_ERROR")
     * @return MSS ErrorMessage
     */
    public String getErrorMessage() {
        if (this.message == null) {
            return getMessage(this.code);
        }
        return this.message;
    }
    
    // Returns: MSS:900 - Internal Error
    @Override
    public String getMessage() {
        if (this.detail != null) {
            return "MSS:" + this.getErrorCode() + " - " + this.detail;
        } else {
            return "MSS:" + this.getErrorCode() + " - " + super.getMessage();
        }
    }
    
    /**
     * Get MSS ErrorMessage from MSS ErrorCode
     * @param code ErrorCode (e.g. "900")
     * @return MSS ErrorMessage (e.g. "INTERNAL_ERROR")
     */
    public static String getMessage(String code) {
        if (code == null) return "INTERNAL_ERROR";
        code = code.replace("_", "");
        switch (code) {
            case WRONG_PARAM: return "WRONG_PARAM";
            case MISSING_PARAM: return "MISSING_PARAM";
            case WRONG_DATA_LENGTH: return "WRONG_DATA_LENGTH";
            case UNAUTHORIZED_ACCESS: return "UNAUTHORIZED_ACCESS";
            case UNKNOWN_USER: return "UNKNOWN_CLIENT";
            case INAPPROPRIATE_DATA: return "INAPPROPRIATE_DATA";
            case INCOMPATIBLE_INTERFACE: return "INCOMPATIBLE_INTERFACE";
            case UNSUPPORTED_PROFILE: return "UNSUPPORTED_PROFILE";
            case EXPIRED_TRANSACTION: return "EXPIRED_TRANSACTION";
            case OTA_ERROR: return "OTA_ERROR";
            case USER_CANCEL: return "USER_CANCEL";
            case PIN_NR_BLOCKED: return "PIN_NR_BLOCKED";
            case CARD_BLOCKED: return "CARD_BLOCKED";
            case NO_KEY_FOUND: return "NO_KEY_FOUND";
            case NO_URL_FOUND: return "NO_URL_FOUND";
            case PB_SIGNATURE_PROCESS: return "PB_SIGNATURE_PROCESS";
            case REGISTRATION_NOK: return "REGISTRATION_NOK";
            case NO_CERT_FOUND: return "NO_CERT_FOUND";
            case CRL_PB: return "CRL_PB";
            case CRL_EXPIRED: return "CRL_EXPIRED";
            case ERROR_CERTIFICATE: return "ERROR_CERTIFICATE";
            case INVALID_SIGNATURE: return "INVALID_SIGNATURE";
            case UNABLE_TO_PROVIDE_SERVICES: return "UNABLE_TO_PROVIDE_SERVICES";
            case INTERNAL_ERROR:
            default:
                return "INTERNAL_ERROR";
        }
    }
    
}
