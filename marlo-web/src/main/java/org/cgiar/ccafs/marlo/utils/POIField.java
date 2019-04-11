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

import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

public class POIField {

  private String text;
  private ParagraphAlignment alignment;
  private Boolean bold;
  private String fontColor;
  private Boolean underlined;
  private String url;
  private int index;
  // New filed to Manage hiperlinks bullets
  private List<String> urls;
  private List<String> texts;

  /**
   * Constructor for make and cell-paragraph with hiperlink bullets
   * 
   * @param texts - The list of the Texts that include into the hiperlink
   * @param urls - the list of hiperlinks urls to transform
   */
  public POIField(List<String> texts, List<String> urls, ParagraphAlignment alignment, Boolean bold, String fontColor,
    int index) {
    super();
    this.alignment = alignment;
    this.bold = bold;
    this.fontColor = fontColor;
    this.index = index;
    this.urls = urls;
    this.texts = texts;
  }


  public POIField(String text, ParagraphAlignment alignment) {
    super();
    this.text = text;
    this.alignment = alignment;
  }


  public POIField(String text, ParagraphAlignment alignment, Boolean bold, String fontColor) {
    super();
    this.text = text;
    this.alignment = alignment;
    this.bold = bold;
    this.fontColor = fontColor;
  }


  public POIField(String text, ParagraphAlignment alignment, Boolean bold, String fontColor, String url) {
    super();
    this.text = text;
    this.alignment = alignment;
    this.bold = bold;
    this.fontColor = fontColor;
    this.url = url;
  }

  public POIField(String text, ParagraphAlignment alignment, Boolean bold, String fontColor, String url, int index) {
    super();
    this.text = text;
    this.alignment = alignment;
    this.bold = bold;
    this.fontColor = fontColor;
    this.url = url;
    this.index = index;
  }


  public ParagraphAlignment getAlignment() {
    return alignment;
  }


  public Boolean getBold() {
    return bold;
  }


  public String getFontColor() {
    return fontColor;
  }

  public int getIndex() {
    return index;
  }


  public String getText() {
    return text;
  }


  public List<String> getTexts() {
    return texts;
  }

  public Boolean getUnderlined() {
    return underlined;
  }

  public String getUrl() {
    return url;
  }


  public List<String> getUrls() {
    return urls;
  }


  public void setAlignment(ParagraphAlignment alignment) {
    this.alignment = alignment;
  }


  public void setBold(Boolean bold) {
    this.bold = bold;
  }


  public void setFontColor(String fontColor) {
    this.fontColor = fontColor;
  }


  public void setIndex(int index) {
    this.index = index;
  }


  public void setText(String text) {
    this.text = text;
  }


  public void setTexts(List<String> texts) {
    this.texts = texts;
  }


  public void setUnderlined(Boolean underlined) {
    this.underlined = underlined;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUrls(List<String> urls) {
    this.urls = urls;
  }

}
