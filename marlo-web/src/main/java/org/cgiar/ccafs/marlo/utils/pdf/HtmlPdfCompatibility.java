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

package org.cgiar.ccafs.marlo.utils.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * Makes the HTML that Handlebars produced render like html-pdf (PhantomJS) when it goes through OpenHTMLtoPDF
 * (CHG-LPRE-FN-004, design section 7.3). The HTML is parsed leniently with jsoup, edited as a jsoup document and
 * converted to a W3C DOM. No DOCTYPE or external entity is resolved: jsoup never parses XML here and
 * {@link W3CDom} builds the DOM from scratch.
 */
final class HtmlPdfCompatibility {

  private static final String COMPAT_CSS_RESOURCE = "/pdf/html-pdf-compat.css";

  /** A CSS length as html-pdf reads it from the page options, for example {@code 40mm} or {@code 1.5cm}. */
  private static final Pattern CSS_LENGTH = Pattern.compile("^(?:0|\\d+(?:\\.\\d+)?(?:px|mm|cm|in|pt|pc))$");

  private static final String ZERO = "0";

  private static volatile String compatCss;

  /**
   * Applies the compatibility edits to {@code html}.
   *
   * @param html the HTML produced by Handlebars.
   * @param headerHeight CSS length of the page header (blank or invalid: 0).
   * @param footerHeight CSS length of the page footer (blank or invalid: 0).
   * @param orientation {@code landscape}, anything else is portrait.
   * @return the edited jsoup document.
   */
  static Document transform(String html, String headerHeight, String footerHeight, String orientation) {
    Document doc = Jsoup.parse(html == null ? "" : html);

    for (Element style : doc.select("style")) {
      String css = stripAtRules(style.data());
      style.empty();
      style.appendChild(new DataNode(css));
    }

    trimWhitespaceBesideBr(doc);

    StringBuilder pageCss = new StringBuilder();
    StringBuilder firstPageCss = new StringBuilder();
    List<Element> running = new ArrayList<Element>();
    boolean landscape = orientation != null && "landscape".equals(orientation.trim().toLowerCase(Locale.ROOT));

    pageCss.append("@page { size: A4 ").append(landscape ? "landscape" : "portrait").append("; margin: ")
      .append(cssLength(headerHeight)).append(" 0 ").append(cssLength(footerHeight)).append(" 0;");

    addSlot(doc, "pageHeader", "@top-center", "pdfHeader", " vertical-align: top;", pageCss, firstPageCss, running);
    addSlot(doc, "pageFooter", "@bottom-center", "pdfFooter", "", pageCss, firstPageCss, running);
    pageCss.append(" }");
    if (firstPageCss.length() > 0) {
      pageCss.append("\n@page :first { ").append(firstPageCss).append("}");
    }

    // Running elements go first in <body>, in the order they were collected.
    for (int i = running.size() - 1; i >= 0; i--) {
      Element element = running.get(i);
      element.remove();
      doc.body().prependChild(element);
    }

    doc.head().appendElement("style").appendChild(new DataNode(pageCss.toString()));
    doc.head().appendElement("style").appendChild(new DataNode(compatCss()));
    return doc;
  }

  /**
   * Same as {@link #transform(String, String, String, String)}, converted to the W3C DOM the renderer takes.
   */
  static org.w3c.dom.Document toW3cDocument(String html, String headerHeight, String footerHeight,
    String orientation) {
    return new W3CDom().fromJsoup(transform(html, headerHeight, footerHeight, orientation));
  }

  /**
   * Registers the default and the first-page variant of one slot (header or footer): the element becomes a running
   * element and a margin box is generated for it.
   */
  private static void addSlot(Document doc, String id, String marginBox, String runningName, String extraBoxCss,
    StringBuilder pageCss, StringBuilder firstPageCss, List<Element> running) {
    Element regular = doc.getElementById(id);
    Element first = doc.getElementById(id + "-1");
    if (first == null) {
      first = doc.getElementById(id + "-first");
    }
    if (regular != null) {
      makeRunning(regular, runningName);
      running.add(regular);
      pageCss.append(' ').append(marginBox).append(" { content: element(").append(runningName)
        .append("); width: 100%;").append(extraBoxCss).append(" }");
    }
    if (first != null) {
      makeRunning(first, runningName + "First");
      running.add(first);
      firstPageCss.append(marginBox).append(" { content: element(").append(runningName).append("First); width: 100%;")
        .append(extraBoxCss).append(" } ");
    }
  }

  private static String compatCss() {
    String css = compatCss;
    if (css == null) {
      try (InputStream in = HtmlPdfCompatibility.class.getResourceAsStream(COMPAT_CSS_RESOURCE)) {
        if (in == null) {
          throw new IllegalStateException("Missing classpath resource " + COMPAT_CSS_RESOURCE);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = in.read(buffer);
        while (read >= 0) {
          out.write(buffer, 0, read);
          read = in.read(buffer);
        }
        css = new String(out.toByteArray(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new IllegalStateException("Cannot read classpath resource " + COMPAT_CSS_RESOURCE, e);
      }
      compatCss = css;
    }
    return css;
  }

  private static String cssLength(String value) {
    if (value == null) {
      return ZERO;
    }
    String trimmed = value.trim();
    return CSS_LENGTH.matcher(trimmed).matches() ? trimmed : ZERO;
  }

  private static boolean insideVerbatimElement(Element element) {
    for (Element parent : element.parents()) {
      String tag = parent.tagName();
      if ("pre".equals(tag) || "textarea".equals(tag)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isBlankText(Node node) {
    return node instanceof TextNode && ((TextNode) node).isBlank();
  }

  /** Appends {@code position: running(name)} to the element's own inline style, keeping what was there. */
  private static void makeRunning(Element element, String name) {
    String style = element.attr("style").trim();
    if (!style.isEmpty() && !style.endsWith(";")) {
      style = style + ";";
    }
    element.attr("style", (style.isEmpty() ? "" : style + " ") + "position: running(" + name + ");");
  }

  /**
   * Removes the at-rules {@code @page} and {@code @font-face} with their (possibly nested) blocks, matching braces
   * and skipping braces inside strings.
   */
  static String stripAtRules(String css) {
    String lower = css.toLowerCase(Locale.ROOT);
    StringBuilder out = new StringBuilder(css.length());
    int pos = 0;
    while (pos < css.length()) {
      int start = nextAtRule(lower, pos);
      if (start < 0) {
        out.append(css, pos, css.length());
        break;
      }
      out.append(css, pos, start);
      int open = css.indexOf('{', start);
      if (open < 0) {
        break;
      }
      pos = endOfBlock(css, open);
    }
    return out.toString();
  }

  private static int nextAtRule(String lower, int from) {
    int best = -1;
    for (String name : new String[] {"@page", "@font-face"}) {
      int index = lower.indexOf(name, from);
      while (index >= 0) {
        int after = index + name.length();
        char next = after < lower.length() ? lower.charAt(after) : ' ';
        if (!Character.isLetterOrDigit(next) && next != '-') {
          break;
        }
        index = lower.indexOf(name, after);
      }
      if (index >= 0 && (best < 0 || index < best)) {
        best = index;
      }
    }
    return best;
  }

  /** Returns the index just after the block that opens at {@code open}. */
  private static int endOfBlock(String css, int open) {
    int depth = 0;
    char quote = 0;
    for (int i = open; i < css.length(); i++) {
      char c = css.charAt(i);
      if (quote != 0) {
        if (c == '\\') {
          i++;
        } else if (c == quote) {
          quote = 0;
        }
      } else if (c == '"' || c == '\'') {
        quote = c;
      } else if (c == '{') {
        depth++;
      } else if (c == '}') {
        depth--;
        if (depth == 0) {
          return i + 1;
        }
      }
    }
    return css.length();
  }

  private static void trimWhitespaceBesideBr(Document doc) {
    for (Element br : doc.select("br")) {
      if (insideVerbatimElement(br)) {
        continue;
      }
      Node previous = br.previousSibling();
      while (isBlankText(previous)) {
        previous.remove();
        previous = br.previousSibling();
      }
      Node next = br.nextSibling();
      while (isBlankText(next)) {
        next.remove();
        next = br.nextSibling();
      }
    }
  }

  private HtmlPdfCompatibility() {
  }
}
