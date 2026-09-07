/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestConnectionUtil {

  private static final Logger LOG = LoggerFactory.getLogger(RestConnectionUtil.class);

  protected static Element getDocumentRoot(InputStreamReader stream) {
    try {
      SAXReader saxReader = new SAXReader();
      saxReader.setEntityResolver(new org.hibernate.internal.util.xml.DTDEntityResolver());
      saxReader.setMergeAdjacentText(true);
      return saxReader.read(stream).getRootElement();
    } catch (DocumentException de) {
      throw new RuntimeException(de);
    }
  }

  public String getJsonRestClient(String linkRequest) {
    HttpClient httpClient = HttpClientBuilder.create().build();
    httpClient = this.verifiedClient(httpClient);
    HttpResponse response = null;
    HttpGet getRequest = new HttpGet(linkRequest);
    getRequest.addHeader("accept", "application/json");
    try {
      response = httpClient.execute(getRequest);
    } catch (ClientProtocolException e) {
      LOG.error("The JSON request to {} failed with a protocol error", linkRequest, e);
    } catch (Exception e) {
      LOG.error("The JSON request to {} could not be executed", linkRequest, e);
    }

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
    }

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      String output;
      StringBuilder json = new StringBuilder();

      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      return json.toString();
    } catch (Exception e) {
      LOG.error("Could not read the JSON response of {}", linkRequest, e);
    }

    return null;


  }

  public String getJsonRestClientFromDOI(String linkRequest) {
    HttpClient httpClient = HttpClientBuilder.create().build();
    httpClient = this.verifiedClient(httpClient);
    HttpResponse response = null;
    HttpGet getRequest = new HttpGet(linkRequest);
    getRequest.addHeader("accept", "application/vnd.citationstyles.csl+json");
    try {
      response = httpClient.execute(getRequest);
    } catch (ClientProtocolException e) {
      LOG.error("The DOI citation request to {} failed with a protocol error", linkRequest, e);
    } catch (Exception e) {
      LOG.error("The DOI citation request to {} could not be executed", linkRequest, e);
    }

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
    }

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      String output;
      StringBuilder json = new StringBuilder();

      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      return json.toString();
    } catch (Exception e) {
      LOG.error("Could not read the DOI citation response of {}", linkRequest, e);
    }

    return null;


  }

  public Element getXmlRestClient(String linkRequest) {
    HttpClient httpClient = HttpClientBuilder.create().build();

    if (linkRequest.contains("https")) {
      httpClient = this.verifiedClient(httpClient);
    }


    HttpGet getRequest = new HttpGet(linkRequest);
    getRequest.addHeader("accept", "application/xml");

    HttpResponse response = null;
    try {
      response = httpClient.execute(getRequest);
    } catch (ClientProtocolException e) {
      LOG.error("The XML request to {} failed with a protocol error", linkRequest, e);
    } catch (Exception e) {
      LOG.error("The XML request to {} could not be executed", linkRequest, e);
    }

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
    }

    InputStreamReader br;
    try {
      br = new InputStreamReader((response.getEntity().getContent()));
      Element metadata = getDocumentRoot(br);
      return metadata;
    } catch (Exception e) {
      LOG.error("Could not parse the XML response of {}", linkRequest, e);
    }

    return null;

  }

  private HttpClient verifiedClient(HttpClient base) {
    try {
      SSLContext ctx = SSLContext.getInstance("SSL");
      X509TrustManager tm = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      };

      ctx.init(null, new TrustManager[] {tm}, null);
      HttpClientBuilder builder = HttpClientBuilder.create();
      SSLConnectionSocketFactory sslConnectionFactory =
        new SSLConnectionSocketFactory(ctx, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      builder.setSSLSocketFactory(sslConnectionFactory);
      Registry<ConnectionSocketFactory> registry =
        RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslConnectionFactory).build();
      HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
      builder.setConnectionManager(ccm);
      return builder.build();
    } catch (Exception ex) {
      LOG.error("Could not build the HTTP client that accepts every SSL certificate", ex);
      return null;
    }
  }

}
