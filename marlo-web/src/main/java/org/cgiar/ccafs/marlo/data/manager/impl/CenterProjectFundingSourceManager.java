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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectFundingSourceManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProjectFundingSourceManager implements ICenterProjectFundingSourceManager {


  private ICenterProjectFundingSourceDAO projectFundingSourceDAO;

  // Managers


  @Inject
  public CenterProjectFundingSourceManager(ICenterProjectFundingSourceDAO projectFundingSourceDAO) {
    this.projectFundingSourceDAO = projectFundingSourceDAO;


  }

  @Override
  public void deleteProjectFundingSource(long projectFundingSourceId) {

    projectFundingSourceDAO.deleteProjectFundingSource(projectFundingSourceId);
  }

  @Override
  public boolean existProjectFundingSource(long projectFundingSourceID) {

    return projectFundingSourceDAO.existProjectFundingSource(projectFundingSourceID);
  }

  @Override
  public List<CenterProjectFundingSource> findAll() {

    return projectFundingSourceDAO.findAll();

  }

  @Override
  public CenterProjectFundingSource getProjectFundingSourceById(long projectFundingSourceID) {

    return projectFundingSourceDAO.find(projectFundingSourceID);
  }

  @Override
  public List<CenterProjectFundingSource> getProjectFundingSourcesByUserId(Long userId) {
    return projectFundingSourceDAO.getProjectFundingSourcesByUserId(userId);
  }

  @Override
  public CenterProjectFundingSource saveProjectFundingSource(CenterProjectFundingSource projectFundingSource) {

    return projectFundingSourceDAO.save(projectFundingSource);
  }


}
