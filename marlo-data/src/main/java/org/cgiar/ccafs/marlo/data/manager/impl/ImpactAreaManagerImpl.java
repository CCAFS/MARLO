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


import org.cgiar.ccafs.marlo.data.dao.ImpactAreaDAO;
import org.cgiar.ccafs.marlo.data.manager.ImpactAreaManager;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ImpactAreaManagerImpl implements ImpactAreaManager {


  private ImpactAreaDAO impactAreaDAO;
  // Managers


  @Inject
  public ImpactAreaManagerImpl(ImpactAreaDAO impactAreaDAO) {
    this.impactAreaDAO = impactAreaDAO;


  }

  @Override
  public void deleteImpactArea(long impactAreaId) {

    impactAreaDAO.deleteImpactArea(impactAreaId);
  }

  @Override
  public boolean existImpactArea(long impactAreaID) {

    return impactAreaDAO.existImpactArea(impactAreaID);
  }

  @Override
  public List<ImpactArea> findAll() {

    return impactAreaDAO.findAll();

  }

  @Override
  public ImpactArea getImpactAreaById(long impactAreaID) {

    return impactAreaDAO.find(impactAreaID);
  }

  @Override
  public ImpactArea saveImpactArea(ImpactArea impactArea) {

    return impactAreaDAO.save(impactArea);
  }


}
