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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisExternalPartnershipProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipProjectManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipProject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisExternalPartnershipProjectManagerImpl implements ReportSynthesisExternalPartnershipProjectManager {


  private ReportSynthesisExternalPartnershipProjectDAO reportSynthesisExternalPartnershipProjectDAO;
  // Managers


  @Inject
  public ReportSynthesisExternalPartnershipProjectManagerImpl(ReportSynthesisExternalPartnershipProjectDAO reportSynthesisExternalPartnershipProjectDAO) {
    this.reportSynthesisExternalPartnershipProjectDAO = reportSynthesisExternalPartnershipProjectDAO;


  }

  @Override
  public void deleteReportSynthesisExternalPartnershipProject(long reportSynthesisExternalPartnershipProjectId) {

    reportSynthesisExternalPartnershipProjectDAO.deleteReportSynthesisExternalPartnershipProject(reportSynthesisExternalPartnershipProjectId);
  }

  @Override
  public boolean existReportSynthesisExternalPartnershipProject(long reportSynthesisExternalPartnershipProjectID) {

    return reportSynthesisExternalPartnershipProjectDAO.existReportSynthesisExternalPartnershipProject(reportSynthesisExternalPartnershipProjectID);
  }

  @Override
  public List<ReportSynthesisExternalPartnershipProject> findAll() {

    return reportSynthesisExternalPartnershipProjectDAO.findAll();

  }

  @Override
  public ReportSynthesisExternalPartnershipProject getReportSynthesisExternalPartnershipProjectById(long reportSynthesisExternalPartnershipProjectID) {

    return reportSynthesisExternalPartnershipProjectDAO.find(reportSynthesisExternalPartnershipProjectID);
  }

  @Override
  public ReportSynthesisExternalPartnershipProject saveReportSynthesisExternalPartnershipProject(ReportSynthesisExternalPartnershipProject reportSynthesisExternalPartnershipProject) {

    return reportSynthesisExternalPartnershipProjectDAO.save(reportSynthesisExternalPartnershipProject);
  }


}
