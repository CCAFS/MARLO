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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.ButtonGuideContent;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ButtonGuideContentManager {


  /**
   * This method removes a specific buttonGuideContent value from the database.
   * 
   * @param buttonGuideContentId is the buttonGuideContent identifier.
   * @return true if the buttonGuideContent was successfully deleted, false otherwise.
   */
  public void deleteButtonGuideContent(long buttonGuideContentId);


  /**
   * This method validate if the buttonGuideContent identify with the given id exists in the system.
   * 
   * @param buttonGuideContentID is a buttonGuideContent identifier.
   * @return true if the buttonGuideContent exists, false otherwise.
   */
  public boolean existButtonGuideContent(long buttonGuideContentID);


  /**
   * This method gets a list of buttonGuideContent that are active
   * 
   * @return a list from ButtonGuideContent null if no exist records
   */
  public List<ButtonGuideContent> findAll();


  /**
   * This method gets a buttonGuideContent object by a given buttonGuideContent identifier.
   * 
   * @param buttonGuideContentID is the buttonGuideContent identifier.
   * @return a ButtonGuideContent object.
   */
  public ButtonGuideContent getButtonGuideContentById(long buttonGuideContentID);

  /**
   * This method gets a buttonGuideContent object by a given buttonGuideContent identifier.
   * 
   * @param identifier is the buttonGuideContent identifier text.
   * @return a ButtonGuideContent object.
   */
  public ButtonGuideContent getButtonGuideContentByIdentifier(String identifier);

  /**
   * This method saves the information of the given buttonGuideContent
   * 
   * @param buttonGuideContent - is the buttonGuideContent object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the buttonGuideContent was
   *         updated
   *         or -1 is some error occurred.
   */
  public ButtonGuideContent saveButtonGuideContent(ButtonGuideContent buttonGuideContent);


}
