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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetCasesLinksDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCasesLinks;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisSrfProgressTargetCasesLinksMySQLDAO extends AbstractMarloDAO<ReportSynthesisSrfProgressTargetCasesLinks, Long> implements ReportSynthesisSrfProgressTargetCasesLinksDAO {


  @Inject
  public ReportSynthesisSrfProgressTargetCasesLinksMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisSrfProgressTargetCasesLinks(long reportSynthesisSrfProgressTargetCasesLinksId) {
    ReportSynthesisSrfProgressTargetCasesLinks reportSynthesisSrfProgressTargetCasesLinks = this.find(reportSynthesisSrfProgressTargetCasesLinksId);
    reportSynthesisSrfProgressTargetCasesLinks.setActive(false);
    this.update(reportSynthesisSrfProgressTargetCasesLinks);
  }

  @Override
  public boolean existReportSynthesisSrfProgressTargetCasesLinks(long reportSynthesisSrfProgressTargetCasesLinksID) {
    ReportSynthesisSrfProgressTargetCasesLinks reportSynthesisSrfProgressTargetCasesLinks = this.find(reportSynthesisSrfProgressTargetCasesLinksID);
    if (reportSynthesisSrfProgressTargetCasesLinks == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisSrfProgressTargetCasesLinks find(long id) {
    return super.find(ReportSynthesisSrfProgressTargetCasesLinks.class, id);

  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCasesLinks> findAll() {
    String query = "from " + ReportSynthesisSrfProgressTargetCasesLinks.class.getName() + " where is_active=1";
    List<ReportSynthesisSrfProgressTargetCasesLinks> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisSrfProgressTargetCasesLinks save(ReportSynthesisSrfProgressTargetCasesLinks reportSynthesisSrfProgressTargetCasesLinks) {
    if (reportSynthesisSrfProgressTargetCasesLinks.getId() == null) {
      super.saveEntity(reportSynthesisSrfProgressTargetCasesLinks);
    } else {
      reportSynthesisSrfProgressTargetCasesLinks = super.update(reportSynthesisSrfProgressTargetCasesLinks);
    }


    return reportSynthesisSrfProgressTargetCasesLinks;
  }


}