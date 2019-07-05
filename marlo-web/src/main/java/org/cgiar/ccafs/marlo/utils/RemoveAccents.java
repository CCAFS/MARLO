package org.cgiar.ccafs.marlo.utils;

/*
 * URL Shortener
 */
public class RemoveAccents {


  public RemoveAccents() {
  }

  /*
   * @param text
   * @return true if the text has accents
   * @return false if the text has not accents
   */
  public Boolean hasAccents(String text) {
    System.out.println("entro 1 " + text);

    String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
    boolean contains = false;

    for (int i = 0; i < original.length(); i++) {
      if (text.contains(original.charAt(i) + "")) {
        contains = true;
        break;
      } else {
        contains = false;
      }
    }
    return contains;
  }


  public String remove(String text) {
    String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";

    String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";
    String output = text;
    for (int i = 0; i < original.length(); i++) {
      output = output.replace(original.charAt(i), ascii.charAt(i));
    }
    return output;
  }

}