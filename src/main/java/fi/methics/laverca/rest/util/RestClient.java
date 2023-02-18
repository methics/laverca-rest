//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest.util;


import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;

import fi.methics.laverca.rest.json.JsonRequest;
import fi.methics.laverca.rest.json.JsonResponse;

public class RestClient {

    private static final Log log = LogFactory.getLog(RestClient.class);
    
    private HttpClient httpClient;

    private String resturl;
    
    private String apid;
    private String apikey;
    
    private String apname;
    private String password;
    
    private AuthnMode mode;
    
    public RestClient() {
        this.httpClient = RestSocketFactory.getNewHttpClient();
        this.mode = AuthnMode.APIKEY; // default mode
    }
    
    public void setApId(final String apid) {
        this.apid = apid;
    }
    
    public void setApiKey(final String apikey) {
        this.apikey = apikey;
    }
    
    public void setApName(final String apname) {
        this.apname = apname;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setRestUrl(final String resturl) {
        this.resturl = resturl;
    }
    
    public void setAuthnMode(final AuthnMode mode) {
        this.mode = mode;
    }
    
    /**
     * Send REST JSON request
     * @param jReq JSON request
     * @return JSON response
     * @throws RestException 
     */
    public JsonResponse sendReq(final JsonRequest jReq) throws RestException {
        final String req  = jReq.toJson();
        final String resp = this.sendReq(req);
        try {
            JsonResponse jResp = JsonResponse.fromString(resp);
            if (jResp.isFault()) {
                throw new RestException(jResp.getFaultCode(), jResp.getFaultDetail());
            }
            return jResp;
        } catch (RestException e) {
            throw e;
        } catch (Exception e) {
            throw new RestException(RestException.INTERNAL_ERROR, e.getMessage());
        }
    }
    
    /**
     * Send HTTP Post request (multipart)
     * @param jReq JSON Request
     * @return String Response
     * @throws RestException 
     */
    private String sendReq(final String jReq) throws RestException {
        log.debug("Connecting to " + this.resturl);
        log.debug("Sending request " + jReq);
        
        switch (this.mode) {
        case APIKEY:
            return this.sendHmacReq(jReq);
        case PASSWORD:
        default:
            return this.sendBasicReq(jReq);
        }
    }
    
    /**
     * Send HTTP Post request using HMAC authn
     * @param req       Req as raw String
     * @param targetUrl Target URL as String
     * @param userId    AP UserID
     * @param apiKey    AP API_KEY
     * @return String Response
     * @throws RestException 
     */
    private String sendHmacReq(final String req) throws RestException {
        
        String userid = this.getUserId(this.apid);
        String apikey = this.apikey;
        
        try {
            HmacHttpClient client = new HmacHttpClient(this.httpClient, userid, apikey);
            return client.send(req, this.resturl);
        } catch (IOException e) {
            log.error("Connection to " + this.resturl + " failed: " + e.getMessage());
            throw new RestException(RestException.UNABLE_TO_PROVIDE_SERVICES, e.getMessage());
        }
    }
    
    /**
     * Send HTTP Post request using BASIC username/password authn
     * @param req       Req as raw String
     * @param targetUrl Target URL as String
     * @param username  AP UserID
     * @param password  AP RestPassword
     * @return String Response
     * @throws RestException 
     */
    private String sendBasicReq(final String req) throws RestException {
        
        try {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.apname, this.password));
            HttpClientContext ctx = HttpClientContext.create();
            
            URL url = new URL(this.resturl);
            AuthCache authCache = new BasicAuthCache();
            HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            authCache.put(targetHost, new BasicScheme());
            
            ctx.setAuthCache(authCache);
            ctx.setCredentialsProvider(provider);

            HttpPost post = this.createPost(req, this.resturl);
            return this.getResponseBody(this.httpClient.execute(post, ctx));
        } catch (IOException e) {
            log.error("Connection to " + this.resturl + " failed (TestUtil): " + e.getMessage());
            throw new RestException(RestException.UNABLE_TO_PROVIDE_SERVICES, e.getMessage());
        }
    }
    
    /**
     * Get UserID from AP_ID
     * @param apid AP_ID
     * @return UserID
     */
    private String getUserId(final String apid) {
        try {
            return Base64.getEncoder().encodeToString(apid.getBytes("UTF-8"));
        } catch (Exception e) {
            log.debug("Could not encode " + apid + " to base64");
            return apid;
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
            post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        }
        return post;
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
    
    public static enum AuthnMode {
        APIKEY,
        PASSWORD
    }

}
