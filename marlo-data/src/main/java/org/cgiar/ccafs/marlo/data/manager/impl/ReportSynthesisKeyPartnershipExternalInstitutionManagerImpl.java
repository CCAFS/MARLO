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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipExternalInstitutionDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipExternalInstitutionManagerImpl implements ReportSynthesisKeyPartnershipExternalInstitutionManager {


  private ReportSynthesisKeyPartnershipExternalInstitutionDAO reportSynthesisKeyPartnershipExternalInstitutionDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipExternalInstitutionManagerImpl(ReportSynthesisKeyPartnershipExternalInstitutionDAO reportSynthesisKeyPartnershipExternalInstitutionDAO) {
    this.reportSynthesisKeyPartnershipExternalInstitutionDAO = reportSynthesisKeyPartnershipExternalInstitutionDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipExternalInstitution(long reportSynthesisKeyPartnershipExternalInstitutionId) {

    reportSynthesisKeyPartnershipExternalInstitutionDAO.deleteReportSynthesisKeyPartnershipExternalInstitution(reportSynthesisKeyPartnershipExternalInstitutionId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipExternalInstitution(long reportSynthesisKeyPartnershipExternalInstitutionID) {

    return reportSynthesisKeyPartnershipExternalInstitutionDAO.existReportSynthesisKeyPartnershipExternalInstitution(reportSynthesisKeyPartnershipExternalInstitutionID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipExternalInstitution> findAll() {

    return reportSynthesisKeyPartnershipExternalInstitutionDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipExternalInstitution getReportSynthesisKeyPartnershipExternalInstitutionById(long reportSynthesisKeyPartnershipExternalInstitutionID) {

    return reportSynthesisKeyPartnershipExternalInstitutionDAO.find(reportSynthesisKeyPartnershipExternalInstitutionID);
  }

  @Override
  public ReportSynthesisKeyPartnershipExternalInstitution saveReportSynthesisKeyPartnershipExternalInstitution(ReportSynthesisKeyPartnershipExternalInstitution reportSynthesisKeyPartnershipExternalInstitution) {

    return reportSynthesisKeyPartnershipExternalInstitutionDAO.save(reportSynthesisKeyPartnershipExternalInstitution);
  }


}
