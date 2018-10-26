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


import org.cgiar.ccafs.marlo.data.dao.RepIndFillingTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndFillingTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndFillingType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndFillingTypeManagerImpl implements RepIndFillingTypeManager {


  private RepIndFillingTypeDAO repIndFillingTypeDAO;
  // Managers


  @Inject
  public RepIndFillingTypeManagerImpl(RepIndFillingTypeDAO repIndFillingTypeDAO) {
    this.repIndFillingTypeDAO = repIndFillingTypeDAO;


  }

  @Override
  public void deleteRepIndFillingType(long repIndFillingTypeId) {

    repIndFillingTypeDAO.deleteRepIndFillingType(repIndFillingTypeId);
  }

  @Override
  public boolean existRepIndFillingType(long repIndFillingTypeID) {

    return repIndFillingTypeDAO.existRepIndFillingType(repIndFillingTypeID);
  }

  @Override
  public List<RepIndFillingType> findAll() {

    return repIndFillingTypeDAO.findAll();

  }

  @Override
  public RepIndFillingType getRepIndFillingTypeById(long repIndFillingTypeID) {

    return repIndFillingTypeDAO.find(repIndFillingTypeID);
  }

  @Override
  public RepIndFillingType saveRepIndFillingType(RepIndFillingType repIndFillingType) {

    return repIndFillingTypeDAO.save(repIndFillingType);
  }


}
