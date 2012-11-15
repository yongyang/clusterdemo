/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.dmr;

import org.apache.catalina.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.demos.server.dmr.DispatchResult;
import org.jboss.demos.server.dmr.Dispatcher;
import org.jboss.demos.server.dmr.ModelNode;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class SimpleDispatcher implements Dispatcher {

    private String domainApiUrl = "http://localhost:9990/management";
    private static final String APPLICATION_DMR_ENCODED = "application/dmr-encoded";

    public SimpleDispatcher(String DOMAIN_API_URL) {
        this.domainApiUrl = DOMAIN_API_URL;
    }

    public SimpleDispatcher() {
        this.domainApiUrl = "http://localhost:9990/management";
    }

    public DispatchResult execute(ModelNode operation)
    {

        try {
            createHttpClientConnection(operation);

            HttpURLConnection connection = createConnection();

            OutputStreamWriter out = new OutputStreamWriter( connection.getOutputStream());
            out.write(operation.toBase64String());
//            safeClose(out);

            InputStream inputStream = connection.getResponseCode()==200 ?
                    connection.getInputStream() : connection.getErrorStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            safeClose(in);

            DispatchResult dispatchResult = new DispatchResult(response.toString());
            dispatchResult.setResponseStatus(connection.getResponseCode());

            return dispatchResult;

        } catch (Exception e) {
            throw new RuntimeException("failed to execute operation", e);
        }

    }

    private HttpURLConnection createConnection() throws IOException {
        URL url = new URL(domainApiUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", APPLICATION_DMR_ENCODED);
        connection.setRequestProperty("Content-Type", APPLICATION_DMR_ENCODED);
        //用户名密码部分
        String username = "admin";
        String password = "123456";
//        String input = username + ":" + password;
//        String encoding = new String(Base64.encodeBytesToBytes(new String(username + ":" + password).getBytes()));
//        connection.setRequestProperty( "Proxy-Authorization","Basic "+encoding);

        String authorizationString = "Basic " + Base64.encode((username + ":" + password).getBytes());
        connection.setRequestProperty ("Proxy-Authorization", authorizationString);
        return connection;
    }

    private HttpURLConnection createHttpClientConnection(ModelNode operation) throws IOException {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, 9990, "ManagementRealm"),
                    new UsernamePasswordCredentials("admin", "123456"));

            HttpPost httppost = new HttpPost("http://localhost:9990/management");
            httppost.setEntity(new StringEntity(operation.toBase64String()));
            httppost.setHeader("Accept", APPLICATION_DMR_ENCODED);
            httppost.setHeader("Content-Type", APPLICATION_DMR_ENCODED);

            System.out.println("executing request " + httppost.getRequestLine() + ", " + Arrays.toString(httppost.getAllHeaders()));

            System.out.println(operation.toString());

            HttpResponse response;
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();


            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));

                StringBuffer sb = new StringBuffer();
                String s = null;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                }

                System.out.println(ModelNode.fromBase64(sb.toString()).toString());

            }
            if (entity != null) {
                EntityUtils.consume(entity);
            }

            httpclient.getConnectionManager().shutdown();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void safeClose(Closeable me)
    {
        try {
            me.close();
        } catch (IOException e) {
            // skip
            System.out.println("Failed to close stream (ignored): "+e.getMessage());
        }
    }
}
