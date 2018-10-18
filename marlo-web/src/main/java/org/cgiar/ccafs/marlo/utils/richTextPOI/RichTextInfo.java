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

import org.springframework.cache.annotation.Cacheable;


public class RichTextInfo {

  private int startIndex;
  private int endIndex;
  private STYLES fontStyle;
  private String fontValue;

  public RichTextInfo(int startIndex, int endIndex, STYLES fontStyle) {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.fontStyle = fontStyle;
  }

  public RichTextInfo(int startIndex, int endIndex, STYLES fontStyle, String fontValue) {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.fontStyle = fontStyle;
    this.fontValue = fontValue;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public STYLES getFontStyle() {
    return fontStyle;
  }

  public String getFontValue() {
    return fontValue;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public boolean isValid() {
    return (startIndex != -1 && endIndex != -1 && endIndex >= startIndex);
  }

  public void setEndIndex(int endIndex) {
    this.endIndex = endIndex;
  }

  @Cacheable("fontstyle")
  public void setFontStyle(STYLES fontStyle) {
    this.fontStyle = fontStyle;
  }

  public void setFontValue(String fontValue) {
    this.fontValue = fontValue;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  @Override
  public String toString() {
    return "RichTextInfo [startIndex=" + startIndex + ", endIndex=" + endIndex + ", fontStyle=" + fontStyle
      + ", fontValue=" + fontValue + "]";
  }
}
