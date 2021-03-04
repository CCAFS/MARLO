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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetContributionDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisSrfProgressTargetContributionMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisSrfProgressTargetContribution, Long>
  implements ReportSynthesisSrfProgressTargetContributionDAO {


  @Inject
  public ReportSynthesisSrfProgressTargetContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisSrfProgressTargetContribution(long reportSynthesisSrfProgressTargetContributionId) {
    ReportSynthesisSrfProgressTargetContribution reportSynthesisSrfProgressTargetContribution =
      this.find(reportSynthesisSrfProgressTargetContributionId);
    this.delete(reportSynthesisSrfProgressTargetContribution);
  }

  @Override
  public boolean
    existReportSynthesisSrfProgressTargetContribution(long reportSynthesisSrfProgressTargetContributionID) {
    ReportSynthesisSrfProgressTargetContribution reportSynthesisSrfProgressTargetContribution =
      this.find(reportSynthesisSrfProgressTargetContributionID);
    if (reportSynthesisSrfProgressTargetContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisSrfProgressTargetContribution find(long id) {
    return super.find(ReportSynthesisSrfProgressTargetContribution.class, id);

  }

  @Override
  public List<ReportSynthesisSrfProgressTargetContribution> findAll() {
    String query = "from " + ReportSynthesisSrfProgressTargetContribution.class.getName() + " where is_active=1";
    List<ReportSynthesisSrfProgressTargetContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisSrfProgressTargetContribution
    save(ReportSynthesisSrfProgressTargetContribution reportSynthesisSrfProgressTargetContribution) {
    if (reportSynthesisSrfProgressTargetContribution.getId() == null) {
      super.saveEntity(reportSynthesisSrfProgressTargetContribution);
    } else {
      reportSynthesisSrfProgressTargetContribution = super.update(reportSynthesisSrfProgressTargetContribution);
    }


    return reportSynthesisSrfProgressTargetContribution;
  }


}