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


import org.cgiar.ccafs.marlo.data.dao.PowbCollaborationRegionDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbCollaborationRegionManager;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationRegion;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbCollaborationRegionManagerImpl implements PowbCollaborationRegionManager {


  private PowbCollaborationRegionDAO powbCollaborationRegionDAO;
  // Managers


  @Inject
  public PowbCollaborationRegionManagerImpl(PowbCollaborationRegionDAO powbCollaborationRegionDAO) {
    this.powbCollaborationRegionDAO = powbCollaborationRegionDAO;


  }

  @Override
  public void deletePowbCollaborationRegion(long powbCollaborationRegionId) {

    powbCollaborationRegionDAO.deletePowbCollaborationRegion(powbCollaborationRegionId);
  }

  @Override
  public boolean existPowbCollaborationRegion(long powbCollaborationRegionID) {

    return powbCollaborationRegionDAO.existPowbCollaborationRegion(powbCollaborationRegionID);
  }

  @Override
  public List<PowbCollaborationRegion> findAll() {

    return powbCollaborationRegionDAO.findAll();

  }

  @Override
  public PowbCollaborationRegion getPowbCollaborationRegionById(long powbCollaborationRegionID) {

    return powbCollaborationRegionDAO.find(powbCollaborationRegionID);
  }

  @Override
  public PowbCollaborationRegion savePowbCollaborationRegion(PowbCollaborationRegion powbCollaborationRegion) {

    return powbCollaborationRegionDAO.save(powbCollaborationRegion);
  }


}
