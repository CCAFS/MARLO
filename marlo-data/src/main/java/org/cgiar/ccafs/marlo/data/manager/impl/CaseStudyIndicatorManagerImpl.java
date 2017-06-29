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


import org.cgiar.ccafs.marlo.data.dao.CaseStudyIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CaseStudyIndicatorManagerImpl implements CaseStudyIndicatorManager {


  private CaseStudyIndicatorDAO caseStudyIndicatorDAO;
  // Managers


  @Inject
  public CaseStudyIndicatorManagerImpl(CaseStudyIndicatorDAO caseStudyIndicatorDAO) {
    this.caseStudyIndicatorDAO = caseStudyIndicatorDAO;


  }

  @Override
  public boolean deleteCaseStudyIndicator(long caseStudyIndicatorId) {

    return caseStudyIndicatorDAO.deleteCaseStudyIndicator(caseStudyIndicatorId);
  }

  @Override
  public boolean existCaseStudyIndicator(long caseStudyIndicatorID) {

    return caseStudyIndicatorDAO.existCaseStudyIndicator(caseStudyIndicatorID);
  }

  @Override
  public List<CaseStudyIndicator> findAll() {

    return caseStudyIndicatorDAO.findAll();

  }

  @Override
  public CaseStudyIndicator getCaseStudyIndicatorById(long caseStudyIndicatorID) {

    return caseStudyIndicatorDAO.find(caseStudyIndicatorID);
  }

  @Override
  public long saveCaseStudyIndicator(CaseStudyIndicator caseStudyIndicator) {

    return caseStudyIndicatorDAO.save(caseStudyIndicator);
  }


}
