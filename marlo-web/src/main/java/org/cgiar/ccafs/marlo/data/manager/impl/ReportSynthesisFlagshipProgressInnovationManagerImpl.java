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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressInnovationManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressInnovationManagerImpl implements ReportSynthesisFlagshipProgressInnovationManager {


  private ReportSynthesisFlagshipProgressInnovationDAO reportSynthesisFlagshipProgressInnovationDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressInnovationManagerImpl(ReportSynthesisFlagshipProgressInnovationDAO reportSynthesisFlagshipProgressInnovationDAO) {
    this.reportSynthesisFlagshipProgressInnovationDAO = reportSynthesisFlagshipProgressInnovationDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationId) {

    reportSynthesisFlagshipProgressInnovationDAO.deleteReportSynthesisFlagshipProgressInnovation(reportSynthesisFlagshipProgressInnovationId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationID) {

    return reportSynthesisFlagshipProgressInnovationDAO.existReportSynthesisFlagshipProgressInnovation(reportSynthesisFlagshipProgressInnovationID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressInnovation> findAll() {

    return reportSynthesisFlagshipProgressInnovationDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation getReportSynthesisFlagshipProgressInnovationById(long reportSynthesisFlagshipProgressInnovationID) {

    return reportSynthesisFlagshipProgressInnovationDAO.find(reportSynthesisFlagshipProgressInnovationID);
  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation saveReportSynthesisFlagshipProgressInnovation(ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation) {

    return reportSynthesisFlagshipProgressInnovationDAO.save(reportSynthesisFlagshipProgressInnovation);
  }


}
