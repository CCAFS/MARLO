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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisNarrativeDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisNarrative;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisNarrativeMySQLDAO extends AbstractMarloDAO<ReportSynthesisNarrative, Long> implements ReportSynthesisNarrativeDAO {


  @Inject
  public ReportSynthesisNarrativeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisNarrative(long reportSynthesisNarrativeId) {
    ReportSynthesisNarrative reportSynthesisNarrative = this.find(reportSynthesisNarrativeId);
    reportSynthesisNarrative.setActive(false);
    this.update(reportSynthesisNarrative);
  }

  @Override
  public boolean existReportSynthesisNarrative(long reportSynthesisNarrativeID) {
    ReportSynthesisNarrative reportSynthesisNarrative = this.find(reportSynthesisNarrativeID);
    if (reportSynthesisNarrative == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisNarrative find(long id) {
    return super.find(ReportSynthesisNarrative.class, id);

  }

  @Override
  public List<ReportSynthesisNarrative> findAll() {
    String query = "from " + ReportSynthesisNarrative.class.getName() + " where is_active=1";
    List<ReportSynthesisNarrative> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisNarrative save(ReportSynthesisNarrative reportSynthesisNarrative) {
    if (reportSynthesisNarrative.getId() == null) {
      super.saveEntity(reportSynthesisNarrative);
    } else {
      reportSynthesisNarrative = super.update(reportSynthesisNarrative);
    }


    return reportSynthesisNarrative;
  }


}