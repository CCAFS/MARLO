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

import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class RichTextDetailsWord {

  private String richText;
  private Map<Integer, XWPFRun> fontMap;

  public RichTextDetailsWord(String richText, Map<Integer, XWPFRun> fontMap2) {
    this.richText = richText;
    this.fontMap = fontMap2;
  }

  public Map<Integer, XWPFRun> getFontMap() {
    return fontMap;
  }

  public String getRichText() {
    return richText;
  }

  @Override
  public int hashCode() {

    // The goal is to have a more efficient hashcode than standard one.
    return richText.hashCode();
  }

  public void setFontMap(Map<Integer, XWPFRun> fontMap) {
    this.fontMap = fontMap;
  }

  public void setRichText(String richText) {
    this.richText = richText;
  }
}
