//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved.
//
package fi.methics.laverca.rest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fi.methics.laverca.rest.MssClient;

public abstract class DocumentSigner {

    protected MssClient client;
    
    public DocumentSigner(MssClient client) {
        this.client = client;
    }
    
    public abstract ByteArrayOutputStream signDocument(final String msisdn,
                                                       final String message,
                                                       final InputStream is,
                                                       final String signatureProfile) throws IOException, RestException;
    
}
