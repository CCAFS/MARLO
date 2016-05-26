/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.CrpsSiteIntegrationDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpsSiteIntegrationManager;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpsSiteIntegrationManagerImpl implements CrpsSiteIntegrationManager {


  private CrpsSiteIntegrationDAO crpsSiteIntegrationDAO;
  // Managers


  @Inject
  public CrpsSiteIntegrationManagerImpl(CrpsSiteIntegrationDAO crpsSiteIntegrationDAO) {
    this.crpsSiteIntegrationDAO = crpsSiteIntegrationDAO;


  }

  @Override
  public boolean deleteCrpsSiteIntegration(long crpsSiteIntegrationId) {

    return crpsSiteIntegrationDAO.deleteCrpsSiteIntegration(crpsSiteIntegrationId);
  }

  @Override
  public boolean existCrpsSiteIntegration(long crpsSiteIntegrationID) {

    return crpsSiteIntegrationDAO.existCrpsSiteIntegration(crpsSiteIntegrationID);
  }

  @Override
  public List<CrpsSiteIntegration> findAll() {

    return crpsSiteIntegrationDAO.findAll();

  }

  @Override
  public CrpsSiteIntegration getCrpsSiteIntegrationById(long crpsSiteIntegrationID) {

    return crpsSiteIntegrationDAO.find(crpsSiteIntegrationID);
  }

  @Override
  public long saveCrpsSiteIntegration(CrpsSiteIntegration crpsSiteIntegration) {

    return crpsSiteIntegrationDAO.save(crpsSiteIntegration);
  }


}
