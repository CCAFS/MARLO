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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCuttingDimensionInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionInnovationManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrossCuttingDimensionInnovationManagerImpl implements ReportSynthesisCrossCuttingDimensionInnovationManager {


  private ReportSynthesisCrossCuttingDimensionInnovationDAO reportSynthesisCrossCuttingDimensionInnovationDAO;
  // Managers


  @Inject
  public ReportSynthesisCrossCuttingDimensionInnovationManagerImpl(ReportSynthesisCrossCuttingDimensionInnovationDAO reportSynthesisCrossCuttingDimensionInnovationDAO) {
    this.reportSynthesisCrossCuttingDimensionInnovationDAO = reportSynthesisCrossCuttingDimensionInnovationDAO;


  }

  @Override
  public void deleteReportSynthesisCrossCuttingDimensionInnovation(long reportSynthesisCrossCuttingDimensionInnovationId) {

    reportSynthesisCrossCuttingDimensionInnovationDAO.deleteReportSynthesisCrossCuttingDimensionInnovation(reportSynthesisCrossCuttingDimensionInnovationId);
  }

  @Override
  public boolean existReportSynthesisCrossCuttingDimensionInnovation(long reportSynthesisCrossCuttingDimensionInnovationID) {

    return reportSynthesisCrossCuttingDimensionInnovationDAO.existReportSynthesisCrossCuttingDimensionInnovation(reportSynthesisCrossCuttingDimensionInnovationID);
  }

  @Override
  public List<ReportSynthesisCrossCuttingDimensionInnovation> findAll() {

    return reportSynthesisCrossCuttingDimensionInnovationDAO.findAll();

  }

  @Override
  public ReportSynthesisCrossCuttingDimensionInnovation getReportSynthesisCrossCuttingDimensionInnovationById(long reportSynthesisCrossCuttingDimensionInnovationID) {

    return reportSynthesisCrossCuttingDimensionInnovationDAO.find(reportSynthesisCrossCuttingDimensionInnovationID);
  }

  @Override
  public ReportSynthesisCrossCuttingDimensionInnovation saveReportSynthesisCrossCuttingDimensionInnovation(ReportSynthesisCrossCuttingDimensionInnovation reportSynthesisCrossCuttingDimensionInnovation) {

    return reportSynthesisCrossCuttingDimensionInnovationDAO.save(reportSynthesisCrossCuttingDimensionInnovation);
  }


}
