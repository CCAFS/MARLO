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


import org.cgiar.ccafs.marlo.data.dao.CrpsSiteIntegrationDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpsSiteIntegrationManager;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Christian Garcia
 */
@Named
@Transactional
public class CrpsSiteIntegrationManagerImpl implements CrpsSiteIntegrationManager {

  private static final Logger LOG = LoggerFactory.getLogger(CrpsSiteIntegrationManagerImpl.class);
  private CrpsSiteIntegrationDAO crpsSiteIntegrationDAO;
  // Managers


  @Inject
  public CrpsSiteIntegrationManagerImpl(CrpsSiteIntegrationDAO crpsSiteIntegrationDAO) {
    this.crpsSiteIntegrationDAO = crpsSiteIntegrationDAO;


  }

  @Override
  public void deleteCrpsSiteIntegration(long crpsSiteIntegrationId) {

    crpsSiteIntegrationDAO.deleteCrpsSiteIntegration(crpsSiteIntegrationId);
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
  @Transactional
  public CrpsSiteIntegration saveCrpsSiteIntegration(CrpsSiteIntegration crpsSiteIntegration) {
    LOG.info("=== SAVE CRPS SITE INTEGRATION CALLED ===");
    LOG.info("=== CRPS SITE INTEGRATION DATA: {} ===", crpsSiteIntegration != null ? 
      "ID: " + crpsSiteIntegration.getId() + ", LocElement: " + 
      (crpsSiteIntegration.getLocElement() != null ? crpsSiteIntegration.getLocElement().getIsoAlpha2() : "NULL") : 
      "NULL OBJECT");
    
    CrpsSiteIntegration result = crpsSiteIntegrationDAO.save(crpsSiteIntegration);
    LOG.info("=== SAVE RESULT: {} ===", result != null ? "ID: " + result.getId() : "NULL");
    
    return result;
  }


}
