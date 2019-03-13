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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressDeliverableDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressDeliverableManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressDeliverableManagerImpl implements ReportSynthesisFlagshipProgressDeliverableManager {


  private ReportSynthesisFlagshipProgressDeliverableDAO reportSynthesisFlagshipProgressDeliverableDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressDeliverableManagerImpl(ReportSynthesisFlagshipProgressDeliverableDAO reportSynthesisFlagshipProgressDeliverableDAO) {
    this.reportSynthesisFlagshipProgressDeliverableDAO = reportSynthesisFlagshipProgressDeliverableDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressDeliverable(long reportSynthesisFlagshipProgressDeliverableId) {

    reportSynthesisFlagshipProgressDeliverableDAO.deleteReportSynthesisFlagshipProgressDeliverable(reportSynthesisFlagshipProgressDeliverableId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressDeliverable(long reportSynthesisFlagshipProgressDeliverableID) {

    return reportSynthesisFlagshipProgressDeliverableDAO.existReportSynthesisFlagshipProgressDeliverable(reportSynthesisFlagshipProgressDeliverableID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressDeliverable> findAll() {

    return reportSynthesisFlagshipProgressDeliverableDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressDeliverable getReportSynthesisFlagshipProgressDeliverableById(long reportSynthesisFlagshipProgressDeliverableID) {

    return reportSynthesisFlagshipProgressDeliverableDAO.find(reportSynthesisFlagshipProgressDeliverableID);
  }

  @Override
  public ReportSynthesisFlagshipProgressDeliverable saveReportSynthesisFlagshipProgressDeliverable(ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable) {

    return reportSynthesisFlagshipProgressDeliverableDAO.save(reportSynthesisFlagshipProgressDeliverable);
  }


}
