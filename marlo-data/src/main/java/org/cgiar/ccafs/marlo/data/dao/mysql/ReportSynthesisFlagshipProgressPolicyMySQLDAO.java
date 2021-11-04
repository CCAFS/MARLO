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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressPolicyDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressPolicyMySQLDAO extends
  AbstractMarloDAO<ReportSynthesisFlagshipProgressPolicy, Long> implements ReportSynthesisFlagshipProgressPolicyDAO {


  @Inject
  public ReportSynthesisFlagshipProgressPolicyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyId) {
    ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy =
      this.find(reportSynthesisFlagshipProgressPolicyId);
    reportSynthesisFlagshipProgressPolicy.setActive(false);
    this.update(reportSynthesisFlagshipProgressPolicy);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyID) {
    ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy =
      this.find(reportSynthesisFlagshipProgressPolicyID);
    if (reportSynthesisFlagshipProgressPolicy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy find(long id) {
    return super.find(ReportSynthesisFlagshipProgressPolicy.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressPolicy> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressPolicy.class.getName();
    List<ReportSynthesisFlagshipProgressPolicy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy
    getReportSynthesisFlagshipProgressPolicyByPolicyAndFlagshipProgress(long policyId, long flagshipProgressId) {
    String query =
      "select rsfpp from ReportSynthesisFlagshipProgressPolicy rsfpp where rsfpp.projectPolicy.id = :policyId "
        + "and rsfpp.reportSynthesisFlagshipProgress.id = :flagshipProgressId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("policyId", policyId);
    createQuery.setParameter("flagshipProgressId", flagshipProgressId);

    List<ReportSynthesisFlagshipProgressPolicy> resultList = super.findAll(createQuery);

    return (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;
  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy
    save(ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy) {
    if (reportSynthesisFlagshipProgressPolicy.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressPolicy);
    } else {
      reportSynthesisFlagshipProgressPolicy = super.update(reportSynthesisFlagshipProgressPolicy);
    }


    return reportSynthesisFlagshipProgressPolicy;
  }

}