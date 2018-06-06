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


import org.cgiar.ccafs.marlo.data.dao.RepIndRegionDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndRegionManagerImpl implements RepIndRegionManager {


  private RepIndRegionDAO repIndRegionDAO;
  // Managers


  @Inject
  public RepIndRegionManagerImpl(RepIndRegionDAO repIndRegionDAO) {
    this.repIndRegionDAO = repIndRegionDAO;


  }

  @Override
  public void deleteRepIndRegion(long repIndRegionId) {

    repIndRegionDAO.deleteRepIndRegion(repIndRegionId);
  }

  @Override
  public boolean existRepIndRegion(long repIndRegionID) {

    return repIndRegionDAO.existRepIndRegion(repIndRegionID);
  }

  @Override
  public List<RepIndRegion> findAll() {

    return repIndRegionDAO.findAll();

  }

  @Override
  public RepIndRegion getRepIndRegionById(long repIndRegionID) {

    return repIndRegionDAO.find(repIndRegionID);
  }

  @Override
  public RepIndRegion saveRepIndRegion(RepIndRegion repIndRegion) {

    return repIndRegionDAO.save(repIndRegion);
  }


}
