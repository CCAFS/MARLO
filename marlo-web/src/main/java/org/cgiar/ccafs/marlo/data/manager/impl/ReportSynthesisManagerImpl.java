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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisManagerImpl implements ReportSynthesisManager {


  private ReportSynthesisDAO reportSynthesisDAO;
  // Managers


  @Inject
  public ReportSynthesisManagerImpl(ReportSynthesisDAO reportSynthesisDAO) {
    this.reportSynthesisDAO = reportSynthesisDAO;
  }

  @Override
  public void deleteReportSynthesis(long reportSynthesisId) {

    reportSynthesisDAO.deleteReportSynthesis(reportSynthesisId);
  }

  @Override
  public boolean existReportSynthesis(long reportSynthesisID) {

    return reportSynthesisDAO.existReportSynthesis(reportSynthesisID);
  }

  @Override
  public List<ReportSynthesis> findAll() {

    return reportSynthesisDAO.findAll();

  }

  @Override
  public ReportSynthesis findSynthesis(long phaseID, long liaisonInstitutionID) {
    return reportSynthesisDAO.findSynthesis(phaseID, liaisonInstitutionID);
  }


  @Override
  public ReportSynthesis getReportSynthesisById(long reportSynthesisID) {

    return reportSynthesisDAO.find(reportSynthesisID);
  }

  @Override
  public ReportSynthesis save(ReportSynthesis reportSynthesis, String sectionName, List<String> relationsName,
    Phase phase) {
    return reportSynthesisDAO.save(reportSynthesis, sectionName, relationsName, phase);
  }

  @Override
  public ReportSynthesis saveReportSynthesis(ReportSynthesis reportSynthesis) {

    return reportSynthesisDAO.save(reportSynthesis);
  }


}
