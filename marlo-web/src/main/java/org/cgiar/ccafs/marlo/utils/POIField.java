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

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

public class POIField {

  private String text;
  private ParagraphAlignment alignment;
  private Boolean bold;


  public POIField(String text, ParagraphAlignment alignment) {
    super();
    this.text = text;
    this.alignment = alignment;
  }


  public POIField(String text, ParagraphAlignment alignment, Boolean bold) {
    super();
    this.text = text;
    this.alignment = alignment;
    this.bold = bold;
  }


  public ParagraphAlignment getAlignment() {
    return alignment;
  }


  public Boolean getBold() {
    return bold;
  }


  public String getText() {
    return text;
  }


  public void setAlignment(ParagraphAlignment alignment) {
    this.alignment = alignment;
  }


  public void setBold(Boolean bold) {
    this.bold = bold;
  }


  public void setText(String text) {
    this.text = text;
  }

}
