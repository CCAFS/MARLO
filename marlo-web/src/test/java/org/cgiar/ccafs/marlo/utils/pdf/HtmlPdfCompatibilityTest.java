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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.Test;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the html-pdf compatibility step (CHG-LPRE-FN-004, spike F4-F6).
 */
public class HtmlPdfCompatibilityTest {

  private static final String HTML = "<html><head><style>"
    + "@page { size: A4; margin: 10mm; @top-center { content: \"x\"; } }\n"
    + "@font-face { font-family: Montserrat; src: url(a.woff2) format('woff2'); }\n"
    + "p { color: red; }</style></head><body>"
    + "<p>Body</p>"
    + "<div id=\"pageFooter-1\" style=\"height: 5px\">F1</div>"
    + "<div id=\"pageHeader\" style=\"color: red\">H</div>"
    + "<div id=\"pageFooter\">F</div>"
    + "<p>a</p>\n<br>\n<p>b</p>"
    + "<pre>x<br> <br>y</pre>"
    + "</body></html>";

  private static Document transform() {
    return HtmlPdfCompatibility.transform(HTML, "40mm", "30mm", "portrait");
  }

  private static String generatedCss(Document doc) {
    StringBuilder css = new StringBuilder();
    for (Element style : doc.head().select("style")) {
      css.append(style.data()).append('\n');
    }
    return css.toString();
  }

  private static String runningName(Element element) {
    Matcher m = Pattern.compile("position:\\s*running\\(([^)]+)\\)").matcher(element.attr("style"));
    assertTrue("no running() in style of " + element.id(), m.find());
    return m.group(1);
  }

  @Test
  public void headerAndFooterMoveFirstInBodyAsRunningElements() {
    Document doc = transform();
    List<String> firstThree = new ArrayList<String>();
    for (Element child : doc.body().children().subList(0, 3)) {
      firstThree.add(child.id());
    }
    assertTrue(firstThree.toString(),
      firstThree.containsAll(Arrays.asList("pageHeader", "pageFooter", "pageFooter-1")));
    assertEquals(3, doc.select("#pageHeader, #pageFooter, #pageFooter-1").size());
  }

  @Test
  public void runningIsAppendedAndTheOriginalInlineStyleIsKept() {
    Document doc = transform();
    String header = doc.getElementById("pageHeader").attr("style");
    assertTrue(header, header.startsWith("color: red"));
    assertTrue(header, header.contains("position: running("));
    String first = doc.getElementById("pageFooter-1").attr("style");
    assertTrue(first, first.startsWith("height: 5px"));
    assertTrue(first, first.contains("position: running("));
    assertTrue(doc.getElementById("pageFooter").attr("style").contains("position: running("));
  }

  @Test
  public void templatePageAndFontFaceRulesAreRemovedAndOtherRulesKept() {
    Document doc = transform();
    String original = doc.head().select("style").first().data();
    assertFalse(original, original.contains("@page"));
    assertFalse(original, original.contains("@font-face"));
    assertFalse(original, original.contains("margin: 10mm"));
    assertFalse(original, original.contains("}}"));
    assertTrue(original, original.contains("p { color: red; }"));
  }

  @Test
  public void generatedPageMarginsEqualTheGivenHeights() {
    String css = generatedCss(transform());
    assertTrue(css, css.contains("size: A4 portrait"));
    assertTrue(css, css.contains("margin: 40mm 0 30mm 0"));
    Document landscape = HtmlPdfCompatibility.transform(HTML, "12px", "1.5cm", "landscape");
    String landscapeCss = generatedCss(landscape);
    assertTrue(landscapeCss, landscapeCss.contains("size: A4 landscape"));
    assertTrue(landscapeCss, landscapeCss.contains("margin: 12px 0 1.5cm 0"));
  }

  @Test
  public void pageFirstUsesTheFirstPageVariantAndMarginBoxesSpanThePage() {
    Document doc = transform();
    String css = generatedCss(doc);
    String firstName = runningName(doc.getElementById("pageFooter-1"));
    String defaultName = runningName(doc.getElementById("pageFooter"));
    int pageFirst = css.indexOf("@page :first");
    assertTrue(css, pageFirst >= 0);
    String firstBlock = css.substring(pageFirst);
    assertTrue(firstBlock, firstBlock.contains("element(" + firstName + ")"));
    assertFalse(firstBlock, firstBlock.contains("element(" + defaultName + ")"));
    String defaultBlock = css.substring(css.indexOf("@page {"), pageFirst);
    assertTrue(defaultBlock, defaultBlock.contains("element(" + defaultName + ")"));
    assertTrue(css, css.contains("@top-center"));
    assertTrue(css, css.contains("@bottom-center"));
    assertTrue(css, css.contains("width: 100%"));
    assertTrue(css, css.contains("vertical-align: top"));
  }

  @Test
  public void whitespaceBesideBrIsRemovedExceptInsidePre() {
    Document doc = transform();
    for (Element br : doc.body().select("br")) {
      if (br.parent().tagName().equals("pre")) {
        continue;
      }
      Node prev = br.previousSibling();
      Node next = br.nextSibling();
      assertFalse("blank text before <br>", prev instanceof TextNode && ((TextNode) prev).isBlank());
      assertFalse("blank text after <br>", next instanceof TextNode && ((TextNode) next).isBlank());
    }
    int blankInPre = 0;
    for (Node child : doc.select("pre").first().childNodes()) {
      if (child instanceof TextNode && ((TextNode) child).isBlank()) {
        blankInPre++;
      }
    }
    assertEquals(1, blankInPre);
  }

  @Test
  public void compatStylesheetIsAppendedLastInHead() {
    Document doc = transform();
    Element last = doc.head().children().last();
    assertEquals("style", last.tagName());
    assertTrue(last.data(), last.data().contains("span.section, span.content"));
    assertTrue(last.data(), last.data().contains(".img-background"));
  }

  @Test
  public void resultConvertsToAW3cDocument() {
    org.w3c.dom.Document dom = HtmlPdfCompatibility.toW3cDocument(HTML, "40mm", "30mm", "portrait");
    assertNotNull(dom);
    assertEquals("html", dom.getDocumentElement().getNodeName());
    NodeList divs = dom.getElementsByTagName("div");
    assertEquals(3, divs.getLength());
    String firstId = ((org.w3c.dom.Element) divs.item(0)).getAttribute("id");
    assertTrue(firstId, Arrays.asList("pageHeader", "pageFooter", "pageFooter-1").contains(firstId));
  }

  @Test
  public void anInvalidHeightFallsBackToZeroInsteadOfReachingTheStylesheet() {
    Document doc = HtmlPdfCompatibility.transform(HTML, "1px; } body { display:none", null, "portrait");
    String css = generatedCss(doc);
    assertFalse(css, css.contains("display:none"));
    assertTrue(css, css.contains("margin: 0 0 0 0"));
  }
}
