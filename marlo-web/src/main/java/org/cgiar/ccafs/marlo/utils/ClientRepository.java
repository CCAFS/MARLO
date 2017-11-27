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
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
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
import org.json.JSONObject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ClientRepository {

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

  /**
   * Read a restul JSON Url page and return these information into a String
   * 
   * @param urlString - the URL
   * @return - String whit the JSON Metadata Structure
   * @throws Exception - Can not connect The url.
   */
  private static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer buffer = new StringBuffer();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1) {
        buffer.append(chars, 0, read);
      }

      return buffer.toString();
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  private final String CGSPACEHANDLE = "https://cgspace.cgiar.org/rest/handle/{0}";

  public JSONObject getMetadata(String linkRequest, String id) {

    Gson gson = new Gson();
    JSONObject jo = new JSONObject();

    try {
      /*
       * OaiPmhServer server = new OaiPmhServer(linkRequest);
       * Record record = server.getRecord(id, "oai_dc");
       */


      String handleUrl = CGSPACEHANDLE.replace("{0}", id.replace("oai:cgspace.cgiar.org:", ""));

      RestConnectionUtil connection = new RestConnectionUtil();

      Element elementHandle = connection.getXmlRestClient(handleUrl);

      id = elementHandle.element("id").getStringValue();

      linkRequest = linkRequest.replace("{0}", id);
      Element metadata = this.getXmlRestClient(linkRequest);
      List<String> authors = new ArrayList<String>();
      List<Element> elements = metadata.elements();
      for (Element element : elements) {
        Element key = element.element("key");
        Element value = element.element("value");
        String keyValue = key.getStringValue();
        keyValue = keyValue.substring(3);
        if (keyValue.equals("contributor.author")) {
          authors.add(value.getStringValue());
        } else {
          if (jo.has(keyValue)) {
            jo.put(keyValue, jo.get(keyValue) + "," + value.getStringValue());
          } else {
            jo.put(keyValue, value.getStringValue());
          }
        }


      }

      jo.put("contributor.author", authors);
    } catch (Exception e) {
      e.printStackTrace();
      jo = null;
      e.printStackTrace();
    }

    return jo;
  }

  public String getMetadataIFPRI(String linkRequest, String id) {

    // Documentation
    // https://www.oclc.org/support/services/contentdm/help/customizing-website-help/other-customizations/contentdm-api-reference.en.html

    try {
      return readUrl(linkRequest + "?q=" + id);
    } catch (Exception e) {
      e.printStackTrace();
      return "Not Found";
    }
  }

  public String getMetadataILRI(String linkRequest, String id) {

    // ILRI outputs METADATA is harvest following an API in JSON Format
    // http://data.ilri.org/portal/harvestinfo

    try {
      return readUrl(linkRequest + "?id=" + id);
    } catch (Exception e) {
      e.printStackTrace();
      return "Not Found";
    }
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
