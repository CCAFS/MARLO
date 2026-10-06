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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

/**
 * Turns the HTML body of an email into the text of its text/plain part, which the clients that do not render HTML
 * show instead, and which spam filters expect next to the HTML one. The text keeps the reading order and the
 * paragraphs of the HTML: list items become "- " lines, a link keeps its URL, and images and the conditional comments
 * of Outlook are dropped.
 */
public final class EmailPlainText {

  // Elements that end a paragraph: a blank line follows them.
  private static final Set<String> PARAGRAPHS =
    new HashSet<>(Arrays.asList("p", "h1", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "table", "blockquote"));

  // Elements that end a line.
  private static final Set<String> LINES = new HashSet<>(Arrays.asList("div", "tr", "li", "br", "hr"));

  private EmailPlainText() {
  }

  /**
   * Converts the HTML body of an email to plain text.
   *
   * @param html the body as sent in the text/html part; it may be a fragment.
   * @return the text, or an empty string when the body is null or has no text.
   */
  public static String fromHtml(String html) {
    if (html == null || html.trim().isEmpty()) {
      return "";
    }

    StringBuilder text = new StringBuilder();
    Element body = Jsoup.parseBodyFragment(html).body();
    NodeTraversor.traverse(new NodeVisitor() {

      @Override
      public void head(Node node, int depth) {
        if (node instanceof TextNode) {
          // Runs of spaces and line breaks in the source are layout, not text, as a browser reads them.
          text.append(((TextNode) node).getWholeText().replaceAll("[\\s\\u00A0]+", " "));
        } else if (node instanceof Element && "li".equals(((Element) node).normalName())) {
          // Each item starts its own line, with no blank line between the items of one list.
          if (text.length() > 0 && text.charAt(text.length() - 1) != '\n') {
            text.append('\n');
          }
          text.append("- ");
        }
      }

      @Override
      public void tail(Node node, int depth) {
        if (!(node instanceof Element)) {
          return;
        }
        Element element = (Element) node;
        String name = element.normalName();
        if ("a".equals(name)) {
          String href = element.attr("href").trim();
          // A mailto link already shows its address; a web link shows its URL unless its text is the URL.
          if (href.startsWith("http") && !element.text().trim().equals(href)) {
            text.append(" (").append(href).append(")");
          }
        } else if ("td".equals(name) || "th".equals(name)) {
          text.append(" ");
        } else if (PARAGRAPHS.contains(name)) {
          text.append("\n\n");
        } else if (LINES.contains(name)) {
          text.append("\n");
        }
      }
    }, body);

    return tidy(text.toString());
  }

  /**
   * Trims every line and leaves at most one blank line between paragraphs.
   */
  private static String tidy(String text) {
    StringBuilder tidy = new StringBuilder();
    int blankLines = 0;
    for (String line : text.split("\n", -1)) {
      String trimmed = line.trim();
      if (trimmed.isEmpty()) {
        blankLines++;
        continue;
      }
      if (tidy.length() > 0) {
        tidy.append(blankLines > 0 ? "\n\n" : "\n");
      }
      tidy.append(trimmed);
      blankLines = 0;
    }
    return tidy.toString();
  }
}
