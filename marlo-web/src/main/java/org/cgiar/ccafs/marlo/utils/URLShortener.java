package org.cgiar.ccafs.marlo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * URL Shortener
 */
public class URLShortener {

  private static final int LENGTH_LINK = 80;

  public URLShortener() {
  }

  /*
   * @param text + url - to detect url links and shorten them
   * @return the text with tiny urllinks
   * @version 2
   */
  public String detectAndShortenLinks(String text) {
    StringBuilder referenceText = new StringBuilder(text);
    Matcher matcher = Pattern.compile("(https?://\\S+)").matcher(text);
    while (matcher.find()) {
      String url = matcher.group(1);
      if (url.length() > LENGTH_LINK) {
        String shortURL = null;
        try {
          shortURL = this.getShortUrlService(url);
        } catch (Exception e) {
          System.out.println(e);
        }
        if (shortURL != null) {
          referenceText.replace(matcher.start(1), matcher.end(1), shortURL);
        }
      }
    }
    return referenceText.toString();
  }

  /*
   * @param text - to detect url links and shorten them
   * @param number - Defines the minimum characters to shorten the text.
   * @return the text with tiny urllinks
   */
  public String detectAndShortenLinks(String text, int number) {
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

          if ((i + 1) == text.length()) {
            finalUrl = i;
          }
        }

        if (startUrl > 0) {
          if (finalUrl > 0) {

            url = text.substring(startUrl, finalUrl);
            if (url.length() > number) {
              String shortURL = null;
              try {
                shortURL = this.getShortUrlService(url);
              } catch (Exception e) {
                System.out.println(e);
              }

              if (shortURL != null) {
                referenceText = referenceText.replace(url, shortURL);
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

  /*
   * @param text + url - to detect url links and shorten them
   * @return the text with tiny urllinks
   */
  public String detectAndShortenLinksv2(String text) {
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

          if ((i + 1) == text.length()) {
            finalUrl = i + 1;
          }
        }

        if (startUrl > 0) {
          if (finalUrl > 0) {

            url = text.substring(startUrl, finalUrl);
            if (url.length() > LENGTH_LINK) {
              String shortURL = null;
              try {
                shortURL = this.getShortUrlService(url);
              } catch (Exception e) {
                System.out.println(e);
              }

              if (shortURL != null) {
                referenceText = referenceText.replace(url, shortURL);
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

  /*
   * @param url link
   * @return tinyURL link
   */
  public String getShortUrlService(String link) {
    String output = null;
    String shortUrl = null;
    link = link.trim();

    if (link.length() > LENGTH_LINK) {

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
        shortUrl = link;
        e.printStackTrace();
      } catch (IOException e) {
        shortUrl = link;
        e.printStackTrace();
      }
    } else {
      shortUrl = link;
    }

    return shortUrl;
  }
}