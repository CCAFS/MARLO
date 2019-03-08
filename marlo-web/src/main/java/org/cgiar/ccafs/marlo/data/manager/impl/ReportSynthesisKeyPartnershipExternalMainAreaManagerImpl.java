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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipExternalMainAreaDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalMainAreaManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipExternalMainAreaManagerImpl implements ReportSynthesisKeyPartnershipExternalMainAreaManager {


  private ReportSynthesisKeyPartnershipExternalMainAreaDAO reportSynthesisKeyPartnershipExternalMainAreaDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipExternalMainAreaManagerImpl(ReportSynthesisKeyPartnershipExternalMainAreaDAO reportSynthesisKeyPartnershipExternalMainAreaDAO) {
    this.reportSynthesisKeyPartnershipExternalMainAreaDAO = reportSynthesisKeyPartnershipExternalMainAreaDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipExternalMainArea(long reportSynthesisKeyPartnershipExternalMainAreaId) {

    reportSynthesisKeyPartnershipExternalMainAreaDAO.deleteReportSynthesisKeyPartnershipExternalMainArea(reportSynthesisKeyPartnershipExternalMainAreaId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipExternalMainArea(long reportSynthesisKeyPartnershipExternalMainAreaID) {

    return reportSynthesisKeyPartnershipExternalMainAreaDAO.existReportSynthesisKeyPartnershipExternalMainArea(reportSynthesisKeyPartnershipExternalMainAreaID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipExternalMainArea> findAll() {

    return reportSynthesisKeyPartnershipExternalMainAreaDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipExternalMainArea getReportSynthesisKeyPartnershipExternalMainAreaById(long reportSynthesisKeyPartnershipExternalMainAreaID) {

    return reportSynthesisKeyPartnershipExternalMainAreaDAO.find(reportSynthesisKeyPartnershipExternalMainAreaID);
  }

  @Override
  public ReportSynthesisKeyPartnershipExternalMainArea saveReportSynthesisKeyPartnershipExternalMainArea(ReportSynthesisKeyPartnershipExternalMainArea reportSynthesisKeyPartnershipExternalMainArea) {

    return reportSynthesisKeyPartnershipExternalMainAreaDAO.save(reportSynthesisKeyPartnershipExternalMainArea);
  }


}
