package org.netmelody.cieye.witness.protocol;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

public final class RestRequester {

    private static final Log LOG = LogFactory.getLog(RestRequester.class);
    
    private final DefaultHttpClient client;

    public RestRequester() {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        final ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(schemeRegistry);
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);
         
        final HttpParams params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        
        client = new DefaultHttpClient(connectionManager, params);
    }
    
    public String makeRequest(String url) {
        LOG.info(url);
        try {
            final HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");

            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return client.execute(httpget, responseHandler);
        }
        catch (HttpResponseException e) {
            if (e.getStatusCode() == 404) {
                LOG.info(url + " - 404 Not Found", e);
                return "";
            }
            LOG.error(url, e);
        }
        catch (Exception e) {
            LOG.error(url, e);
//            httpget.abort();
        }
        return "";
    }

    public void performBasicAuthentication(String username, String password) {
        client.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
                                                       new UsernamePasswordCredentials(username, password));
    }
    
    public void doPost(String url, Map<String, String> parameterValues) {
        LOG.info(url);
        try {
            final HttpPost httpPost = new HttpPost(url);
            final BasicHttpParams params = new BasicHttpParams();
            for (Entry<String, String> parameterValue : parameterValues.entrySet()) {
                params.setParameter(parameterValue.getKey(), parameterValue.getValue());
            }
            httpPost.setParams(params);
            
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            client.execute(httpPost, responseHandler);
        }
        catch (Exception e) {
            LOG.error(url, e);
        }
    }
    
    public void shutdown() {
        try {
            client.getConnectionManager().shutdown();
        }
        catch (Exception e) {
            LOG.error("error shutting down", e);
        }
    }
}
