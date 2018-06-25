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

/**
 * @author Andres Valencia
 */
public interface HTMLParser {

  /**
   * This method removes a specific projectExpectedStudyInfo value from the database.
   * 
   * @param projectExpectedStudyInfoId is the projectExpectedStudyInfo identifier.
   * @return true if the projectExpectedStudyInfo was successfully deleted, false otherwise.
   */
  /**
   * This method convert a specific plain text value from the database to HTML
   * 
   * @param plainText
   * @return HTML Text
   */
  public String plainTextToHtml(String plainText);

}
