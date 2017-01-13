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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CaseStudyIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;

import java.util.List;

import com.google.inject.Inject;

public class CaseStudyIndicatorMySQLDAO implements CaseStudyIndicatorDAO {

  private StandardDAO dao;

  @Inject
  public CaseStudyIndicatorMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCaseStudyIndicator(long caseStudyIndicatorId) {
    CaseStudyIndicator caseStudyIndicator = this.find(caseStudyIndicatorId);

    return dao.delete(caseStudyIndicator);
  }

  @Override
  public boolean existCaseStudyIndicator(long caseStudyIndicatorID) {
    CaseStudyIndicator caseStudyIndicator = this.find(caseStudyIndicatorID);
    if (caseStudyIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public CaseStudyIndicator find(long id) {
    return dao.find(CaseStudyIndicator.class, id);

  }

  @Override
  public List<CaseStudyIndicator> findAll() {
    String query = "from " + CaseStudyIndicator.class.getName() + " where is_active=1";
    List<CaseStudyIndicator> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CaseStudyIndicator caseStudyIndicator) {
    if (caseStudyIndicator.getId() == null) {
      dao.save(caseStudyIndicator);
    } else {
      dao.update(caseStudyIndicator);
    }


    return caseStudyIndicator.getId();
  }


}