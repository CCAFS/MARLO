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
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.service.impl.CenterProjectCrosscutingThemeManager;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterProjectCrosscutingThemeManager.class)
public interface ICenterProjectCrosscutingThemeManager {


  /**
   * This method removes a specific projectCrosscutingTheme value from the database.
   * 
   * @param projectCrosscutingThemeId is the projectCrosscutingTheme identifier.
   * @return true if the projectCrosscutingTheme was successfully deleted, false otherwise.
   */
  public boolean deleteProjectCrosscutingTheme(long projectCrosscutingThemeId);


  /**
   * This method validate if the projectCrosscutingTheme identify with the given id exists in the system.
   * 
   * @param projectCrosscutingThemeID is a projectCrosscutingTheme identifier.
   * @return true if the projectCrosscutingTheme exists, false otherwise.
   */
  public boolean existProjectCrosscutingTheme(long projectCrosscutingThemeID);


  /**
   * This method gets a list of projectCrosscutingTheme that are active
   * 
   * @return a list from CenterProjectCrosscutingTheme null if no exist records
   */
  public List<CenterProjectCrosscutingTheme> findAll();


  /**
   * This method gets a projectCrosscutingTheme object by a given projectCrosscutingTheme identifier.
   * 
   * @param projectCrosscutingThemeID is the projectCrosscutingTheme identifier.
   * @return a CenterProjectCrosscutingTheme object.
   */
  public CenterProjectCrosscutingTheme getProjectCrosscutingThemeById(long projectCrosscutingThemeID);

  /**
   * This method gets a list of projectCrosscutingThemes belongs of the user
   * 
   * @param userId - the user id
   * @return List of ProjectCrosscutingThemes or null if the user is invalid or not have roles.
   */
  public List<CenterProjectCrosscutingTheme> getProjectCrosscutingThemesByUserId(Long userId);

  /**
   * This method saves the information of the given projectCrosscutingTheme
   * 
   * @param projectCrosscutingTheme - is the projectCrosscutingTheme object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectCrosscutingTheme was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProjectCrosscutingTheme(CenterProjectCrosscutingTheme projectCrosscutingTheme);


}
