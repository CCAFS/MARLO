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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisNarrativeDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisNarrativeManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisNarrative;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisNarrativeManagerImpl implements ReportSynthesisNarrativeManager {


  private ReportSynthesisNarrativeDAO reportSynthesisNarrativeDAO;
  // Managers


  @Inject
  public ReportSynthesisNarrativeManagerImpl(ReportSynthesisNarrativeDAO reportSynthesisNarrativeDAO) {
    this.reportSynthesisNarrativeDAO = reportSynthesisNarrativeDAO;


  }

  @Override
  public void deleteReportSynthesisNarrative(long reportSynthesisNarrativeId) {

    reportSynthesisNarrativeDAO.deleteReportSynthesisNarrative(reportSynthesisNarrativeId);
  }

  @Override
  public boolean existReportSynthesisNarrative(long reportSynthesisNarrativeID) {

    return reportSynthesisNarrativeDAO.existReportSynthesisNarrative(reportSynthesisNarrativeID);
  }

  @Override
  public List<ReportSynthesisNarrative> findAll() {

    return reportSynthesisNarrativeDAO.findAll();

  }

  @Override
  public ReportSynthesisNarrative getReportSynthesisNarrativeById(long reportSynthesisNarrativeID) {

    return reportSynthesisNarrativeDAO.find(reportSynthesisNarrativeID);
  }

  @Override
  public ReportSynthesisNarrative saveReportSynthesisNarrative(ReportSynthesisNarrative reportSynthesisNarrative) {

    return reportSynthesisNarrativeDAO.save(reportSynthesisNarrative);
  }


}
