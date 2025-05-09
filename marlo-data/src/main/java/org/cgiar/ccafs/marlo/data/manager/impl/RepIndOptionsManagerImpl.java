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


import org.cgiar.ccafs.marlo.data.dao.RepIndOptionsDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndOptionsManager;
import org.cgiar.ccafs.marlo.data.model.RepIndOptions;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndOptionsManagerImpl implements RepIndOptionsManager {


  private RepIndOptionsDAO repIndOptionsDAO;
  // Managers


  @Inject
  public RepIndOptionsManagerImpl(RepIndOptionsDAO repIndOptionsDAO) {
    this.repIndOptionsDAO = repIndOptionsDAO;


  }

  @Override
  public void deleteRepIndOptions(long repIndOptionsId) {

    repIndOptionsDAO.deleteRepIndOptions(repIndOptionsId);
  }

  @Override
  public boolean existRepIndOptions(long repIndOptionsID) {

    return repIndOptionsDAO.existRepIndOptions(repIndOptionsID);
  }

  @Override
  public List<RepIndOptions> findAll() {

    return repIndOptionsDAO.findAll();

  }

  @Override
  public RepIndOptions getRepIndOptionsById(long repIndOptionsID) {

    return repIndOptionsDAO.find(repIndOptionsID);
  }

  @Override
  public RepIndOptions saveRepIndOptions(RepIndOptions repIndOptions) {

    return repIndOptionsDAO.save(repIndOptions);
  }


}
