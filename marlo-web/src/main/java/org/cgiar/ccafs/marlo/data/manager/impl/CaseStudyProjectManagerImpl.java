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


import org.cgiar.ccafs.marlo.data.dao.CaseStudyProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyProjectManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CaseStudyProjectManagerImpl implements CaseStudyProjectManager {


  private CaseStudyProjectDAO caseStudyProjectDAO;
  // Managers


  @Inject
  public CaseStudyProjectManagerImpl(CaseStudyProjectDAO caseStudyProjectDAO) {
    this.caseStudyProjectDAO = caseStudyProjectDAO;


  }

  @Override
  public boolean deleteCaseStudyProject(long caseStudyProjectId) {

    return caseStudyProjectDAO.deleteCaseStudyProject(caseStudyProjectId);
  }

  @Override
  public boolean existCaseStudyProject(long caseStudyProjectID) {

    return caseStudyProjectDAO.existCaseStudyProject(caseStudyProjectID);
  }

  @Override
  public List<CaseStudyProject> findAll() {

    return caseStudyProjectDAO.findAll();

  }

  @Override
  public CaseStudyProject getCaseStudyProjectById(long caseStudyProjectID) {

    return caseStudyProjectDAO.find(caseStudyProjectID);
  }

  @Override
  public long saveCaseStudyProject(CaseStudyProject caseStudyProject) {

    return caseStudyProjectDAO.save(caseStudyProject);
  }


}
