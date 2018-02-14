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


import org.cgiar.ccafs.marlo.data.dao.PowbExpenditureAreasDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbExpenditureAreasManagerImpl implements PowbExpenditureAreasManager {


  private PowbExpenditureAreasDAO powbExpenditureAreasDAO;
  // Managers


  @Inject
  public PowbExpenditureAreasManagerImpl(PowbExpenditureAreasDAO powbExpenditureAreasDAO) {
    this.powbExpenditureAreasDAO = powbExpenditureAreasDAO;


  }

  @Override
  public void deletePowbExpenditureAreas(long powbExpenditureAreasId) {

    powbExpenditureAreasDAO.deletePowbExpenditureAreas(powbExpenditureAreasId);
  }

  @Override
  public boolean existPowbExpenditureAreas(long powbExpenditureAreasID) {

    return powbExpenditureAreasDAO.existPowbExpenditureAreas(powbExpenditureAreasID);
  }

  @Override
  public List<PowbExpenditureAreas> findAll() {

    return powbExpenditureAreasDAO.findAll();

  }

  @Override
  public PowbExpenditureAreas getPowbExpenditureAreasById(long powbExpenditureAreasID) {

    return powbExpenditureAreasDAO.find(powbExpenditureAreasID);
  }

  @Override
  public PowbExpenditureAreas savePowbExpenditureAreas(PowbExpenditureAreas powbExpenditureAreas) {

    return powbExpenditureAreasDAO.save(powbExpenditureAreas);
  }


}
