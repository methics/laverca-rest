//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.util;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Simple HTTP Client used for HMAC authentication
 */
public class HmacHttpClient {
    
    private static final Log log = LogFactory.getLog(HmacHttpClient.class);
    
    private HttpClient client;
    private String userId;
    private String apiKey;
    
    private String    rfc2822Pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
    private SimpleDateFormat rfc2822 = new SimpleDateFormat(this.rfc2822Pattern, Locale.US);
    
    /**
     * Create a new HMAC HttpClient
     * 
     * @param client Base KiuruHttpClient
     * @param userId UserId (aka username)
     * @param apiKey API_KEY (aka HMAC secret)
     */
    public HmacHttpClient(final HttpClient client,
                          final String userId, 
                          final String apiKey) 
    {
        this.client = client;
        this.userId = userId;
        this.apiKey = apiKey;
    }

    /**
     * Send a text based POST request (e.g. JSON)
     * 
     * @param request Request to send
     * @param url Request URL
     * @return response as String
     * @throws IOException if request sending fails
     */
    public String send(final String request, final String url) throws IOException {
        HttpPost post = this.createPost(request, url);
        return this.sendHttpRequest(post);
    }

    /**
     * Convert byte[] to hex
     * @param bytes byte[] to convert
     * @return Hex String
     */
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    /**
     * Add headers that HMAC authentication requires. 
     * @param req  HTTP Request
     * @param body Request body
     * @throws IOException Failed to create HMAC key. 
     */
    private void addHeaders(final HttpUriRequest req, final String body) throws IOException {
        Map<String, String> headers = this.requestHeaders(req,
                                                          body,
                                                          this.userId,
                                                          this.apiKey);
        for (String header : headers.keySet()) {
            req.addHeader(header, headers.get(header));
        }
    }
    
    /**
     * Create a HTTP Post
     * @param req Request as String
     * @param url Request URL
     * @return HTTP Post
     * @throws IOException
     */
    private HttpPost createPost(final String req, final String url) throws IOException {
        final HttpPost post = new HttpPost(url);
        final String body = req;
        if (!body.isEmpty()) {
            post.setEntity(new StringEntity(body, "UTF-8"));
        }
        
        this.addHeaders(post, body);
        this.printRequest(post, body);
        
        return post;
    }
    
    /**
     * Calculate a HMAC digest
     * @param content Content to digest
     * @param skey    HMAC secret key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private byte[] digest(final String content, final String skey) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] contentBytes = content.getBytes();
        Mac mac = Mac.getInstance("HMACSHA256");
        SecretKeySpec macKey = new SecretKeySpec(skey.getBytes(), "RAW");
        mac.init(macKey);
        return mac.doFinal(contentBytes);
    }
    
    /**
     * Get the host parameter from HTTP Request
     * @param req HTTP Request
     * @return host value (e.g. localhost or server-xyzzy:9061)
     */
    private String getHost(final HttpUriRequest req) {
        String host = req.getURI().getHost();
        int port    = req.getURI().getPort();
        if (port > 0 && port != 80 && port != 443) {
            host += ":" + port;
        }
        return host;
    }
    
    /**
     * Get response body
     * @param resp HTTP Response
     * @return Body as UTF-8 String
     * @throws ParseException if body fails to parse
     * @throws IOException if body fails to parse
     */
    private String getResponseBody(final HttpResponse resp) throws ParseException, IOException {
        return resp.getEntity() != null ? EntityUtils.toString(resp.getEntity(), "UTF-8") : "";
    }
    
    /**
     * Print the request to debug log
     * @param request Request to print
     * @param body    Request body
     */
    private void printRequest(final HttpRequest request, final String body) {
        log.debug("Request:");
        log.debug("  Headers: ");
        for (Header h: request.getAllHeaders()) {
            log.debug("    " + h);
        }
        log.debug("  Request line:");
        log.debug("    "  + request.getRequestLine());
        if (!body.isEmpty()) {
            log.debug("  Body:");
            log.debug("    " + body);
        }
    }

    /**
     * Return HTTP Basic Authentication ("Authorization" and "Date") headers.
     *
     * @param method request HTTP method
     * @param host request host string (without port and without the "https://" prefix)
     * @param path request path (including query params)
     * @param params request body parameters stringified
     * @param userId Base64(AP_ID)
     * @param apiKey Service Auth API key
     */
    private Map<String, String> requestHeaders(final HttpUriRequest req,
                                               String params,
                                               final String userId,
                                               final String apiKey) throws IOException {
        params = (params == null) ? "" : params;
        String date = this.rfc2822.format(new Date());
        URI  reqUri = req.getURI();
        String host   = this.getHost(req);
        String method = req.getMethod();
        String path   = reqUri.getPath();

        String[] values = new String[] { date, method, host, path, params };
        log.debug("Checksum data: " + Arrays.toString(values));

        StringBuilder data = new StringBuilder();
        for (String val : values) {
            data.append(val);
            data.append("\n");
        }

        String sig;
        try {
            sig = bytesToHex(this.digest(data.toString(), apiKey));
        } catch (Exception e) {
            log.debug("Crypto error while computing HMAC request signature", e);
            throw new IOException("Crypto error while computing HMAC request signature");
        }

        String auth = userId + ":" + sig;

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Date", date);
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
        headers.put("Content-Type",  "application/json");

        return headers;
    }
    
    /**
     * Send a HTTP request. 
     * @param req HTTP request
     * @return Response body as a string. 
     * @throws IOException Failed to parse response body. 
     * @throws IOException if non-ok status code is received
     */
    private String sendHttpRequest(final HttpRequestBase req)
        throws IOException
    {
        HttpResponse resp = this.client.execute(req);
        String       body = this.getResponseBody(resp);
        if (!body.isEmpty()) {
            log.debug("Got response body " + body);
        }
        if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            log.warn("Got HTTP status " + resp.getStatusLine().getStatusCode());
        }
        return body;
    }


}
