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


import org.cgiar.ccafs.marlo.data.dao.CrpClusterOfActivityDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpClusterOfActivityManagerImpl implements CrpClusterOfActivityManager {


  private CrpClusterOfActivityDAO crpClusterOfActivityDAO;
  // Managers


  @Inject
  public CrpClusterOfActivityManagerImpl(CrpClusterOfActivityDAO crpClusterOfActivityDAO) {
    this.crpClusterOfActivityDAO = crpClusterOfActivityDAO;


  }

  @Override
  public boolean deleteCrpClusterOfActivity(long crpClusterOfActivityId) {

    return crpClusterOfActivityDAO.deleteCrpClusterOfActivity(crpClusterOfActivityId);
  }

  @Override
  public boolean existCrpClusterOfActivity(long crpClusterOfActivityID) {

    return crpClusterOfActivityDAO.existCrpClusterOfActivity(crpClusterOfActivityID);
  }

  @Override
  public List<CrpClusterOfActivity> findAll() {

    return crpClusterOfActivityDAO.findAll();

  }

  @Override
  public CrpClusterOfActivity getCrpClusterOfActivityById(long crpClusterOfActivityID) {

    return crpClusterOfActivityDAO.find(crpClusterOfActivityID);
  }

  @Override
  public Long saveCrpClusterOfActivity(CrpClusterOfActivity crpClusterOfActivity) {

    return crpClusterOfActivityDAO.save(crpClusterOfActivity);
  }


}
