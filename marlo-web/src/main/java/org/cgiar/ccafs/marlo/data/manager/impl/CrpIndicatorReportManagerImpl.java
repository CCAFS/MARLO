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


import org.cgiar.ccafs.marlo.data.dao.CrpIndicatorReportDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpIndicatorReportManager;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorReport;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CrpIndicatorReportManagerImpl implements CrpIndicatorReportManager {


  private CrpIndicatorReportDAO crpIndicatorReportDAO;
  // Managers


  @Inject
  public CrpIndicatorReportManagerImpl(CrpIndicatorReportDAO crpIndicatorReportDAO) {
    this.crpIndicatorReportDAO = crpIndicatorReportDAO;


  }

  @Override
  public void deleteCrpIndicatorReport(long crpIndicatorReportId) {

    crpIndicatorReportDAO.deleteCrpIndicatorReport(crpIndicatorReportId);
  }

  @Override
  public boolean existCrpIndicatorReport(long crpIndicatorReportID) {

    return crpIndicatorReportDAO.existCrpIndicatorReport(crpIndicatorReportID);
  }

  @Override
  public List<CrpIndicatorReport> findAll() {

    return crpIndicatorReportDAO.findAll();

  }

  @Override
  public CrpIndicatorReport getCrpIndicatorReportById(long crpIndicatorReportID) {

    return crpIndicatorReportDAO.find(crpIndicatorReportID);
  }

  @Override
  public List<CrpIndicatorReport> getIndicatorReportsList(long leader, int year) {
    return crpIndicatorReportDAO.getIndicatorReportsList(leader, year);
  }

  @Override
  public CrpIndicatorReport saveCrpIndicatorReport(CrpIndicatorReport crpIndicatorReport) {

    return crpIndicatorReportDAO.save(crpIndicatorReport);
  }


}
