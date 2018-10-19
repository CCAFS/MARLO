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

public class TagInfo {

  private String tagName;
  private String style;
  private int tagType;

  public TagInfo(String tagName, int tagType) {
    this.tagName = tagName;
    this.tagType = tagType;
  }

  public TagInfo(String tagName, String style, int tagType) {
    this.tagName = tagName;
    this.style = style;
    this.tagType = tagType;
  }

  public String getStyle() {
    return style;
  }

  public String getTagName() {
    return tagName;
  }

  public int getTagType() {
    return tagType;
  }

  @Override
  public int hashCode() {

    // The goal is to have a more efficient hashcode than standard one.
    return tagName.hashCode();
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public void setTagType(int tagType) {
    this.tagType = tagType;
  }

  @Override
  public String toString() {
    return "TagInfo [tagName=" + tagName + ", style=" + style + ", tagType=" + tagType + "]";
  }
}
