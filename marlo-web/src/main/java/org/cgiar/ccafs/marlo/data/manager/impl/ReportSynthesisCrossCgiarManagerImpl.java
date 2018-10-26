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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCgiarDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrossCgiarManagerImpl implements ReportSynthesisCrossCgiarManager {


  private ReportSynthesisCrossCgiarDAO reportSynthesisCrossCgiarDAO;
  // Managers


  @Inject
  public ReportSynthesisCrossCgiarManagerImpl(ReportSynthesisCrossCgiarDAO reportSynthesisCrossCgiarDAO) {
    this.reportSynthesisCrossCgiarDAO = reportSynthesisCrossCgiarDAO;


  }

  @Override
  public void deleteReportSynthesisCrossCgiar(long reportSynthesisCrossCgiarId) {

    reportSynthesisCrossCgiarDAO.deleteReportSynthesisCrossCgiar(reportSynthesisCrossCgiarId);
  }

  @Override
  public boolean existReportSynthesisCrossCgiar(long reportSynthesisCrossCgiarID) {

    return reportSynthesisCrossCgiarDAO.existReportSynthesisCrossCgiar(reportSynthesisCrossCgiarID);
  }

  @Override
  public List<ReportSynthesisCrossCgiar> findAll() {

    return reportSynthesisCrossCgiarDAO.findAll();

  }

  @Override
  public ReportSynthesisCrossCgiar getReportSynthesisCrossCgiarById(long reportSynthesisCrossCgiarID) {

    return reportSynthesisCrossCgiarDAO.find(reportSynthesisCrossCgiarID);
  }

  @Override
  public ReportSynthesisCrossCgiar saveReportSynthesisCrossCgiar(ReportSynthesisCrossCgiar reportSynthesisCrossCgiar) {

    return reportSynthesisCrossCgiarDAO.save(reportSynthesisCrossCgiar);
  }


}
