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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipExternalInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipExternalInstitutionMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisKeyPartnershipExternalInstitution, Long>
  implements ReportSynthesisKeyPartnershipExternalInstitutionDAO {


  @Inject
  public ReportSynthesisKeyPartnershipExternalInstitutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteReportSynthesisKeyPartnershipExternalInstitution(long reportSynthesisKeyPartnershipExternalInstitutionId) {
    ReportSynthesisKeyPartnershipExternalInstitution reportSynthesisKeyPartnershipExternalInstitution =
      this.find(reportSynthesisKeyPartnershipExternalInstitutionId);
    this.delete(reportSynthesisKeyPartnershipExternalInstitution);
  }

  @Override
  public boolean
    existReportSynthesisKeyPartnershipExternalInstitution(long reportSynthesisKeyPartnershipExternalInstitutionID) {
    ReportSynthesisKeyPartnershipExternalInstitution reportSynthesisKeyPartnershipExternalInstitution =
      this.find(reportSynthesisKeyPartnershipExternalInstitutionID);
    if (reportSynthesisKeyPartnershipExternalInstitution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnershipExternalInstitution find(long id) {
    return super.find(ReportSynthesisKeyPartnershipExternalInstitution.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnershipExternalInstitution> findAll() {
    String query = "from " + ReportSynthesisKeyPartnershipExternalInstitution.class.getName();
    List<ReportSynthesisKeyPartnershipExternalInstitution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnershipExternalInstitution
    save(ReportSynthesisKeyPartnershipExternalInstitution reportSynthesisKeyPartnershipExternalInstitution) {
    if (reportSynthesisKeyPartnershipExternalInstitution.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnershipExternalInstitution);
    } else {
      reportSynthesisKeyPartnershipExternalInstitution = super.update(reportSynthesisKeyPartnershipExternalInstitution);
    }


    return reportSynthesisKeyPartnershipExternalInstitution;
  }


}