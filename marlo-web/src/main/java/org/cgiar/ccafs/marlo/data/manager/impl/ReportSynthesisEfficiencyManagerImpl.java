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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisEfficiencyDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisEfficiencyManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisEfficiency;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisEfficiencyManagerImpl implements ReportSynthesisEfficiencyManager {


  private ReportSynthesisEfficiencyDAO reportSynthesisEfficiencyDAO;
  // Managers


  @Inject
  public ReportSynthesisEfficiencyManagerImpl(ReportSynthesisEfficiencyDAO reportSynthesisEfficiencyDAO) {
    this.reportSynthesisEfficiencyDAO = reportSynthesisEfficiencyDAO;


  }

  @Override
  public void deleteReportSynthesisEfficiency(long reportSynthesisEfficiencyId) {

    reportSynthesisEfficiencyDAO.deleteReportSynthesisEfficiency(reportSynthesisEfficiencyId);
  }

  @Override
  public boolean existReportSynthesisEfficiency(long reportSynthesisEfficiencyID) {

    return reportSynthesisEfficiencyDAO.existReportSynthesisEfficiency(reportSynthesisEfficiencyID);
  }

  @Override
  public List<ReportSynthesisEfficiency> findAll() {

    return reportSynthesisEfficiencyDAO.findAll();

  }

  @Override
  public ReportSynthesisEfficiency getReportSynthesisEfficiencyById(long reportSynthesisEfficiencyID) {

    return reportSynthesisEfficiencyDAO.find(reportSynthesisEfficiencyID);
  }

  @Override
  public ReportSynthesisEfficiency saveReportSynthesisEfficiency(ReportSynthesisEfficiency reportSynthesisEfficiency) {

    return reportSynthesisEfficiencyDAO.save(reportSynthesisEfficiency);
  }


}
