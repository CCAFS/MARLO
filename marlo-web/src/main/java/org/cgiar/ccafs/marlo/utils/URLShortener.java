package org.cgiar.ccafs.marlo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * URL Shortener
 */
public class URLShortener {

  public URLShortener() {

  }

  public String getShortUrlService(String link) {
    String output = null;
    try {

      URL url = new URL("http://tinyurl.com/api-create.php?url=" + link);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
      }
      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

      while ((output = br.readLine()) != null) {
        System.out.println(output);
      }
      conn.disconnect();

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return output;
  }
}