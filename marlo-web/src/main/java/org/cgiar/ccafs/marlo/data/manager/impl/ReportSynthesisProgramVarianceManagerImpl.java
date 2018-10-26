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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisProgramVarianceDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisProgramVarianceManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisProgramVariance;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisProgramVarianceManagerImpl implements ReportSynthesisProgramVarianceManager {


  private ReportSynthesisProgramVarianceDAO reportSynthesisProgramVarianceDAO;
  // Managers


  @Inject
  public ReportSynthesisProgramVarianceManagerImpl(ReportSynthesisProgramVarianceDAO reportSynthesisProgramVarianceDAO) {
    this.reportSynthesisProgramVarianceDAO = reportSynthesisProgramVarianceDAO;


  }

  @Override
  public void deleteReportSynthesisProgramVariance(long reportSynthesisProgramVarianceId) {

    reportSynthesisProgramVarianceDAO.deleteReportSynthesisProgramVariance(reportSynthesisProgramVarianceId);
  }

  @Override
  public boolean existReportSynthesisProgramVariance(long reportSynthesisProgramVarianceID) {

    return reportSynthesisProgramVarianceDAO.existReportSynthesisProgramVariance(reportSynthesisProgramVarianceID);
  }

  @Override
  public List<ReportSynthesisProgramVariance> findAll() {

    return reportSynthesisProgramVarianceDAO.findAll();

  }

  @Override
  public ReportSynthesisProgramVariance getReportSynthesisProgramVarianceById(long reportSynthesisProgramVarianceID) {

    return reportSynthesisProgramVarianceDAO.find(reportSynthesisProgramVarianceID);
  }

  @Override
  public ReportSynthesisProgramVariance saveReportSynthesisProgramVariance(ReportSynthesisProgramVariance reportSynthesisProgramVariance) {

    return reportSynthesisProgramVarianceDAO.save(reportSynthesisProgramVariance);
  }


}
