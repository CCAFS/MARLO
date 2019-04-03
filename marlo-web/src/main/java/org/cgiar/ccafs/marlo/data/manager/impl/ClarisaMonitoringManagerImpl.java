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


import org.cgiar.ccafs.marlo.data.dao.ClarisaMonitoringDAO;
import org.cgiar.ccafs.marlo.data.manager.ClarisaMonitoringManager;
import org.cgiar.ccafs.marlo.data.model.ClarisaMonitoring;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ClarisaMonitoringManagerImpl implements ClarisaMonitoringManager {


  private ClarisaMonitoringDAO clarisaMonitoringDAO;
  // Managers


  @Inject
  public ClarisaMonitoringManagerImpl(ClarisaMonitoringDAO clarisaMonitoringDAO) {
    this.clarisaMonitoringDAO = clarisaMonitoringDAO;


  }

  @Override
  public void deleteClarisaMonitoring(long clarisaMonitoringId) {

    clarisaMonitoringDAO.deleteClarisaMonitoring(clarisaMonitoringId);
  }

  @Override
  public boolean existClarisaMonitoring(long clarisaMonitoringID) {

    return clarisaMonitoringDAO.existClarisaMonitoring(clarisaMonitoringID);
  }

  @Override
  public List<ClarisaMonitoring> findAll() {

    return clarisaMonitoringDAO.findAll();

  }

  @Override
  public ClarisaMonitoring getClarisaMonitoringById(long clarisaMonitoringID) {

    return clarisaMonitoringDAO.find(clarisaMonitoringID);
  }

  @Override
  public ClarisaMonitoring saveClarisaMonitoring(ClarisaMonitoring clarisaMonitoring) {

    return clarisaMonitoringDAO.save(clarisaMonitoring);
  }


}
