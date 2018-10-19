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

package org.cgiar.ccafs.marlo.utils.richTextPOI;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class HTMLtoWord {

  private static final Pattern HEAVY_REGEX = Pattern.compile("(<br/>)|(</br>)|(<br />)|(< /br>)");
  private static final int START_TAG = 0;
  private static final int END_TAG = 1;
  private static final String NEW_LINE = System.getProperty("line.separator");

  private static Map<Integer, Font> buildFontMap(List<RichTextInfo> textInfos, XWPFDocument document) {
    Map<Integer, Font> fontMap = new LinkedHashMap<Integer, Font>(550, .95f);

    for (RichTextInfo richTextInfo : textInfos) {
      if (richTextInfo.isValid()) {
        for (int i = richTextInfo.getStartIndex(); i < richTextInfo.getEndIndex(); i++) {
          fontMap.put(i, mergeFont(fontMap.get(i), richTextInfo.getFontStyle(), richTextInfo.getFontValue(), document));
        }
      }
    }

    return fontMap;
  }

  //


  static RichTextDetails createCellValue(String html, XWPFDocument document) {
    Config.IsHTMLEmptyElementTagRecognised = true;
    Source source = new Source(html);
    Map<String, TagInfo> tagMap = new LinkedHashMap<String, TagInfo>(550, .95f);
    for (Element e : source.getChildElements()) {
      getInfo(e, tagMap);
    }

    StringBuilder sbPatt = new StringBuilder();
    sbPatt.append("(").append(StringUtils.join(tagMap.keySet(), "|")).append(")");
    String patternString = sbPatt.toString();
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(html);

    StringBuffer textBuffer = new StringBuffer();
    List<RichTextInfo> textInfos = new ArrayList<RichTextInfo>();
    ArrayDeque<RichTextInfo> richTextBuffer = new ArrayDeque<RichTextInfo>();
    while (matcher.find()) {
      matcher.appendReplacement(textBuffer, "");
      TagInfo currentTag = tagMap.get(matcher.group(1));
      if (START_TAG == currentTag.getTagType()) {
        richTextBuffer.push(getRichTextInfo(currentTag, textBuffer.length(), document));
      } else {
        if (!richTextBuffer.isEmpty()) {
          RichTextInfo info = richTextBuffer.pop();
          if (info != null) {
            info.setEndIndex(textBuffer.length());
            textInfos.add(info);
          }
        }
      }
    }
    matcher.appendTail(textBuffer);
    Map<Integer, Font> fontMap = buildFontMap(textInfos, document);

    return new RichTextDetails(textBuffer.toString(), fontMap);
  }

  private static void getInfo(Element e, Map<String, TagInfo> tagMap) {
    tagMap.put(e.getStartTag().toString(),
      new TagInfo(e.getStartTag().getName(), e.getAttributeValue("style"), START_TAG));
    if (e.getChildElements().size() > 0) {
      List<Element> children = e.getChildElements();
      for (Element child : children) {
        getInfo(child, tagMap);
      }
    }
    if (e.getEndTag() != null) {
      tagMap.put(e.getEndTag().toString(), new TagInfo(e.getEndTag().getName(), END_TAG));
    } else {
      // Handling self closing tags
      tagMap.put(e.getStartTag().toString(), new TagInfo(e.getStartTag().getName(), END_TAG));
    }
  }

  private static RichTextInfo getRichTextInfo(TagInfo currentTag, int startIndex, XWPFDocument document) {
    RichTextInfo info = null;
    switch (STYLES.fromValue(currentTag.getTagName())) {
      case SPAN:
        if (!isEmpty(currentTag.getStyle())) {
          for (String style : currentTag.getStyle().split(";")) {
            String[] styleDetails = style.split(":");
            if (styleDetails != null && styleDetails.length > 1) {
              if ("COLOR".equalsIgnoreCase(styleDetails[0].trim())) {
                info = new RichTextInfo(startIndex, -1, STYLES.COLOR, styleDetails[1]);
              }
            }
          }
        }
        break;
      default:
        info = new RichTextInfo(startIndex, -1, STYLES.fromValue(currentTag.getTagName()));
        break;
    }
    return info;
  }

  private static boolean isEmpty(String str) {
    return (str == null || str.trim().length() == 0);
  }

  @SuppressWarnings("deprecation")
  private static Font mergeFont(Font font, STYLES fontStyle, String fontValue, XWPFDocument document) {
    XWPFRun paragraphRun;
    XWPFParagraph paragraph = null;
    paragraph = document.createParagraph();
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    paragraphRun = paragraph.createRun();

    paragraphRun.setColor("000000");
    paragraphRun.setFontFamily("Calibri");
    paragraphRun.setFontSize(11);
    paragraphRun.setBold(false);
    paragraphRun.setUnderline(UnderlinePatterns.NONE);
    paragraphRun.setItalic(false);
    if (font == null) {

      // font = document.createParagraph();
    }

    switch (fontStyle) {
      case BOLD:
      case EM:
      case STRONG:
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        break;
      case UNDERLINE:
        font.setUnderline(Font.U_SINGLE);
        break;
      case ITALLICS:
        font.setItalic(true);
        break;
      case PRE:
        font.setFontName("Courier New");
      case COLOR:
        if (!isEmpty(fontValue)) {

          font.setColor(IndexedColors.BLACK.getIndex());
        }
        break;
      default:
        break;
    }

    return font;
  }

  // this returns a rich text string
  private static RichTextString mergeTextDetails(List<RichTextDetailsWord> cellValues) {
    Config.IsHTMLEmptyElementTagRecognised = true;
    StringBuilder textBuffer = new StringBuilder();
    Map<Integer, XWPFRun> mergedMap = new LinkedHashMap<Integer, XWPFRun>(550, .95f);
    int currentIndex = 0;
    for (RichTextDetailsWord richTextDetail : cellValues) {
      // textBuffer.append(BULLET_CHARACTER + " ");
      currentIndex = textBuffer.length();
      for (Entry<Integer, XWPFRun> entry : richTextDetail.getFontMap().entrySet()) {
        mergedMap.put(entry.getKey() + currentIndex, entry.getValue());
      }
      textBuffer.append(richTextDetail.getRichText()).append(NEW_LINE);
    }

    RichTextString richText = new XSSFRichTextString(textBuffer.toString());
    for (int i = 0; i < textBuffer.length(); i++) {
      Font currentFont = mergedMap.get(i);
      if (currentFont != null) {
        richText.applyFont(i, i + 1, currentFont);
      }
    }
    return richText;
  }

  @Cacheable("tocellvalue")
  public RichTextString fromHtmlToCellValue(String html, XWPFDocument document) {
    Config.IsHTMLEmptyElementTagRecognised = true;

    Matcher m = HEAVY_REGEX.matcher(html);
    String replacedhtml = m.replaceAll("");
    StringBuilder sb = new StringBuilder();
    sb.insert(0, "<div>");
    sb.append(replacedhtml);
    sb.append("</div>");
    String newhtml = sb.toString();
    Source source = new Source(newhtml);
    List<RichTextDetailsWord> cellValues = new ArrayList<RichTextDetailsWord>();
    for (Element el : source.getAllElements("div")) {
      cellValues.add(createCellValue(el.toString(), document));
    }
    RichTextString cellValue = mergeTextDetails(cellValues);


    return cellValue;
  }

}