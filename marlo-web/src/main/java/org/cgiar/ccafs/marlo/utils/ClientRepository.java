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

import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.util.DTDEntityResolver;
import org.json.JSONObject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ClientRepository {

  protected static Element getDocumentRoot(InputStreamReader stream) {
    try {
      SAXReader saxReader = new SAXReader();
      saxReader.setEntityResolver(new DTDEntityResolver());
      saxReader.setMergeAdjacentText(true);
      return saxReader.read(stream).getRootElement();
    } catch (DocumentException de) {
      throw new RuntimeException(de);
    }
  }

  private final String CGSPACEHANDLE = "https://cgspace.cgiar.org/rest/handle/{0}";

  public JSONObject getMetadata(String linkRequest, String id) {


    JSONObject jo = new JSONObject();

    try {
      /*
       * OaiPmhServer server = new OaiPmhServer(linkRequest);
       * Record record = server.getRecord(id, "oai_dc");
       */


      String handleUrl = CGSPACEHANDLE.replace("{0}", id.replace("oai:cgspace.cgiar.org:", ""));


      Element elementHandle = this.getXmlRestClient(handleUrl);
      id = elementHandle.element("id").getStringValue();

      linkRequest = linkRequest.replace("{0}", id);
      Element metadata = this.getXmlRestClient(linkRequest);
      List<Element> elements = metadata.elements();
      for (Element element : elements) {
        Element key = element.element("key");
        Element value = element.element("value");
        String keyValue = key.getStringValue();
        keyValue = keyValue.substring(3);

        if (jo.has(keyValue)) {
          jo.put(keyValue, jo.get(keyValue) + "," + value.getStringValue());
        } else {
          jo.put(keyValue, value.getStringValue());
        }


      }


    } catch (Exception e) {
      e.printStackTrace();
      jo = null;
      e.printStackTrace();
    }

    return jo;
  }

  public Element getXmlRestClient(String linkRequest) {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    httpClient = this.verifiedClient(httpClient);


    HttpGet getRequest = new HttpGet(linkRequest);
    getRequest.addHeader("accept", "application/xml");

    HttpResponse response = null;
    try {
      response = httpClient.execute(getRequest);
    } catch (ClientProtocolException e) {

      e.printStackTrace();
    } catch (Exception e) {

      e.printStackTrace();
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

      e.printStackTrace();
    }

    return null;

  }

  public DefaultHttpClient verifiedClient(HttpClient base) {
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
      SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      ClientConnectionManager mgr = base.getConnectionManager();
      SchemeRegistry registry = mgr.getSchemeRegistry();
      registry.register(new Scheme("https", 443, ssf));
      return new DefaultHttpClient(mgr, base.getParams());
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


}
