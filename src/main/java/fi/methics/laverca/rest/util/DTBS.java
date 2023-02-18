//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.util;

import java.io.UnsupportedEncodingException;

/**
 * Dual-mode mapper class for "DataToBeSigned"
 */
public class DTBS {

    private String encoding = null;
    private String text     = null;
    private byte[] data     = null;
    private String mimeType = null;
    
    final static public String ENCODING_UTF8   = "UTF-8";
    final static public String ENCODING_BASE64 = "base64";
    
    final static public String MIME_STREAM    = "application/octet-stream";
    final static public String MIME_SHA1      = "application/x-sha1";
    final static public String MIME_SHA256    = "application/x-sha256";
    final static public String MIME_SHA384    = "application/x-sha384";
    final static public String MIME_UCS2      = "text/plain;ucs2";
    final static public String MIME_GSM       = "text/plain;gsm";
    final static public String MIME_UTF8      = "text/plain;UTF-8";
    final static public String MIME_TEXTPLAIN = "text/plain";

    /**
     * Initialize a DTBS with text.
     * 
     * @param text Text data
     * @param encoding Text encoding
     * @param mimeType Mime-type
     */
    public DTBS(final String text, final String encoding, final String mimeType) {
        this.text     = text;
        this.encoding = encoding;
        this.mimeType = mimeType;
    }
    
    /**
     * Initialize a DTBS with data.
     * 
     * @param data Binary data
     * @param encoding Text encoding
     * @param mimeType Mime-type
     */
    public DTBS(final byte[] data, final String encoding, final String mimeType) {
        this.data = data;
        this.encoding = encoding;
        this.mimeType = mimeType;
    }
    
    /**
     * Initialize a DTBS without a mime type for <code>toBytes()</code>
     * 
     * @param text Text data
     * @param encoding Text encoding
     */
    public DTBS(final String text, final String encoding) {
    	this(text, encoding, null);
    }

    /**
     * Initialize a DTBS without a mime type for <code>toBytes()</code>
     * This defaults encoding to UTF-8.
     * @param text Text data
     */
    public DTBS(final String text) {
    	this(text, ENCODING_UTF8);
    }

    public String  getText() { return this.text; }
    public byte[]  getData() { return this.data; }


    /**
     * Converter of incoming DTBS to byte-array, if the incoming
     * form happened to be a String, otherwise returning it as is.
     * 
     * @return byte[]
     * @throws RuntimeException when no text or data is found
     */
    public byte[] toBytes() {
        if (this.data != null) {
            return this.data;
        }
        if (this.text != null) {
            try {
                return this.text.getBytes(this.encoding);
            }
            catch (UnsupportedEncodingException uee) {
                throw new RuntimeException(uee);
            }
        }
        throw new RuntimeException("Illegal DTBS");
    }

    /**
     * Retrieve DTBS as a String - if it was a String at creation time.
     * If the DTBS was not a String, return either 
     * "-binary DTBS-" or 
     * "-llegal DTBS-" 
     * depending on whether the DTBS contains binary data.
     * 
     * @return String
     */
    @Override
    public String toString() {
        if (this.text != null) {
            return this.text;
        }
        else if (this.data != null) {
            return "-binary DTBS-";
        }
        else {
            return "-illegal DTBS-";
        }
    }

    public String getEncoding() {
        return this.encoding;
    }
    
    public String getMimetype() {
        return this.mimeType;
    }
    
    /**
     * Length of DTBS data, either the string, or the byte-array
     * 
     * @return int - length of data
     * @throws RuntimeException when no text or data is found
     */
    public int length() {
        if (this.text != null) {
            return this.text.length();
        }
        else if (this.data != null) {
            return this.data.length; 
        }
        else {
            throw new RuntimeException("Illegal DTBS");
        }
    }
}
