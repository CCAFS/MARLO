package org.cgiar.ccafs.marlo.utils;

/*
 * URL Shortener
 */
public class PlaintTextToHTML {

  public String getHTMLLinks(String link) {
    // Get the html part of the References text into url var
    String studiesReference = null, url = null, referenceText = null;
    if (studiesReference.length() > 0 && studiesReference.contains("http")) {
      String textPart = null;
      int startUrl = 0, finalUrl = 0;

      for (int i = 0; i < studiesReference.length() - 6; i++) {

        if (i < studiesReference.length() - 6) {

          if ((studiesReference.charAt(i) == 'h' && studiesReference.charAt(i + 1) == 't'
            && studiesReference.charAt(i + 2) == 't' && studiesReference.charAt(i + 3) == 'p'
            && studiesReference.charAt(i + 4) == ':' && studiesReference.charAt(i + 5) == '/'
            && studiesReference.charAt(i + 6) == '/')
            || (studiesReference.charAt(i) == 'h' && studiesReference.charAt(i + 1) == 't'
              && studiesReference.charAt(i + 2) == 't' && studiesReference.charAt(i + 3) == 'p'
              && studiesReference.charAt(i + 4) == 's' && studiesReference.charAt(i + 5) == ':'
              && studiesReference.charAt(i + 6) == '/' && studiesReference.charAt(i + 7) == '/')) {
            startUrl = i;
            textPart = studiesReference.substring(1, i - 1);
            i = i + 6;
          }
        }
        if (studiesReference.charAt(i) == '<' && studiesReference.charAt(i + 1) == 'b'
          && studiesReference.charAt(i + 2) == 'r' && studiesReference.charAt(i + 3) == '>' && startUrl > 0) {
          finalUrl = i - 1;
          i = i + 3;
        }

        if (startUrl > 0) {
          if (finalUrl > 0) {
            // System.out.println("url " + url);
            // System.out.println("textpart " + textPart);
            url = studiesReference.substring(startUrl, finalUrl);
            referenceText += "<a href=\"" + url + "\">" + textPart + "</a><br>";

            startUrl = 0;
            finalUrl = 0;
            url = "";
            textPart = "";
          }
        }
      }

    }
    referenceText = "<a href=\"" + url + "\">" + studiesReference + "</a><br>";
    return referenceText;
  }
}
