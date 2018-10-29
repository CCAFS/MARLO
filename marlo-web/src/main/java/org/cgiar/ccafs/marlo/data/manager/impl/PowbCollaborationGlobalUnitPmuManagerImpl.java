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


import org.cgiar.ccafs.marlo.data.dao.PowbCollaborationGlobalUnitPmuDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbCollaborationGlobalUnitPmuManager;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnitPmu;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PowbCollaborationGlobalUnitPmuManagerImpl implements PowbCollaborationGlobalUnitPmuManager {


  private PowbCollaborationGlobalUnitPmuDAO powbCollaborationGlobalUnitPmuDAO;
  // Managers


  @Inject
  public PowbCollaborationGlobalUnitPmuManagerImpl(PowbCollaborationGlobalUnitPmuDAO powbCollaborationGlobalUnitPmuDAO) {
    this.powbCollaborationGlobalUnitPmuDAO = powbCollaborationGlobalUnitPmuDAO;


  }

  @Override
  public void deletePowbCollaborationGlobalUnitPmu(long powbCollaborationGlobalUnitPmuId) {

    powbCollaborationGlobalUnitPmuDAO.deletePowbCollaborationGlobalUnitPmu(powbCollaborationGlobalUnitPmuId);
  }

  @Override
  public boolean existPowbCollaborationGlobalUnitPmu(long powbCollaborationGlobalUnitPmuID) {

    return powbCollaborationGlobalUnitPmuDAO.existPowbCollaborationGlobalUnitPmu(powbCollaborationGlobalUnitPmuID);
  }

  @Override
  public List<PowbCollaborationGlobalUnitPmu> findAll() {

    return powbCollaborationGlobalUnitPmuDAO.findAll();

  }

  @Override
  public PowbCollaborationGlobalUnitPmu getPowbCollaborationGlobalUnitPmuById(long powbCollaborationGlobalUnitPmuID) {

    return powbCollaborationGlobalUnitPmuDAO.find(powbCollaborationGlobalUnitPmuID);
  }

  @Override
  public PowbCollaborationGlobalUnitPmu savePowbCollaborationGlobalUnitPmu(PowbCollaborationGlobalUnitPmu powbCollaborationGlobalUnitPmu) {

    return powbCollaborationGlobalUnitPmuDAO.save(powbCollaborationGlobalUnitPmu);
  }


}
