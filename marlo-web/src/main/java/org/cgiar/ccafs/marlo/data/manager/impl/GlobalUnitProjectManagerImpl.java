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


import org.cgiar.ccafs.marlo.data.dao.GlobalUnitProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class GlobalUnitProjectManagerImpl implements GlobalUnitProjectManager {


  private GlobalUnitProjectDAO globalUnitProjectDAO;
  // Managers


  @Inject
  public GlobalUnitProjectManagerImpl(GlobalUnitProjectDAO globalUnitProjectDAO) {
    this.globalUnitProjectDAO = globalUnitProjectDAO;


  }

  @Override
  public void deleteGlobalUnitProject(long globalUnitProjectId) {

    globalUnitProjectDAO.deleteGlobalUnitProject(globalUnitProjectId);
  }

  @Override
  public boolean existGlobalUnitProject(long globalUnitProjectID) {

    return globalUnitProjectDAO.existGlobalUnitProject(globalUnitProjectID);
  }

  @Override
  public List<GlobalUnitProject> findAll() {

    return globalUnitProjectDAO.findAll();

  }

  @Override
  public GlobalUnitProject getGlobalUnitProjectById(long globalUnitProjectID) {

    return globalUnitProjectDAO.find(globalUnitProjectID);
  }

  @Override
  public GlobalUnitProject saveGlobalUnitProject(GlobalUnitProject globalUnitProject) {

    return globalUnitProjectDAO.save(globalUnitProject);
  }


}
