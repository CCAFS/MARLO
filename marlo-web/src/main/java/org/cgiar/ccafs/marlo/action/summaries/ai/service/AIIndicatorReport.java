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

package org.cgiar.ccafs.marlo.action.summaries.ai.service;

public class AIIndicatorReport {

  private String indicator;
  private int year;
  private String content;
  private String status;

  public String getContent() {
    return content;
  }

  // Getters y Setters
  public String getIndicator() {
    return indicator;
  }

  public String getStatus() {
    return status;
  }

  public int getYear() {
    return year;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
