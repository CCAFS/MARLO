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


import org.cgiar.ccafs.marlo.data.dao.PRMSInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.PRMSInnovationManager;
import org.cgiar.ccafs.marlo.data.model.PRMSInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CCAFS
 */
@Named
public class PRMSInnovationManagerImpl implements PRMSInnovationManager {


  private PRMSInnovationDAO pRMSInnovationDAO;
  // Managers


  @Inject
  public PRMSInnovationManagerImpl(PRMSInnovationDAO pRMSInnovationDAO) {
    this.pRMSInnovationDAO = pRMSInnovationDAO;


  }

  @Override
  @Transactional
  public void deletePRMSInnovation(long pRMSInnovationId) {

    pRMSInnovationDAO.deletePRMSInnovation(pRMSInnovationId);
  }

  @Override
  public boolean existPRMSInnovation(long pRMSInnovationID) {

    return pRMSInnovationDAO.existPRMSInnovation(pRMSInnovationID);
  }

  @Override
  public List<PRMSInnovation> findAll() {

    return pRMSInnovationDAO.findAll();

  }

  @Override
  public PRMSInnovation getPRMSInnovationById(long pRMSInnovationID) {

    return pRMSInnovationDAO.find(pRMSInnovationID);
  }

  @Override
  public PRMSInnovation savePRMSInnovation(PRMSInnovation pRMSInnovation) {

    return pRMSInnovationDAO.save(pRMSInnovation);
  }


}
