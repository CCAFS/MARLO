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


import org.cgiar.ccafs.marlo.data.dao.RepIndPolicyInvestimentTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndPolicyInvestimentTypeManagerImpl implements RepIndPolicyInvestimentTypeManager {


  private RepIndPolicyInvestimentTypeDAO repIndPolicyInvestimentTypeDAO;
  // Managers


  @Inject
  public RepIndPolicyInvestimentTypeManagerImpl(RepIndPolicyInvestimentTypeDAO repIndPolicyInvestimentTypeDAO) {
    this.repIndPolicyInvestimentTypeDAO = repIndPolicyInvestimentTypeDAO;


  }

  @Override
  public void deleteRepIndPolicyInvestimentType(long repIndPolicyInvestimentTypeId) {

    repIndPolicyInvestimentTypeDAO.deleteRepIndPolicyInvestimentType(repIndPolicyInvestimentTypeId);
  }

  @Override
  public boolean existRepIndPolicyInvestimentType(long repIndPolicyInvestimentTypeID) {

    return repIndPolicyInvestimentTypeDAO.existRepIndPolicyInvestimentType(repIndPolicyInvestimentTypeID);
  }

  @Override
  public List<RepIndPolicyInvestimentType> findAll() {

    return repIndPolicyInvestimentTypeDAO.findAll();

  }

  @Override
  public RepIndPolicyInvestimentType getRepIndPolicyInvestimentTypeById(long repIndPolicyInvestimentTypeID) {

    return repIndPolicyInvestimentTypeDAO.find(repIndPolicyInvestimentTypeID);
  }

  @Override
  public RepIndPolicyInvestimentType saveRepIndPolicyInvestimentType(RepIndPolicyInvestimentType repIndPolicyInvestimentType) {

    return repIndPolicyInvestimentTypeDAO.save(repIndPolicyInvestimentType);
  }


}
