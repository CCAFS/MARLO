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


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectOutputDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectOutputManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProjectOutputManager implements ICenterProjectOutputManager {


  private ICenterProjectOutputDAO projectOutputDAO;

  // Managers


  @Inject
  public CenterProjectOutputManager(ICenterProjectOutputDAO projectOutputDAO) {
    this.projectOutputDAO = projectOutputDAO;


  }

  @Override
  public void deleteProjectOutput(long projectOutputId) {

    projectOutputDAO.deleteProjectOutput(projectOutputId);
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
  public CenterProjectOutput saveProjectOutput(CenterProjectOutput projectOutput) {

    return projectOutputDAO.save(projectOutput);
  }


}
