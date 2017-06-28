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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectCrosscutingThemeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectCrosscutingThemeService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProjectCrosscutingThemeService implements ICenterProjectCrosscutingThemeService {


  private ICenterProjectCrosscutingThemeDAO projectCrosscutingThemeDAO;

  // Managers


  @Inject
  public CenterProjectCrosscutingThemeService(ICenterProjectCrosscutingThemeDAO projectCrosscutingThemeDAO) {
    this.projectCrosscutingThemeDAO = projectCrosscutingThemeDAO;


  }

  @Override
  public boolean deleteProjectCrosscutingTheme(long projectCrosscutingThemeId) {

    return projectCrosscutingThemeDAO.deleteProjectCrosscutingTheme(projectCrosscutingThemeId);
  }

  @Override
  public boolean existProjectCrosscutingTheme(long projectCrosscutingThemeID) {

    return projectCrosscutingThemeDAO.existProjectCrosscutingTheme(projectCrosscutingThemeID);
  }

  @Override
  public List<CenterProjectCrosscutingTheme> findAll() {

    return projectCrosscutingThemeDAO.findAll();

  }

  @Override
  public CenterProjectCrosscutingTheme getProjectCrosscutingThemeById(long projectCrosscutingThemeID) {

    return projectCrosscutingThemeDAO.find(projectCrosscutingThemeID);
  }

  @Override
  public List<CenterProjectCrosscutingTheme> getProjectCrosscutingThemesByUserId(Long userId) {
    return projectCrosscutingThemeDAO.getProjectCrosscutingThemesByUserId(userId);
  }

  @Override
  public long saveProjectCrosscutingTheme(CenterProjectCrosscutingTheme projectCrosscutingTheme) {

    return projectCrosscutingThemeDAO.save(projectCrosscutingTheme);
  }


}
