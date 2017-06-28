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


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectOutputService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProjectOutputService implements ICenterProjectOutputService {


  private ICenterProjectOutputDAO projectOutputDAO;

  // Managers


  @Inject
  public CenterProjectOutputService(ICenterProjectOutputDAO projectOutputDAO) {
    this.projectOutputDAO = projectOutputDAO;


  }

  @Override
  public boolean deleteProjectOutput(long projectOutputId) {

    return projectOutputDAO.deleteProjectOutput(projectOutputId);
  }

  @Override
  public boolean existProjectOutput(long projectOutputID) {

    return projectOutputDAO.existProjectOutput(projectOutputID);
  }

  @Override
  public List<CenterProjectOutput> findAll() {

    return projectOutputDAO.findAll();

  }

  @Override
  public CenterProjectOutput getProjectOutputById(long projectOutputID) {

    return projectOutputDAO.find(projectOutputID);
  }

  @Override
  public List<CenterProjectOutput> getProjectOutputsByUserId(Long userId) {
    return projectOutputDAO.getProjectOutputsByUserId(userId);
  }

  @Override
  public long saveProjectOutput(CenterProjectOutput projectOutput) {

    return projectOutputDAO.save(projectOutput);
  }


}
