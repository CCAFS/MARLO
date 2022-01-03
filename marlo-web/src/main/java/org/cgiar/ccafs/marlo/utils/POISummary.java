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

import org.cgiar.ccafs.marlo.data.manager.UrlSynthesisLogManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TOC;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POISummary {

  // LOG
  private static final Logger LOG = LoggerFactory.getLogger(POISummary.class);
  private static String FONT_TYPE = "Calibri Light";
  private final static String TITLE_FONT_COLOR = "3366CC";
  private final static String TEXT_FONT_COLOR = "000000";
  private static Integer TABLE_TEXT_FONT_SIZE = 10;
  private static String TABLE_HEADER_FONT_COLOR = "FFF2CC";
  private int count = 0;

  List<String> expressionsList = new ArrayList<String>();
  List<String> expressionsListClose = new ArrayList<String>();
  private ReportSynthesis reportSynthesis;
  private Phase phase;

  // Manager
  @Inject
  UrlSynthesisLogManager urlSynthesisLogManager;


  private void addExpressionsToList() {
    expressionsList.add("<b>");
    expressionsList.add("<strong>");
    expressionsList.add("<i>");
    expressionsList.add("<em>");
    expressionsList.add("<u>");
    expressionsList.add("<strike>");
    expressionsList.add("<s>");
    expressionsList.add("<del>");
    expressionsList.add("<p>");
    expressionsList.add("<a");

    expressionsListClose.add("</b>");
    expressionsListClose.add("</strong>");
    expressionsListClose.add("</i>");
    expressionsListClose.add("</em>");
    expressionsListClose.add("</u>");
    expressionsListClose.add("</strike>");
    expressionsListClose.add("</s>");
    expressionsListClose.add("</del>");
    expressionsListClose.add("</p>");
    expressionsListClose.add("</a>");
  }

  public void addLineSeparator(XWPFParagraph h1) {
    XWPFRun h1Run = h1.createRun();
    h1.setBorderBottom(Borders.SINGLE);
  }

  private void addParagraphTextBreak(XWPFRun paragraphRun, String text) {
    try {
      if (text.contains("\n")) {
        String[] lines = text.split("\n");
        paragraphRun.setText(lines[0], 0); // set first line into XWPFRun
        for (int i = 1; i < lines.length; i++) {
          // add break and insert new text
          paragraphRun.addBreak();
          paragraphRun.setText(lines[i]);
        }
      } else {
        paragraphRun.setText(text, 0);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void addParagraphTextBreakPOW2019(XWPFRun paragraphRun, String text) {
    if (text.contains("\n")) {
      String[] lines = text.split("\n");
      try {
        paragraphRun.setText(lines[0], 0); // set first line into XWPFRun
      } catch (Exception e) {

      }
      for (int i = 1; i < lines.length; i++) {
        // add break and insert new text
        paragraphRun.addCarriageReturn();
        paragraphRun.setText(lines[i]);
        paragraphRun.setFontFamily(FONT_TYPE);

      }
    } else {
      paragraphRun.setText(text, 0);
    }
  }

  private void addParagraphTextBreakPOW2019B(XWPFRun paragraphRun, String text) {
    if (text.contains("\n")) {
      String[] lines = text.split("\n");
      try {
        paragraphRun.setText(lines[0], 0); // set first line into XWPFRun
      } catch (Exception e) {

      }
      for (int i = 1; i < lines.length; i++) {
        // add break and insert new text
        paragraphRun.addCarriageReturn();
        paragraphRun.setText(lines[i]);
      }
    } else {
      paragraphRun.setText(text, 0);
    }
  }


  public void convertHTMLTags(XWPFDocument document, String text, XWPFTableCell cell) {
    if (text != null && text.isEmpty()) {
      text = text.trim();
    }

    List<Integer> startsPosList = new ArrayList<Integer>();
    List<Integer> finalPosList = new ArrayList<Integer>();
    List<String> tagsAddList = new ArrayList<String>();
    List<String> urlList = new ArrayList<String>();
    List<String> referenceList = new ArrayList<String>();

    text = text.replace("</span>", " ");
    text = text.replace("<span>", " ");
    text = text.replace("<p>", "");
    text = text.replace("<strong>", "");
    text = text.replace("</strong>", "");
    text = text.replace("<em>", "");
    text = text.replace("</em>", "");
    text = text.replaceAll("<td>", "");
    text = text.replaceAll("<tr>", "");
    text = text.replaceAll("</td>", " ");
    text = text.replaceAll("<th>", "");
    text = text.replaceAll("</th>", " | ");
    text = text.replaceAll("</table>", "\n");
    text = text.replaceAll("<thead>", "");
    text = text.replaceAll("</thead>", "");
    text = text.replaceAll("<tbody>", "");
    text = text.replaceAll("</tbody>", "");
    text = text.replaceAll("</br>", "");
    text = text.replaceAll("   ", "");
    text = text.replaceAll("<a></a>", "");
    text = text.replaceAll("&nbsp;", " ");
    text = text.replaceAll("<font>", " ");
    text = text.replaceAll("</font>", " ");
    text = text.replaceAll("<span style=\"color: rgb(130, 130, 130); font-size: 0.98em;\">", "");
    text = text.replaceAll("<span style=\"color: rgb(130, 130, 130); font-size: 0.98em;", "");
    text = text.replaceAll("<span style=\"color: rgb(130, 130, 130); font-size: 0.98em;\">", "");
    text = text.replaceAll("style=\"font-size: 0.98em; background-color: rgb(255, 255, 255)", "");
    text = text.replaceAll("style=\"font-size: 0.98em; background-color: rgb(255, 255, 255);", "");
    text = text.replaceAll("</span>", "");
    text = text.replaceAll("title=\"\"", "");
    text = text.replaceAll("style=\"font-size: 0.98em;\"", "");
    text = text.replaceAll(" style=\"font-size: 0.98em;\"", "");
    text = text.replaceAll("\" \">", "");

    /*
     * recognize the tag as a line break
     */
    text = text.replaceAll("\n", "");
    text = text.replaceAll("<table>", "\n");
    text = text.replaceAll("</p>", " \n");
    text = text.replaceAll("</tr>", "\n");
    text = text.replaceAll("<br>", "\n");
    // text = text.replaceAll("\r", " ");

    int textLength = 0;
    this.addExpressionsToList();
    String url = "";
    int posFinal = 0, hrefTagsCount = 0;
    String startText = text;
    textLength = startText.length();
    String expressionListActual = "", textIndicatorLink = "";

    /*
     * runs through expressions list and checks if it is included in the text,
     * if it occurs, the text is scanned and the position of the label is detected
     */
    for (int i = 0; i < expressionsList.size(); i++) {
      expressionListActual = expressionsList.get(i);

      if (text.contains(expressionListActual)) {

        for (int j = 0; j < text.length(); j++) {
          /*
           * Getting open tags
           */
          if ((text.charAt(j) == expressionListActual.charAt(0))
            && text.charAt(j + 1) == expressionListActual.charAt(1)) {

            startsPosList.add(j); // pos init expression
            tagsAddList.add(expressionsList.get(i)); // expression list

            /*
             * Getting close tag
             */
            posFinal = text.indexOf(expressionsListClose.get(i), j); // pos final expression
            finalPosList.add(posFinal);

            /*
             * Detect start of a href tags
             */
            if (expressionListActual.equals("<a")) {

              hrefTagsCount++;
              try {
                textIndicatorLink = text.substring(text.indexOf(">", j) + 1, posFinal);
                if (textIndicatorLink == null || textIndicatorLink.isEmpty()) {
                  textIndicatorLink = "link";
                }
              } catch (Exception e) {
                textIndicatorLink = "link";
              }
              try {
                url = text.substring(text.indexOf("=", j) + 2, text.indexOf(">", j) - 1);

                if (!url.contains("http://") && !url.contains("https://")) {
                  url = "http://" + url;
                }
              } catch (Exception e) {
                url = "()";
              }
              urlList.add(url);
              referenceList.add(textIndicatorLink);
            }
          }
        }
      }
    }

    String expression = "";
    int startPosition = 0;
    int finalPosition = 0;
    int posText = 0;
    int posStart = 0;

    String stringTemp = "";
    XWPFRun paragraphRun;
    XWPFParagraph paragraph = null;

    if (cell != null) {
      paragraph = cell.addParagraph();
    }

    String url1 = "";
    String textIndicatorLink1 = "";
    int k = 0;

    for (posText = 0; posText < text.length(); posText++) {

      if (startsPosList.contains(posText)) {
        for (posStart = 0; posStart < startsPosList.size(); posStart++) {
          if (startsPosList.get(posStart) == posText) {
            finalPosition = finalPosList.get(posStart);
            expression = tagsAddList.get(posStart);
          }
        }

        if (posText > 0) {
          stringTemp = text.substring(startPosition, posText);
          stringTemp = stringTemp.replaceAll(">", "");

          try {
            paragraph.setAlignment(ParagraphAlignment.BOTH);
            paragraphRun = paragraph.createRun();
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setText(stringTemp);
          } catch (Exception e) {
            if (cell != null) {
              paragraph = null;
              paragraph = cell.addParagraph();
              paragraph.setAlignment(ParagraphAlignment.BOTH);
              paragraphRun = paragraph.createRun();
            } else {
              paragraph = document.createParagraph();
              paragraph.setAlignment(ParagraphAlignment.BOTH);
              paragraphRun = paragraph.createRun();
            }
          }
          /*
           * paragraph.setAlignment(ParagraphAlignment.BOTH);
           * paragraphRun = paragraph.createRun();
           * paragraphRun.setFontFamily(FONT_TYPE);
           */
          stringTemp = this.replaceHTMLTags(stringTemp);
          if (stringTemp != null) {
            this.addParagraphTextBreakPOW2019(paragraphRun, stringTemp);
          }

          paragraphRun.setColor(TEXT_FONT_COLOR);
          paragraphRun.setFontFamily(FONT_TYPE);
          paragraphRun.setFontSize(11);
          paragraphRun.setBold(false);
          paragraphRun.setUnderline(UnderlinePatterns.NONE);
          paragraphRun.setItalic(false);
        }

        startPosition = posText + expression.length() + 1;
        /*
         * Create paragraph with last position after the start of html tag until the close of this
         */
        if (finalPosition + expression.length() <= text.length()) {
          stringTemp = text.substring(startPosition, finalPosition + expression.length());
        } else {
          stringTemp = text;
        }

        try {
          paragraph.setAlignment(ParagraphAlignment.BOTH);
          paragraphRun = paragraph.createRun();
          paragraphRun.setFontFamily(FONT_TYPE);
        } catch (Exception e) {
          if (cell != null) {
            paragraph = null;
            paragraph = cell.addParagraph();
            paragraph.setAlignment(ParagraphAlignment.BOTH);
            paragraphRun = paragraph.createRun();
          } else {
            paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.BOTH);
            paragraphRun = paragraph.createRun();
          }
        }

        if (expressionListActual.contains("<a") == false) {
          paragraph.setAlignment(ParagraphAlignment.BOTH);
          stringTemp = this.replaceHTMLTags(stringTemp);

          this.addParagraphTextBreakPOW2019(paragraphRun, stringTemp);
          paragraphRun.setColor(TEXT_FONT_COLOR);
          paragraphRun.setFontFamily(FONT_TYPE);
          paragraphRun.setFontSize(11);
        }

        /*
         * Apply the style to the paragraph depending on the identified expression
         */
        switch (expression) {
          /*
           * Open tags detection
           */
          case "<b>":
            paragraphRun.setBold(true);
            break;
          case "<strong>":
            paragraphRun.setBold(true);
            break;
          case "<i>":
            paragraphRun.setItalic(true);
            break;
          case "<em>":
            break;
          case "<u>":
            paragraphRun.setUnderline(UnderlinePatterns.SINGLE);
            break;
          case "<strike>":
            break;
          case "<del>":
            break;
          case "<p>":
            break;
          case "<a":

            for (posStart = 0; posStart < startsPosList.size(); posStart++) {
              if (startsPosList.get(posStart) == posText) {

                url1 = urlList.get(k);
                textIndicatorLink1 = referenceList.get(k);
                if (k <= hrefTagsCount) {
                  k++;
                }
                break;
              }
            }
            try {
              this.textHyperlink(url1, textIndicatorLink1, paragraph);
            } catch (Exception e) {
              try {
                paragraph.setAlignment(ParagraphAlignment.BOTH);
                paragraphRun = paragraph.createRun();
                paragraphRun.setColor("FC0000");
                paragraphRun.setFontFamily(FONT_TYPE);
                paragraphRun.setText(url1 + " (" + textIndicatorLink1 + ")");
                /*
                 * UrlSynthesisLog urlSynthesisLog = new UrlSynthesisLog();
                 * urlSynthesisLog.setErrorText(url1);
                 * urlSynthesisLog.setErrorText(url1);
                 * urlSynthesisLogManager.saveUrlSynthesisLog(urlSynthesisLog);
                 */

              } catch (Exception x) {
                if (cell != null) {
                  paragraph = null;
                  paragraph = cell.addParagraph();
                  paragraph.setAlignment(ParagraphAlignment.BOTH);
                  paragraphRun = paragraph.createRun();

                } else {
                  paragraph = document.createParagraph();
                  paragraph.setAlignment(ParagraphAlignment.BOTH);
                  paragraphRun = paragraph.createRun();

                }
              }
            }
            break;

          /*
           * Close tags detection
           */
          case "</b>":
            paragraphRun.setBold(false);
            break;
          case "</strong>":
            paragraphRun.setBold(false);
            break;
          case "</i>":
            paragraphRun.setItalic(false);
            break;
          case "</em>":
            break;
          case "</u>":
            paragraphRun.setUnderline(UnderlinePatterns.NONE);
            break;
          case "</strike>":
            break;
          case "</del>":
            break;
          case "</p>":
            break;
          case "</a>":
            break;
          default:
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setBold(false);
            paragraphRun.setUnderline(UnderlinePatterns.NONE);
            paragraphRun.setItalic(false);
        }
        startPosition = finalPosition + expression.length() + 1;
        expression = "";
        posText = finalPosition;
      }
    }

    if (finalPosition < textLength)

    {
      int length = startText.length();

      startText = startText.substring(finalPosition + expression.length(), length);

      try {
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        paragraphRun = paragraph.createRun();
        paragraphRun.setFontFamily(FONT_TYPE);
      } catch (Exception e) {
        if (cell != null) {
          paragraph = null;
          paragraph = cell.addParagraph();
          paragraph.setAlignment(ParagraphAlignment.BOTH);
          paragraphRun = paragraph.createRun();
        } else {
          paragraph = document.createParagraph();
          paragraph.setAlignment(ParagraphAlignment.BOTH);
          paragraphRun = paragraph.createRun();
        }
      }

      if (startText != null && !startText.isEmpty() && startText != "" && finalPosition != 0) {
        startText = this.replaceHTMLTags(" " + startText);
      } else {
        startText = this.replaceHTMLTags("" + startText);
      }

      startText = this.replaceHTMLTags(startText);
      startText = startText.replaceAll("&nbsp;", " ");
      startText = startText.replaceAll(">", "");
      this.addParagraphTextBreakPOW2019(paragraphRun, startText);

      paragraphRun.setColor(TEXT_FONT_COLOR);
      paragraphRun.setFontFamily(FONT_TYPE);
      paragraphRun.setFontSize(11);
      paragraphRun.setBold(false);
      paragraphRun.setUnderline(UnderlinePatterns.NONE);
      paragraphRun.setItalic(false);
    }
  }

  public void createTOC(XWPFDocument document) {
    // Create table of contents
    CTSdtBlock block = document.getDocument().getBody().addNewSdt();
    TOC toc = new TOC(block);
    for (XWPFParagraph par : document.getParagraphs()) {
      String parStyle = par.getStyle();
      if ((parStyle != null) && (parStyle.startsWith("Narrative "))) {
        try {
          int level = Integer.valueOf(parStyle.substring("Narrative".length())).intValue();
          toc.addRow(level, par.getText(), 1, "112723803");
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Phase getPhase() {
    return phase;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public void pageCenterBoldHeader(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpHeader = CTP.Factory.newInstance();
    CTR ctrHeader = ctpHeader.addNewR();
    CTText ctHeader = ctrHeader.addNewT();
    ctHeader.setStringValue(text);
    XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
    headerParagraph.setAlignment(ParagraphAlignment.LEFT);
    XWPFParagraph[] parsHeader = new XWPFParagraph[1];
    parsHeader[0] = headerParagraph;
    policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
  }

  /**
   * Footer title
   * 
   * @param document
   * @param text
   * @throws IOException
   */
  public void pageFooter(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpFooter = CTP.Factory.newInstance();
    CTR ctrFooter = ctpFooter.addNewR();
    CTText ctFooter = ctrFooter.addNewT();
    ctFooter.setStringValue(text);
    XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
    footerParagraph.setAlignment(ParagraphAlignment.LEFT);
    XWPFParagraph[] parsFooter = new XWPFParagraph[1];
    parsFooter[0] = footerParagraph;
    policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);
  }


  /**
   * Header title
   * 
   * @param document
   * @param text
   * @throws IOException
   */
  public void pageHeader(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpHeader = CTP.Factory.newInstance();
    CTR ctrHeader = ctpHeader.addNewR();
    CTText ctHeader = ctrHeader.addNewT();
    ctHeader.setStringValue(text);
    XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
    headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
    XWPFParagraph[] parsHeader = new XWPFParagraph[1];
    parsHeader[0] = headerParagraph;
    policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
  }

  /**
   * Header title
   * 
   * @param document
   * @param text
   * @throws IOException
   */
  public void pageHeaderCenter(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpHeader = CTP.Factory.newInstance();
    CTR ctrHeader = ctpHeader.addNewR();
    CTText ctHeader = ctrHeader.addNewT();
    ctHeader.setStringValue(text);
    XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
    headerParagraph.setAlignment(ParagraphAlignment.CENTER);
    XWPFParagraph[] parsHeader = new XWPFParagraph[1];
    parsHeader[0] = headerParagraph;
    policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
  }

  public void pageLeftHeader(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpHeader = CTP.Factory.newInstance();
    CTR ctrHeader = ctpHeader.addNewR();
    CTText ctHeader = ctrHeader.addNewT();
    ctHeader.setStringValue(text);
    XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
    headerParagraph.setAlignment(ParagraphAlignment.LEFT);
    XWPFParagraph[] parsHeader = new XWPFParagraph[1];
    parsHeader[0] = headerParagraph;
    policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
  }

  public void pageRightHeader(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpHeader = CTP.Factory.newInstance();
    CTR ctrHeader = ctpHeader.addNewR();
    CTText ctHeader = ctrHeader.addNewT();
    CTR ctr = CTR.Factory.newInstance();
    ctr.addNewRPr().addNewU().setVal(STUnderline.SINGLE);
    ctHeader.setStringValue(text);
    XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
    headerParagraph.setAlignment(ParagraphAlignment.RIGHT);

    XWPFParagraph[] parsHeader = new XWPFParagraph[1];
    parsHeader[0] = headerParagraph;
    policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
  }

  public String replaceHTMLTags(String html) {
    try {
      html = html.replaceAll("\\<.*?>", "");
      html = html.replaceAll("&nbsp;", " ");
      html = html.replaceAll("&amp;", "");
      html = html.replaceAll("&gt;", "");
      html = html.replaceAll("&lt;", "");
      html = html.replaceAll("<span style=\"font-size: 1em;\"", "");
      html = html.replaceAll("</span", "");
      html = html.replaceAll("<br", "");
      html = html.replaceAll("<td", "");
      html = html.replaceAll("<tr", "");
      html = html.replaceAll("</td", "");
      html = html.replaceAll("</tr", "");
      html = html.replaceAll("<th", "");
      html = html.replaceAll("</th", "");
      html = html.replaceAll("<table", "");
      html = html.replaceAll("</table", "");
      html = html.replaceAll("<thead", "");
      html = html.replaceAll("</thead", "");
      html = html.replaceAll("<tbody", "");
      html = html.replaceAll("</tbody", "");
      html = html.replaceAll(">", "");
      html = html.replaceAll(">", "");
      html = html.replaceAll("   ", "");

    } catch (Exception e) {
      throw e;
    }
    return html;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void table10AnnualReport2018Style(XWPFTable table) {
    /* horizontal merge, From format tables I */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }

  public void table11AnnualReport2018Style(XWPFTable table) {
    /* Horizontal merge, From format tables D1 Annual report */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 4; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void table13AnnualReportStyle(XWPFTable table, boolean isAr2021) {
    /* Horizontal merge, From format tables 13 */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();

    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell; y++) {
      XWPFTableCell cell = row.getCell(y);

      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }

      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y <= numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    if (isAr2021 == false) {
      for (int x = 0; x < table.getNumberOfRows(); x++) {
        if (x > 1) {
          XWPFTableRow rowCom = table.getRow(x);
          XWPFTableCell cell = rowCom.getCell(10);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }

          cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(5000));

        }
      }
    }
  }

  public void table1AnnualReport2020Style(XWPFTable table) {
    /* Horizontal merge, From format tables B */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 1; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void table2AnnualReportCRPStyle(XWPFTable table) {
    /* Horizontal merge, From format table A */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();

    /* Vertical merge, From format table A */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell - 1; y++) {
      XWPFTableCell cell = row.getCell(y);
      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }
      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y < numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x >= 0 && x < 3) {
        XWPFTableRow row1 = table.getRow(x);
        for (int y = 0; y <= 7; y++) {
          XWPFTableCell cell = row1.getCell(y);
          if (cell != null) {
            if (cell.getCTTc() == null) {
              ((CTTc) cell).addNewTcPr();
            }

            if (cell.getCTTc().getTcPr() == null) {
              cell.getCTTc().addNewTcPr();
            }
            if (x == 2 && !(cell.getText().trim().length() > 0)) {
              break;
            }
            if (cell.getText().trim().length() > 0) {
              if (y == 0) {
                cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
              }
              vmerge.setVal(STMerge.RESTART);
              cell.getCTTc().getTcPr().setVMerge(vmerge);
            } else {
              if (y == 0) {
                cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
              }
              vmerge1.setVal(STMerge.CONTINUE);
              cell.getCTTc().getTcPr().setVMerge(vmerge1);
            }
          }
        }

      }
    }

  }


  public void table3AnnualReport2018Style(XWPFTable table) {
    /* horizontal merge, From format tables I */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }

  public void table4AnnualReport2018Style(XWPFTable table) {
    /* horizontal merge, From format tables I */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }


  public void table5AnnualReport2018Style(XWPFTable table) {
    /* Horizontal merge, From format tables D1 Annual report */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 6; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              // cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              // cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void table6Annual2018ReportStyle(XWPFTable table) {
    /* horizontal merge, From format tables A1 */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }

  public void tableA1Annual2018ReportStyle(XWPFTable table) {
    /* horizontal merge, From format tables A1 */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }


  public void tableA1AnnualReportStyle(XWPFTable table) {
    /* horizontal merge, From format tables A1 */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }

  public void tableA2PowbStyle(XWPFTable table) {
    /* Horizontal merge, From format tables A */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 11; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void tableAnnexesAnnualReportStyle(XWPFTable table) {
    /* horizontal merge, From format tables A1 */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }

  public void tableAPowbCRPStyle(XWPFTable table) {
    /* Horizontal merge, From format table A */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();

    /* Vertical merge, From format table A */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell - 1; y++) {
      XWPFTableCell cell = row.getCell(y);
      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }
      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y < numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x >= 0) {
        XWPFTableRow row1 = table.getRow(x);
        for (int y = 0; y <= 8; y++) {
          XWPFTableCell cell = row1.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 2 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }

  }

  public void tableAPowbStyle(XWPFTable table) {
    /* Horizontal merge, From format tables A */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();

    /* Vertical merge, From format tables A */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell; y++) {
      XWPFTableCell cell = row.getCell(y);
      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }
      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y < numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x >= 0) {
        XWPFTableRow row1 = table.getRow(x);
        for (int y = 0; y <= 6; y++) {
          XWPFTableCell cell = row1.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 2 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }
      }
    }
  }

  public void tableAStyle(XWPFTable table) {
    /* Horizontal merge, From format tables A */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 2; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void tableBAnnualReportStyle(XWPFTable table) {
    /* Horizontal merge, From format tables B */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 2; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void tableC2PowbStyle(XWPFTable table) {
    /* Vertical merge, From format tables C */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        XWPFTableCell cell = row.getCell(row.getTableCells().size() - 1);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }
        if (x == 1 && !(cell.getText().trim().length() > 0)) {
          break;
        }
        if (cell.getText().trim().length() > 0) {
          vmerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setVMerge(vmerge);
        } else {
          vmerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setVMerge(vmerge1);
        }
      }
    }
  }

  public void tableCStyle(XWPFTable table) {
    /* Vertical merge, From format tables C */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        XWPFTableCell cell = row.getCell(row.getTableCells().size() - 1);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }
        if (x == 1 && !(cell.getText().trim().length() > 0)) {
          break;
        }
        if (cell.getText().trim().length() > 0) {
          vmerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setVMerge(vmerge);
        } else {
          vmerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setVMerge(vmerge1);
        }
      }
    }
  }

  public void tableD1AnnualReportStyle(XWPFTable table) {
    /* Horizontal merge, From format tables D1 Annual report */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 2; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void tableEPowbStyle(XWPFTable table) {
    /* Horizontal merge, From format tables E */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();

    /* Vertical merge, From format tables E */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell - 1; y++) {
      XWPFTableCell cell = row.getCell(y);
      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }
      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y < numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    for (int x = 0; x < 3; x++) {
      if (x >= 0) {
        XWPFTableRow row1 = table.getRow(x);
        for (int y = 0; y < 6; y++) {
          XWPFTableCell cell = row1.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 2 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }

    /*
     * for (int x = 0; x < table.getNumberOfRows(); x++) {
     * if (x > 1) {
     * XWPFTableRow rowCom = table.getRow(x);
     * XWPFTableCell cell = rowCom.getCell(6);
     * if (cell.getCTTc() == null) {
     * ((CTTc) cell).addNewTcPr();
     * }
     * if (cell.getCTTc().getTcPr() == null) {
     * cell.getCTTc().addNewTcPr();
     * }
     * cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(5000));
     * }
     * }
     */
  }

  public void tableEStyle(XWPFTable table) {
    /* Horizontal merge, From format tables E */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell - 1; y++) {
      XWPFTableCell cell = row.getCell(y);

      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }

      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y < numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }


    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 1) {
        XWPFTableRow rowCom = table.getRow(x);
        XWPFTableCell cell = rowCom.getCell(6);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }

        cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(5000));

      }
    }
  }

  public void tableFStyle(XWPFTable table) {
    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        XWPFTableCell cell = row.getCell(2);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }

        cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(7000));

      }
    }
  }

  public void tableGStyle(XWPFTable table) {
    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }

        cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(5000));
      }
    }
  }

  public void tableIAnnualReportStyle(XWPFTable table) {
    /* horizontal merge, From format tables I */

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();

        CTTcPr pr = cell.getCTTc().addNewTcPr();
        // pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(100));
      }
    }
  }

  public void tableJAnnualReportStyle(XWPFTable table) {
    /* Horizontal merge, From format tables J */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell; y++) {
      XWPFTableCell cell = row.getCell(y);

      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }

      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y <= numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 1) {
        XWPFTableRow rowCom = table.getRow(x);
        XWPFTableCell cell = rowCom.getCell(6);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }

        cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(5000));

      }
    }
  }

  public void tableMergeAnnualReportCRPStyle(XWPFTable table) {
    /* Horizontal merge, From format table A */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();

    /* Vertical merge, From format table A */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell - 1; y++) {
      XWPFTableCell cell = row.getCell(y);
      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }
      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y < numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 1) {
        XWPFTableRow row1 = table.getRow(x);
        for (int y = 0; y <= 7; y++) {
          XWPFTableCell cell = row1.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 2 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }

  }

  public void test(XWPFDocument document, String text) {
    int posInit = 0;
    int posFinal = 0;

    for (int i = 0; i < expressionsList.size(); i++) {
      posInit = text.indexOf(expressionsList.get(i).concat(expressionsList.get(i + 1)));

    }
  }

  public void test2(XWPFParagraph paragraph, XWPFRun paragraphRun, String textTag, String expression) {
  }

  /**
   * Head 1 Title
   * 
   * @param h1
   * @param text
   */
  public void textHead1Title(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor(TITLE_FONT_COLOR);
    h1Run.setBold(true);
    h1Run.setFontFamily(FONT_TYPE);
    h1Run.setFontSize(16);
  }

  public void textHead1TitleBlack(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor("000000");
    h1Run.setBold(true);
    h1Run.setFontFamily("Calibri");
    h1Run.setFontSize(13);
  }

  public void textHead1TitleFontCalibri(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor(TITLE_FONT_COLOR);
    h1Run.setBold(true);
    h1Run.setFontFamily("Calibri");
    h1Run.setFontSize(14);
  }

  public void textHead1TitleLightBlue(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor("5B9BD5");
    h1Run.setBold(true);
    h1Run.setFontFamily("Calibri");
    h1Run.setFontSize(13);
  }

  public void textHead2Title(XWPFParagraph h2, String text) {
    h2.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h2Run = h2.createRun();
    this.addParagraphTextBreak(h2Run, text);
    h2Run.setColor(TITLE_FONT_COLOR);
    h2Run.setBold(true);
    h2Run.setFontFamily(FONT_TYPE);
    h2Run.setFontSize(14);
  }

  public void textHead3Title(XWPFParagraph h2, String text) {
    h2.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h2Run = h2.createRun();
    this.addParagraphTextBreak(h2Run, text);
    h2Run.setColor(TITLE_FONT_COLOR);
    h2Run.setBold(true);
    h2Run.setFontFamily(FONT_TYPE);
    h2Run.setFontSize(12);
  }

  public void textHeadCoverTitle(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.CENTER);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor(TEXT_FONT_COLOR);
    h1Run.setBold(false);
    h1Run.setFontFamily(FONT_TYPE);
    h1Run.setFontSize(26);
  }

  public void textHeadCoverTitleAR2018(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.CENTER);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor(TEXT_FONT_COLOR);
    h1Run.setBold(true);
    h1Run.setFontFamily(FONT_TYPE);
    h1Run.setFontSize(28);
  }

  public void textHeadPrincipalTitle(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.LEFT);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor("323E4F");
    h1Run.setBold(false);
    h1Run.setFontFamily("Calibri");
    h1Run.setFontSize(26);
    h1.setBorderBottom(Borders.SINGLE);
  }


  public void textHeadPrincipalTitlefirtsPageCRP(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.CENTER);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor("323E4F");
    h1Run.setBold(false);
    h1Run.setFontFamily("Calibri");
    h1Run.setFontSize(26);
  }

  public void textHyperlink(String url, String text, XWPFParagraph paragraph) {

    // Add the link as External relationship
    String id = paragraph.getDocument().getPackagePart()
      .addExternalRelationship(url, XWPFRelation.HYPERLINK.getRelation()).getId();

    // Append the link and bind it to the relationship
    CTHyperlink cLink = paragraph.getCTP().addNewHyperlink();
    cLink.setId(id);

    // // Create the linked text
    CTText ctText = CTText.Factory.newInstance();
    ctText.setStringValue(text);

    CTR ctr = CTR.Factory.newInstance();
    ctr.setTArray(new CTText[] {ctText});
    ctr.addNewRPr().addNewColor().setVal("0000FF");
    ctr.addNewRPr().addNewU().setVal(STUnderline.SINGLE);
    ctr.addNewRPr().addNewRFonts().setAscii(FONT_TYPE);
    // Insert the linked text into the link
    cLink.setRArray(new CTR[] {ctr});

  }

  public void textLineBreak(XWPFDocument document, int breakNumber) {
    for (int i = 0; i < breakNumber; i++) {
      document.createParagraph();
    }
  }

  public void textNotes(XWPFParagraph paragraph, String text) {
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun paragraphRun = paragraph.createRun();
    this.addParagraphTextBreak(paragraphRun, text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily(FONT_TYPE);
    paragraphRun.setFontSize(10);
  }

  public void textParagraph(XWPFParagraph paragraph, String text) {
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun paragraphRun = paragraph.createRun();

    this.addParagraphTextBreak(paragraphRun, text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily(FONT_TYPE);
    paragraphRun.setFontSize(11);
  }

  public void textParagraphBold(XWPFParagraph paragraph, String text) {
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun paragraphRun = paragraph.createRun();

    this.addParagraphTextBreak(paragraphRun, text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(true);
    paragraphRun.setFontFamily(FONT_TYPE);
    paragraphRun.setFontSize(11);
  }

  public void textParagraphFontCalibri(XWPFParagraph paragraph, String text) {
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun paragraphRun = paragraph.createRun();
    this.addParagraphTextBreak(paragraphRun, text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily("Calibri");
    paragraphRun.setFontSize(11);
  }

  public void textParagraphItalicLightBlue(XWPFParagraph paragraph, String text) {
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun paragraphRun = paragraph.createRun();
    this.addParagraphTextBreak(paragraphRun, text);
    paragraphRun.setColor("5B9BD5");
    paragraphRun.setBold(false);
    paragraphRun.setItalic(true);
    paragraphRun.setFontFamily("Calibri");
    paragraphRun.setFontSize(12);
  }

  public void textTable(XWPFDocument document, List<List<POIField>> sHeaders, List<List<POIField>> sData,
    Boolean highlightFirstColumn, String tableType) {
    if (tableType.contains("Powb")) {
      TABLE_TEXT_FONT_SIZE = 11;
      FONT_TYPE = "Calibri";
    } else {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableC2PowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableC2PowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableEPowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableEPowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableA2PowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableA2PowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableBPowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableBPowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    XWPFTable table = document.createTable();
    int record = 0;
    int headerIndex = 0;
    for (List<POIField> poiParameters : sHeaders) {

      // Setting the Header
      XWPFTableRow tableRowHeader;
      if (headerIndex == 0) {
        tableRowHeader = table.getRow(0);
      } else {
        tableRowHeader = table.createRow();
      }

      for (POIField poiParameter : poiParameters) {

        // Condition for table b cell color in fields 5 and 6 in annual report
        if (tableType.equals("tableBAnnualReport") && (record == 4 || record == 5)) {
          TABLE_HEADER_FONT_COLOR = "DEEAF6";
          // Condition for table 2a
        } else if (tableType.contains("Powb")) {
          TABLE_HEADER_FONT_COLOR = "FFF2CC";
        } else if (tableType.contains("table2AnnualReport2018")) {
          if (record == 0) {
            TABLE_HEADER_FONT_COLOR = "D9E2F3";

          } else {
            TABLE_HEADER_FONT_COLOR = "FFFFFF";
          }
        } else if (tableType.contains("table6AnnualReport2018")) {
          TABLE_HEADER_FONT_COLOR = "E2EFD9";

        } else if (tableType.contains("Report2018")) {
          TABLE_HEADER_FONT_COLOR = "FFF2CC";
        } else {
          TABLE_HEADER_FONT_COLOR = "FFFFFF";
        }

        if (headerIndex == 0) {
          if (record == 0) {
            XWPFParagraph paragraph = tableRowHeader.getCell(0).addParagraph();
            paragraph.setAlignment(poiParameter.getAlignment());
            XWPFRun paragraphRun = paragraph.createRun();
            this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
            paragraphRun.setColor(TEXT_FONT_COLOR);
            if (poiParameter.getBold() != null) {
              paragraphRun.setBold(poiParameter.getBold());
            } else {
              paragraphRun.setBold(true);
            }
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          } else {
            XWPFParagraph paragraph = tableRowHeader.createCell().addParagraph();
            paragraph.setAlignment(poiParameter.getAlignment());
            XWPFRun paragraphRun = paragraph.createRun();
            this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
            paragraphRun.setColor(TEXT_FONT_COLOR);
            if (poiParameter.getBold() != null) {
              paragraphRun.setBold(poiParameter.getBold());
            } else {
              paragraphRun.setBold(true);
            }
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          }
        } else {
          XWPFParagraph paragraph = tableRowHeader.getCell(record).addParagraph();
          paragraph.setAlignment(poiParameter.getAlignment());
          XWPFRun paragraphRun = paragraph.createRun();
          this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
          paragraphRun.setColor(TEXT_FONT_COLOR);
          if (poiParameter.getBold() != null) {
            paragraphRun.setBold(poiParameter.getBold());
          } else {
            paragraphRun.setBold(true);
          }
          paragraphRun.setFontFamily(FONT_TYPE);
          paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

          tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
        }
        record++;
      }
      headerIndex++;
      record = 0;
    }

    for (List<POIField> poiParameters : sData) {
      record = 0;

      // Condition for table b cell color in fields 5 and 6
      if (tableType.equals("tableBAnnualReport") && (record == 4 || record == 5)) {
        TABLE_HEADER_FONT_COLOR = "DEEAF6";
      } else if (tableType.contains("tableA2Powb")) {
        TABLE_HEADER_FONT_COLOR = "D9EAD3";
      } else if (tableType.contains("table2AnnualReport2018") && (record == 0 || record == 1)) {
        TABLE_HEADER_FONT_COLOR = "D9E2F3";
      } else if (tableType.contains("table6AnnualReport2018") && (record == 0 || record == 1)) {
        TABLE_HEADER_FONT_COLOR = "E2EFD9";
      } else if (tableType.contains("table13AnnualReport2018") && (record == 0 || record == 1)) {
        TABLE_HEADER_FONT_COLOR = "FFF2CC";
      } else {
        TABLE_HEADER_FONT_COLOR = "FFFFFF";
      }

      XWPFTableRow dataRow = table.createRow();
      for (POIField poiParameter : poiParameters) {
        count++;
        XWPFParagraph paragraph = null;
        if (!poiParameter.isHtml()) {
          paragraph = dataRow.getCell(record).addParagraph();
          paragraph.setAlignment(poiParameter.getAlignment());
        }
        // HiperLinks
        if (poiParameter.getUrls() != null && !poiParameter.getUrls().isEmpty()) {
          for (int i = 0; i < poiParameter.getUrls().size(); i++) {
            // For don't hurt the implementation'for the others paragraph options
            if (i == 0) {
              this.textHyperlink(poiParameter.getUrls().get(i), poiParameter.getTexts().get(i), paragraph);
            } else {
              XWPFParagraph breakParagraph = dataRow.getCell(record).addParagraph();
              breakParagraph.setAlignment(poiParameter.getAlignment());
              this.textHyperlink(poiParameter.getUrls().get(i), poiParameter.getTexts().get(i), breakParagraph);
            }
          }
        }
        // Hyperlink
        else if (poiParameter.getUrl() != null && !poiParameter.getUrl().isEmpty()) {
          this.textHyperlink(poiParameter.getUrl(), poiParameter.getText(), paragraph);
        } else {

          if (poiParameter.isHtml()) {
            if (poiParameter.getText() != null) {
              this.convertHTMLTags(null, poiParameter.getText(), dataRow.getCell(record));
            }
          } else {
            XWPFRun paragraphRun = paragraph.createRun();
            this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
            if (poiParameter.getFontColor() != null) {
              paragraphRun.setColor(poiParameter.getFontColor());
            } else {
              paragraphRun.setColor(TEXT_FONT_COLOR);
            }
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

            // Condition for table b cell color in fields 5 and 6
            if (tableType.equals("tableBAnnualReport") && (record == 4 || record == 5)) {
              TABLE_HEADER_FONT_COLOR = "DEEAF6";
              dataRow.getCell(record).setColor("DEEAF6");
            } else if (tableType.equals("table2AnnualReport2018")) {
              if (record == 0) {
                TABLE_HEADER_FONT_COLOR = "D9E2F3";

              } else {
                TABLE_HEADER_FONT_COLOR = "FFFFFF";
              }
            } else {
              TABLE_HEADER_FONT_COLOR = "FFF2CC";
            }

            // highlight and bold first and SecondColumn for table D1
            if (tableType.equals("tableD1AnnualReport") && (record == 0 || record == 1) && count < 9) {
              dataRow.getCell(record).setColor("DEEAF6");
              paragraphRun.setBold(true);
            } else if (tableType.equals("tableD1AnnualReport") && count >= 9 && (record == 0 || record == 1)) {
              dataRow.getCell(record).setColor("E2EFD9");
              paragraphRun.setBold(true);

            } else if (tableType.contains("tableA2Powb") && record < 6) {
              dataRow.getCell(record).setColor("D9EAD3");
            } else if (tableType.contains("table2AnnualReport2018") && record < 1) {
              dataRow.getCell(record).setColor("D9E2F3");
            } else if (tableType.contains("table6AnnualReport2018") && record < 1) {
              dataRow.getCell(record).setColor("E2EFD9");
            } else if (tableType.contains("table13AnnualReport2018") && record < 1) {
              dataRow.getCell(record).setColor("FFF2CC");
            } else if (tableType.contains("table10AnnualReport2018") && record < 1) {
              dataRow.getCell(record).setColor("EAF1DD");
            } else {
              if (highlightFirstColumn && record == 0) {
                dataRow.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
                if (poiParameter.getBold() != null) {
                  paragraphRun.setBold(poiParameter.getBold());
                } else {
                  paragraphRun.setBold(true);
                }
              } else {
                if (poiParameter.getBold() != null) {
                  paragraphRun.setBold(poiParameter.getBold());
                } else {
                  paragraphRun.setBold(false);
                }
              }
            }
          }
        }
        record++;
      }
    }

    switch (tableType) {
      case "tableA":
        this.tableAStyle(table);
        break;
      case "tableE":
        this.tableEStyle(table);
        break;
      case "tableC":
        this.tableCStyle(table);
        break;
      case "tableF":
        this.tableFStyle(table);
        break;
      case "tableG":
        this.tableGStyle(table);
        break;

      // Annual report tables
      case "tableAAnnualReport":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableA1AnnualReport":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableA2AnnualReport":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableBAnnualReport":
        this.tableBAnnualReportStyle(table);
        break;
      case "table1AnnualReport2020":
        this.table1AnnualReport2020Style(table);
        break;
      case "tableCAnnualReport":
        count = 0;
        this.tableCStyle(table);
        break;
      case "tableD1AnnualReport":
        this.tableD1AnnualReportStyle(table);
        break;
      case "tableD2AnnualReport":
        count = 0;
        this.tableAStyle(table);
        break;
      case "tableEAnnualReport":
        this.tableGStyle(table);
        break;
      case "tableFAnnualReport":
        this.tableFStyle(table);
        break;
      case "tableGAnnualReport":
        this.tableGStyle(table);
        break;
      case "tableHAnnualReport":
        this.tableGStyle(table);
        break;
      case "table3AnnualReport2018":
        this.table3AnnualReport2018Style(table);
        break;
      case "table10AnnualReport2018":
        this.table10AnnualReport2018Style(table);
        break;
      case "table4AnnualReport2018":
        this.table4AnnualReport2018Style(table);
        break;

      case "tableJAnnualReport":
        this.tableJAnnualReportStyle(table);
        break;
      case "table13AnnualReport2018":
        this.table13AnnualReportStyle(table, false);
        break;
      case "table13AnnualReport2021":
        this.table13AnnualReportStyle(table, true);
        break;


      // Annual report tables 2018
      case "table6AnnualReport2018":
        this.table6Annual2018ReportStyle(table);
        break;
      case "tableA1AnnualReport2018":
        this.tableA1Annual2018ReportStyle(table);
        break;
      case "tableA2AnnualReport2018":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableBAnnualReport2018":
        this.tableBAnnualReportStyle(table);
        break;
      case "tableCAnnualReport2018":
        count = 0;
        this.tableCStyle(table);
        break;
      case "tableD1AnnualReport2018":
        this.tableD1AnnualReportStyle(table);
        break;
      case "tableD2AnnualReport2018":
        count = 0;
        this.tableAStyle(table);
        break;
      case "table5AnnualReport2018":
        count = 0;
        this.table5AnnualReport2018Style(table);
        break;
      case "table11AnnualReport2018":
        count = 0;
        this.table11AnnualReport2018Style(table);
        break;
      case "tableEAnnualReport2018":
        this.tableGStyle(table);
        break;
      case "tableFAnnualReport2018":
        this.tableFStyle(table);
        break;
      case "tableGAnnualReport2018":
        this.tableGStyle(table);
        break;
      case "tableHAnnualReport2018":
        this.tableGStyle(table);
        break;
      case "tableIAnnualReport2018":
        this.tableIAnnualReportStyle(table);
        break;
      case "tableJAnnualReport2018":
        this.tableJAnnualReportStyle(table);
        break;

      // powb 2019 template tables
      case "table2AnnualReport2018PLT":
        count = 0;
        this.tableAPowbStyle(table);
        break;
      case "table2AnnualReport2018CRP":
        count = 0;
        this.table2AnnualReportCRPStyle(table);
        break;
      case "tableBPowbPLT":
        count = 0;
        // this.tableB2PowbStyle(table);
        break;
      case "tableBPowbCRP":
        count = 0;
        // this.tableB2PowbStyle(table);
        break;
      case "tableC2PowbPLT":
        count = 0;
        // this.tableC2PowbStyle(table);
        break;
      case "tableC2PowbCRP":
        count = 0;
        // this.tableC2PowbStyle(table);
        break;
      case "tableEPowbPLT":
        count = 0;
        this.tableEPowbStyle(table);
        break;
      case "tableEPowbCRP":
        count = 0;
        this.tableEPowbStyle(table);
        break;
      case "merge":
        count = 0;
        this.tableMergeAnnualReportCRPStyle(table);
        break;
    }
    if (tableType.contains("AnnualReport")) {
      table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(13350));
    } else if (tableType.contains("Powb")) {
      table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(13700));
    } else {
      table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(12000));
    }

  }

  public void textTableAnnualReport(XWPFDocument document, List<List<POIField>> sHeaders, List<List<POIField>> sData,
    Boolean highlightFirstColumn, String tableType) {

    if (tableType.contains("Powb")) {
      TABLE_TEXT_FONT_SIZE = 11;
      FONT_TYPE = "Calibri";
    } else {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableC2PowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableC2PowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableEPowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableEPowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableA2PowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableA2PowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    if (tableType.equals("tableBPowbPLT")) {
      TABLE_TEXT_FONT_SIZE = 11;
    } else if (tableType.equals("tableBPowbCRP")) {
      TABLE_TEXT_FONT_SIZE = 10;
    }

    XWPFTable table = document.createTable();
    int record = 0;
    int headerIndex = 0;
    for (List<POIField> poiParameters : sHeaders) {

      // Setting the Header
      XWPFTableRow tableRowHeader;
      if (headerIndex == 0) {
        tableRowHeader = table.getRow(0);
      } else {
        tableRowHeader = table.createRow();
      }
      int index = 0;
      int lastIndex = 0;
      for (POIField poiParameter : poiParameters) {

        // Condition for table b cell color in fields 5 and 6 in annual report
        if (tableType.equals("tableBAnnualReport") && (record == 4 || record == 5)) {
          TABLE_HEADER_FONT_COLOR = "DEEAF6";
          // Condition for table 2a
        } else if (tableType.contains("Powb")) {
          TABLE_HEADER_FONT_COLOR = "FFF2CC";
        } else if (tableType.contains("table2AnnualReport2018")) {
          if (record == 0) {
            TABLE_HEADER_FONT_COLOR = "D9E2F3";

          } else {
            TABLE_HEADER_FONT_COLOR = "FFFFFF";
          }
        } else if (tableType.contains("table5AnnualReport2018")) {
          if (record == 0 || record == 1 || record == 3) {
            TABLE_HEADER_FONT_COLOR = "FFF2CC";
          }
          if (record == 2 || record == 4 || record == 5) {
            TABLE_HEADER_FONT_COLOR = "EAF1DD";

          } else {
            TABLE_HEADER_FONT_COLOR = "FFFFFF";
          }

        } else if (tableType.contains("table6AnnualReport2018")) {
          TABLE_HEADER_FONT_COLOR = "E2EFD9";
        } else if (tableType.contains("table13AnnualReport2018")) {
          TABLE_HEADER_FONT_COLOR = "FFF2CC";

        } else if (tableType.contains("Report2018")) {
          TABLE_HEADER_FONT_COLOR = "FFF2CC";
        } else {
          TABLE_HEADER_FONT_COLOR = "FFFFFF";
        }


        index = poiParameter.getIndex();
        lastIndex = index;

        if (index == lastIndex) {
          index = -1;
        } else {
          lastIndex = index;
        }

        if (headerIndex == 0) {
          if (record == 0) {
            XWPFParagraph paragraph = tableRowHeader.getCell(0).addParagraph();
            paragraph.setAlignment(poiParameter.getAlignment());
            XWPFRun paragraphRun = paragraph.createRun();

            if (index != -1) {
              this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
            }
            paragraphRun.setColor(TEXT_FONT_COLOR);
            if (poiParameter.getBold() != null) {
              paragraphRun.setBold(poiParameter.getBold());
            } else {
              paragraphRun.setBold(true);
            }
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          } else {
            XWPFParagraph paragraph = tableRowHeader.createCell().addParagraph();
            paragraph.setAlignment(poiParameter.getAlignment());
            XWPFRun paragraphRun = paragraph.createRun();
            this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
            paragraphRun.setColor(TEXT_FONT_COLOR);
            if (poiParameter.getBold() != null) {
              paragraphRun.setBold(poiParameter.getBold());
            } else {
              paragraphRun.setBold(true);
            }
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          }
        } else {
          XWPFParagraph paragraph = tableRowHeader.getCell(record).addParagraph();
          paragraph.setAlignment(poiParameter.getAlignment());
          XWPFRun paragraphRun = paragraph.createRun();
          this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
          paragraphRun.setColor(TEXT_FONT_COLOR);
          if (poiParameter.getBold() != null) {
            paragraphRun.setBold(poiParameter.getBold());
          } else {
            paragraphRun.setBold(true);
          }
          paragraphRun.setFontFamily(FONT_TYPE);
          paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

          tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
        }
        record++;
      }
      headerIndex++;
      record = 0;
    }

    for (

    List<POIField> poiParameters : sData) {
      record = 0;

      // Condition for table b cell color in fields 5 and 6
      if (tableType.equals("tableBAnnualReport") && (record == 4 || record == 5)) {
        TABLE_HEADER_FONT_COLOR = "DEEAF6";
      } else if (tableType.contains("tableA2Powb")) {
        TABLE_HEADER_FONT_COLOR = "D9EAD3";
      } else if (tableType.contains("table2AnnualReport2018") && (record == 0 || record == 1)) {
        TABLE_HEADER_FONT_COLOR = "D9E2F3";
      } else if (tableType.contains("table6AnnualReport2018") && (record == 0 || record == 1)) {
        TABLE_HEADER_FONT_COLOR = "E2EFD9";
      } else if (tableType.contains("table13AnnualReport2018") && (record == 0 || record == 1)) {
        TABLE_HEADER_FONT_COLOR = "FFF2CC";

      } else {
        TABLE_HEADER_FONT_COLOR = "FFFFFF";
      }

      XWPFTableRow dataRow = table.createRow();
      for (POIField poiParameter : poiParameters) {
        count++;
        XWPFParagraph paragraph = dataRow.getCell(record).addParagraph();
        paragraph.setAlignment(poiParameter.getAlignment());
        // Hyperlink
        if (poiParameter.getUrl() != null && !poiParameter.getUrl().isEmpty()) {
          this.textHyperlink(poiParameter.getUrl(), poiParameter.getText(), paragraph);
        } else {
          XWPFRun paragraphRun = paragraph.createRun();
          this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
          if (poiParameter.getFontColor() != null) {
            paragraphRun.setColor(poiParameter.getFontColor());
          } else {
            paragraphRun.setColor(TEXT_FONT_COLOR);
          }
          paragraphRun.setFontFamily(FONT_TYPE);
          paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);

          // Condition for table b cell color in fields 5 and 6
          if (tableType.equals("tableBAnnualReport") && (record == 4 || record == 5)) {
            TABLE_HEADER_FONT_COLOR = "DEEAF6";
            dataRow.getCell(record).setColor("DEEAF6");
          } else if (tableType.equals("table2AnnualReport2018")) {
            if (record == 0) {
              TABLE_HEADER_FONT_COLOR = "D9E2F3";

            } else {
              TABLE_HEADER_FONT_COLOR = "FFFFFF";
            }
          } else if (tableType.equals("table5AnnualReport2018")) {

            if (record == 0 || record == 1 || record == 3) {
              TABLE_HEADER_FONT_COLOR = "FFF2CC";
            }
            if (record == 2 || record == 4 || record == 5) {
              TABLE_HEADER_FONT_COLOR = "EAF1DD";

            } else {
              TABLE_HEADER_FONT_COLOR = "FFFFFF";
            }
          } else {
            TABLE_HEADER_FONT_COLOR = "FFF2CC";
          }

          // highlight and bold first and SecondColumn for table D1
          if (tableType.equals("tableD1AnnualReport") && (record == 0 || record == 1) && count < 9) {
            dataRow.getCell(record).setColor("DEEAF6");
            paragraphRun.setBold(true);
          } else if (tableType.equals("tableD1AnnualReport") && count >= 9 && (record == 0 || record == 1)) {
            dataRow.getCell(record).setColor("E2EFD9");
            paragraphRun.setBold(true);

          } else if (tableType.contains("tableA2Powb") && record < 6) {
            dataRow.getCell(record).setColor("D9EAD3");
          } else if (tableType.contains("table2AnnualReport2018") && record < 1) {
            dataRow.getCell(record).setColor("D9E2F3");

          } else {
            if (highlightFirstColumn && record == 0) {
              dataRow.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
              if (poiParameter.getBold() != null) {
                paragraphRun.setBold(poiParameter.getBold());
              } else {
                paragraphRun.setBold(true);
              }
            } else {
              if (poiParameter.getBold() != null) {
                paragraphRun.setBold(poiParameter.getBold());
              } else {
                paragraphRun.setBold(false);
              }
            }
          }
        }
        record++;
      }
    }

    switch (tableType)

    {
      case "tableA":
        this.tableAStyle(table);
        break;
      case "tableE":
        this.tableEStyle(table);
        break;
      case "tableC":
        this.tableCStyle(table);
        break;
      case "tableF":
        this.tableFStyle(table);
        break;
      case "tableG":
        this.tableGStyle(table);
        break;

      // Annual report tables
      case "tableAAnnualReport":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableA1AnnualReport":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableA2AnnualReport":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableBAnnualReport":
        this.tableBAnnualReportStyle(table);
        break;
      case "tableCAnnualReport":
        count = 0;
        this.tableCStyle(table);
        break;
      case "tableD1AnnualReport":
        this.tableD1AnnualReportStyle(table);
        break;
      case "tableD2AnnualReport":
        count = 0;
        this.tableAStyle(table);
        break;
      case "tableEAnnualReport":
        this.tableGStyle(table);
        break;
      case "tableFAnnualReport":
        this.tableFStyle(table);
        break;
      case "tableGAnnualReport":
        this.tableGStyle(table);
        break;
      case "tableHAnnualReport":
        this.tableGStyle(table);
        break;
      case "table3AnnualReport2018":
        this.table3AnnualReport2018Style(table);
        break;
      case "table4AnnualReport2018":
        this.table4AnnualReport2018Style(table);
        break;
      case "table5AnnualReport2018":
        this.table5AnnualReport2018Style(table);
        break;
      case "tableJAnnualReport":
        this.tableJAnnualReportStyle(table);
        break;

      // Annual report tables 2018
      case "table6AnnualReport2018":
        this.table6Annual2018ReportStyle(table);
        break;
      case "tableA1AnnualReport2018":
        this.tableA1Annual2018ReportStyle(table);
        break;
      case "tableA2AnnualReport2018":
        this.tableA1AnnualReportStyle(table);
        break;
      case "tableBAnnualReport2018":
        this.tableBAnnualReportStyle(table);
        break;
      case "tableCAnnualReport2018":
        count = 0;
        this.tableCStyle(table);
        break;
      case "tableD1AnnualReport2018":
        this.tableD1AnnualReportStyle(table);
        break;
      case "tableD2AnnualReport2018":
        count = 0;
        this.tableAStyle(table);
        break;
      case "tableEAnnualReport2018":
        this.tableGStyle(table);
        break;
      case "tableFAnnualReport2018":
        this.tableFStyle(table);
        break;
      case "tableGAnnualReport2018":
        this.tableGStyle(table);
        break;
      case "tableHAnnualReport2018":
        this.tableGStyle(table);
        break;
      case "tableIAnnualReport2018":
        this.tableIAnnualReportStyle(table);
        break;
      case "tableJAnnualReport2018":
        this.tableJAnnualReportStyle(table);
        break;

      // powb 2019 template tables
      case "table2AnnualReport2018PLT":
        count = 0;
        this.tableAPowbStyle(table);
        break;
      case "table2AnnualReport2018CRP":
        count = 0;
        this.table2AnnualReportCRPStyle(table);
        break;
      case "tableBPowbPLT":
        count = 0;
        // this.tableB2PowbStyle(table);
        break;
      case "tableBPowbCRP":
        count = 0;
        // this.tableB2PowbStyle(table);
        break;
      case "tableC2PowbPLT":
        count = 0;
        // this.tableC2PowbStyle(table);
        break;
      case "tableC2PowbCRP":
        count = 0;
        // this.tableC2PowbStyle(table);
        break;
      case "tableEPowbPLT":
        count = 0;
        this.tableEPowbStyle(table);
        break;
      case "tableEPowbCRP":
        count = 0;
        this.tableEPowbStyle(table);
        break;
    }
    if (tableType.contains("AnnualReport")) {
      table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(13350));
    } else if (tableType.contains("Powb")) {
      table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(13700));
    } else {
      table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(12000));
    }

  }


}
