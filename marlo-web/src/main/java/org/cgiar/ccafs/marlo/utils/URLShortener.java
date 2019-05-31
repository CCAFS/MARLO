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

  private int wordsNumber;

  public URLShortener() {
    wordsNumber = 93;
  }

  public int getWordsNumber() {
    return wordsNumber;
  }

  public void setWordsNumber(int wordsNumber) {
    this.wordsNumber = wordsNumber;
  }

  public String convertText(String text) {
    String url = "", referenceText = text;
    if (text.length() > 0 && text.contains("http")) {
      String textPart = null;
      int startUrl = 0, finalUrl = 0;

      for (int i = 0; i < text.length(); i++) {
        if (i < text.length() - 6) {

          if ((text.charAt(i) == 'h' && text.charAt(i + 1) == 't' && text.charAt(i + 2) == 't'
            && text.charAt(i + 3) == 'p' && text.charAt(i + 4) == ':' && text.charAt(i + 5) == '/'
            && text.charAt(i + 6) == '/')
            || (text.charAt(i) == 'h' && text.charAt(i + 1) == 't' && text.charAt(i + 2) == 't'
              && text.charAt(i + 3) == 'p' && text.charAt(i + 4) == 's' && text.charAt(i + 5) == ':'
              && text.charAt(i + 6) == '/' && text.charAt(i + 7) == '/')) {
            startUrl = i;
            if (i > 1) {
              textPart = text.substring(1, i - 1);
            }
            i = i + 6;
          }

        }
        if (startUrl > 0) {
          if ((text.charAt(i) == '<' && text.charAt(i + 1) == 'b' && text.charAt(i + 2) == 'r'
            && text.charAt(i + 3) == '>')) {
            finalUrl = i - 1;
            i = i + 3;
          }
          if (text.charAt(i) == ')') {
            finalUrl = i - 1;
          }

          if (text.charAt(i) == '(') {
            finalUrl = i - 1;
          }

          if (text.charAt(i) == '[') {
            finalUrl = i - 1;
          }

          if (text.charAt(i) == ' ') {
            finalUrl = i - 1;
          }
        }

        if (startUrl > 0) {
          if (finalUrl > 0) {

            url = text.substring(startUrl, finalUrl);
            if (url.length() > 93) {
              String shortURL = null;
              try {
                shortURL = this.getShortUrlService(url);
                System.out.println("short url " + shortURL);
              } catch (Exception e) {
                System.out.println(e);
              }

              if (shortURL != null) {
                referenceText = referenceText.replaceAll(url, shortURL);
              }
            }

            startUrl = 0;
            finalUrl = 0;
            url = "";
            textPart = "";
          }
        }
      }
    }
    return referenceText;
  }

  public String getShortUrlService(String link) {
    String output = null;
    String shortUrl = null;
    try {

      URL url = new URL("http://tinyurl.com/api-create.php?url=" + link);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      if (conn.getResponseCode() != 200) {
        shortUrl = link;
        // throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
      } else {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        while ((output = br.readLine()) != null) {
          shortUrl = output;
        }
      }
      conn.disconnect();

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return shortUrl;
  }
}