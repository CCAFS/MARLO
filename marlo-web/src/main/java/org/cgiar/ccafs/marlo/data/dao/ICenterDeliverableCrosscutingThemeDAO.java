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


package org.cgiar.ccafs.marlo.data.dao;


import org.cgiar.ccafs.marlo.data.dao.mysql.CenterDeliverableCrosscutingThemeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableCrosscutingTheme;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterDeliverableCrosscutingThemeDAO.class)
public interface ICenterDeliverableCrosscutingThemeDAO {

  /**
   * This method removes a specific deliverableCrosscutingTheme value from the database.
   * 
   * @param deliverableCrosscutingThemeId is the deliverableCrosscutingTheme identifier.
   * @return true if the deliverableCrosscutingTheme was successfully deleted, false otherwise.
   */
  public void deleteDeliverableCrosscutingTheme(long deliverableCrosscutingThemeId);

  /**
   * This method validate if the deliverableCrosscutingTheme identify with the given id exists in the system.
   * 
   * @param deliverableCrosscutingThemeID is a deliverableCrosscutingTheme identifier.
   * @return true if the deliverableCrosscutingTheme exists, false otherwise.
   */
  public boolean existDeliverableCrosscutingTheme(long deliverableCrosscutingThemeID);

  /**
   * This method gets a deliverableCrosscutingTheme object by a given deliverableCrosscutingTheme identifier.
   * 
   * @param deliverableCrosscutingThemeID is the deliverableCrosscutingTheme identifier.
   * @return a CenterDeliverableCrosscutingTheme object.
   */
  public CenterDeliverableCrosscutingTheme find(long id);

  /**
   * This method gets a list of deliverableCrosscutingTheme that are active
   * 
   * @return a list from CenterDeliverableCrosscutingTheme null if no exist records
   */
  public List<CenterDeliverableCrosscutingTheme> findAll();


  /**
   * This method gets a list of deliverableCrosscutingThemes belongs of the user
   * 
   * @param userId - the user id
   * @return List of DeliverableCrosscutingThemes or null if the user is invalid or not have roles.
   */
  public List<CenterDeliverableCrosscutingTheme> getDeliverableCrosscutingThemesByUserId(long userId);

  /**
   * This method saves the information of the given deliverableCrosscutingTheme
   * 
   * @param deliverableCrosscutingTheme - is the deliverableCrosscutingTheme object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableCrosscutingTheme was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterDeliverableCrosscutingTheme save(CenterDeliverableCrosscutingTheme deliverableCrosscutingTheme);

  /**
   * This method saves the information of the given deliverableCrosscutingTheme
   * 
   * @param deliverableCrosscutingTheme - is the deliverableCrosscutingTheme object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableCrosscutingTheme was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterDeliverableCrosscutingTheme save(CenterDeliverableCrosscutingTheme deliverableCrosscutingTheme, String actionName,
    List<String> relationsName);
}
