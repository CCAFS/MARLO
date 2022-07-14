/*****************************************************************
 * /*****************************************************************
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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author tonyshikali
 */
public class ExternalPostUtils {

  private String username = "";

  private String password = "";

  private String requestMethod = "POST";

  /**
   * Get the value of password
   *
   * @return the value of password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Get the value of requestMethod
   *
   * @return the value of requestMethod
   */
  public String getRequestMethod() {
    return requestMethod;
  }

  /**
   * Get the value of username
   *
   * @return the value of username
   */
  public String getUsername() {
    return username;
  }

  private String makeRequest(String method, String contentType, String parameters) {
    if (method.startsWith("https://")) {
      return this.makeSecureRequest(method, contentType, parameters);
    } else {
      return this.makeUnsecureRequest(method, contentType, parameters);
    }
  }

  private String makeSecureRequest(String endpoint, String contentType, String parameters) {

    String postResponse = "";

    try {
      if (!username.isEmpty()) {
        Authenticator.setDefault(new Authenticator() {

          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
          }
        });
      }
      URL url = new URL(endpoint);

      System.setProperty("jsse.enableSNIExtension", "false");
      SSLContext ctx = SSLContext.getInstance("SSL");

      TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      }};

      ctx.init(null, trustAllCerts, new java.security.SecureRandom());
      SSLSocketFactory sslFactory = ctx.getSocketFactory();
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.setSSLSocketFactory(sslFactory);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod(requestMethod);
      connection.setRequestProperty("Content-Type",
        contentType.isEmpty() ? "application/x-www-form-urlencoded" : contentType);
      connection.setRequestProperty("charset", "utf-8");

      // for (Map.Entry<String, String> entry : customProperties.entrySet()) {
      // connection.setRequestProperty(entry.getKey(), entry.getValue());
      // }

      connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
      connection.setUseCaches(false);
      connection.setConnectTimeout(30000);

      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(parameters);
      wr.flush();

      // Get the response
      BufferedReader rd = null;
      if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
        rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {
        System.out
          .println(requestMethod + " request to " + endpoint + " generated response " + connection.getResponseCode()
            + " " + connection.getResponseMessage() + " " + requestMethod + "ing\n" + parameters);
        if (connection.getErrorStream() != null) {
          rd = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        } else if (connection.getInputStream() != null) {
          rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
          postResponse = "Failed to " + requestMethod;
        }
      }

      String line;

      if (rd != null) {
        while ((line = rd.readLine()) != null) {
          postResponse += line + "\n";
        }
        rd.close();
      }
      wr.close();
      connection.disconnect();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return postResponse;
  }

  private String makeUnsecureRequest(String endpoint, String contentType, String parameters) {

    String postResponse = "";

    try {
      URL url = new URL(endpoint);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod(requestMethod);
      connection.setRequestProperty("Content-Type",
        contentType.isEmpty() ? "application/x-www-form-urlencoded" : contentType);
      connection.setRequestProperty("charset", "utf-8");
      connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));

      // for (Map.Entry<String, String> entry : customProperties.entrySet()) {
      // connection.setRequestProperty(entry.getKey(), entry.getValue());
      // }

      connection.setUseCaches(false);
      connection.setConnectTimeout(30000);

      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(parameters);
      wr.flush();

      // Get the response
      BufferedReader rd = null;
      if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
        rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {
        System.out
          .println(requestMethod + " request to " + endpoint + " generated response " + connection.getResponseCode()
            + " " + connection.getResponseMessage() + " " + requestMethod + "ing\n" + parameters);
        if (connection.getErrorStream() != null) {
          rd = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        } else if (connection.getInputStream() != null) {
          rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
          postResponse = "Failed to post";
        }
      }

      String line;
      if (rd != null) {
        while ((line = rd.readLine()) != null) {
          postResponse += line + "\n";
        }
        rd.close();
      }
      wr.close();
      connection.disconnect();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return postResponse;
  }

  public String postJson(String endpoint, String parameters) {
    return this.makeRequest(endpoint, "application/json", parameters);
  }

  /**
   * Set the value of password
   *
   * @param password new value of password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Set the value of requestMethod
   *
   * @param requestMethod new value of requestMethod
   */
  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  /**
   * Set the value of username
   *
   * @param username new value of username
   */
  public void setUsername(String username) {
    this.username = username;
  }


}