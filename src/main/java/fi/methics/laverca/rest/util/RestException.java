//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.util;

public class RestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String INVALID_SIGNATURE          = "503";
    public static final String UNABLE_TO_PROVIDE_SERVICES = "780";
    public static final String INTERNAL_ERROR             = "900";

    private final String code;
    
    public RestException(String code, String msg) {
        super(msg);
        this.code = "900";
    }
    
    public RestException(Throwable t) {
        super(t);
        this.code = "900";
    }
    
    public RestException(String code, Throwable t) {
        super(t);
        this.code = code;
    }
    
    public String getErrorCode() {
        return this.code;
    }
    
}
