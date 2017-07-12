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


import org.cgiar.ccafs.marlo.data.dao.CaseStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CaseStudyManagerImpl implements CaseStudyManager {


  private CaseStudyDAO caseStudyDAO;
  // Managers


  @Inject
  public CaseStudyManagerImpl(CaseStudyDAO caseStudyDAO) {
    this.caseStudyDAO = caseStudyDAO;


  }

  @Override
  public boolean deleteCaseStudy(long caseStudyId) {

    return caseStudyDAO.deleteCaseStudy(caseStudyId);
  }

  @Override
  public boolean existCaseStudy(long caseStudyID) {

    return caseStudyDAO.existCaseStudy(caseStudyID);
  }

  @Override
  public List<CaseStudy> findAll() {

    return caseStudyDAO.findAll();

  }

  @Override
  public CaseStudy getCaseStudyById(long caseStudyID) {

    return caseStudyDAO.find(caseStudyID);
  }

  @Override
  public long saveCaseStudy(CaseStudy caseStudy) {

    return caseStudyDAO.save(caseStudy);
  }

  @Override
  public long saveCaseStudy(CaseStudy caseStudy, String section, List<String> relationsName) {
    return caseStudyDAO.save(caseStudy, section, relationsName);
  }


}
