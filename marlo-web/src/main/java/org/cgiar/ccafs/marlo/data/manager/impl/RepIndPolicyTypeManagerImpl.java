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


import org.cgiar.ccafs.marlo.data.dao.RepIndPolicyTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndPolicyTypeManagerImpl implements RepIndPolicyTypeManager {


  private RepIndPolicyTypeDAO repIndPolicyTypeDAO;
  // Managers


  @Inject
  public RepIndPolicyTypeManagerImpl(RepIndPolicyTypeDAO repIndPolicyTypeDAO) {
    this.repIndPolicyTypeDAO = repIndPolicyTypeDAO;


  }

  @Override
  public void deleteRepIndPolicyType(long repIndPolicyTypeId) {

    repIndPolicyTypeDAO.deleteRepIndPolicyType(repIndPolicyTypeId);
  }

  @Override
  public boolean existRepIndPolicyType(long repIndPolicyTypeID) {

    return repIndPolicyTypeDAO.existRepIndPolicyType(repIndPolicyTypeID);
  }

  @Override
  public List<RepIndPolicyType> findAll() {

    return repIndPolicyTypeDAO.findAll();

  }

  @Override
  public RepIndPolicyType getRepIndPolicyTypeById(long repIndPolicyTypeID) {

    return repIndPolicyTypeDAO.find(repIndPolicyTypeID);
  }

  @Override
  public RepIndPolicyType saveRepIndPolicyType(RepIndPolicyType repIndPolicyType) {

    return repIndPolicyTypeDAO.save(repIndPolicyType);
  }


}
