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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicator;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisIndicatorManagerImpl implements ReportSynthesisIndicatorManager {


  private ReportSynthesisIndicatorDAO reportSynthesisIndicatorDAO;
  // Managers


  @Inject
  public ReportSynthesisIndicatorManagerImpl(ReportSynthesisIndicatorDAO reportSynthesisIndicatorDAO) {
    this.reportSynthesisIndicatorDAO = reportSynthesisIndicatorDAO;


  }

  @Override
  public void deleteReportSynthesisIndicator(long reportSynthesisIndicatorId) {

    reportSynthesisIndicatorDAO.deleteReportSynthesisIndicator(reportSynthesisIndicatorId);
  }

  @Override
  public boolean existReportSynthesisIndicator(long reportSynthesisIndicatorID) {

    return reportSynthesisIndicatorDAO.existReportSynthesisIndicator(reportSynthesisIndicatorID);
  }

  @Override
  public List<ReportSynthesisIndicator> findAll() {

    return reportSynthesisIndicatorDAO.findAll();

  }

  @Override
  public List<ReportSynthesisIndicator> getIndicatorsByType(ReportSynthesis reportSynthesis, String indicatorType) {
    return reportSynthesis.getReportSynthesisIndicatorGeneral().getReportSynthesisIndicators().stream()
      .filter(
        si -> si.isActive() && si.getRepIndSynthesisIndicator() != null && si.getRepIndSynthesisIndicator().isMarlo()
          && si.getRepIndSynthesisIndicator().getType().equals(indicatorType))
      .sorted((i1, i2) -> i1.getRepIndSynthesisIndicator().getIndicator()
        .compareTo(i2.getRepIndSynthesisIndicator().getIndicator()))
      .collect(Collectors.toList());
  }

  @Override
  public ReportSynthesisIndicator getReportSynthesisIndicatorById(long reportSynthesisIndicatorID) {

    return reportSynthesisIndicatorDAO.find(reportSynthesisIndicatorID);
  }

  @Override
  public ReportSynthesisIndicator saveReportSynthesisIndicator(ReportSynthesisIndicator reportSynthesisIndicator) {

    return reportSynthesisIndicatorDAO.save(reportSynthesisIndicator);
  }

}
