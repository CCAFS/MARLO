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


import org.cgiar.ccafs.marlo.data.dao.ExpectedStudyProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ExpectedStudyProjectManagerImpl implements ExpectedStudyProjectManager {


  private ExpectedStudyProjectDAO expectedStudyProjectDAO;
  // Managers


  @Inject
  public ExpectedStudyProjectManagerImpl(ExpectedStudyProjectDAO expectedStudyProjectDAO) {
    this.expectedStudyProjectDAO = expectedStudyProjectDAO;


  }

  @Override
  public void deleteExpectedStudyProject(long expectedStudyProjectId) {

    expectedStudyProjectDAO.deleteExpectedStudyProject(expectedStudyProjectId);
  }

  @Override
  public boolean existExpectedStudyProject(long expectedStudyProjectID) {

    return expectedStudyProjectDAO.existExpectedStudyProject(expectedStudyProjectID);
  }

  @Override
  public List<ExpectedStudyProject> findAll() {

    return expectedStudyProjectDAO.findAll();

  }

  @Override
  public ExpectedStudyProject getExpectedStudyProjectById(long expectedStudyProjectID) {

    return expectedStudyProjectDAO.find(expectedStudyProjectID);
  }

  @Override
  public ExpectedStudyProject saveExpectedStudyProject(ExpectedStudyProject expectedStudyProject) {

    return expectedStudyProjectDAO.save(expectedStudyProject);
  }


}
