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


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectFundingSourceService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProjectFundingSourceService implements ICenterProjectFundingSourceService {


  private ICenterProjectFundingSourceDAO projectFundingSourceDAO;

  // Managers


  @Inject
  public CenterProjectFundingSourceService(ICenterProjectFundingSourceDAO projectFundingSourceDAO) {
    this.projectFundingSourceDAO = projectFundingSourceDAO;


  }

  @Override
  public boolean deleteProjectFundingSource(long projectFundingSourceId) {

    return projectFundingSourceDAO.deleteProjectFundingSource(projectFundingSourceId);
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
  public long saveProjectFundingSource(CenterProjectFundingSource projectFundingSource) {

    return projectFundingSourceDAO.save(projectFundingSource);
  }


}
