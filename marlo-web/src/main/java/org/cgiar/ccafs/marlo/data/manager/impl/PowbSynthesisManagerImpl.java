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


import org.cgiar.ccafs.marlo.data.dao.PowbSynthesisDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbSynthesisManagerImpl implements PowbSynthesisManager {


  private PowbSynthesisDAO powbSynthesisDAO;
  // Managers


  @Inject
  public PowbSynthesisManagerImpl(PowbSynthesisDAO powbSynthesisDAO) {
    this.powbSynthesisDAO = powbSynthesisDAO;


  }

  @Override
  public void deletePowbSynthesis(long powbSynthesisId) {

    powbSynthesisDAO.deletePowbSynthesis(powbSynthesisId);
  }

  @Override
  public boolean existPowbSynthesis(long powbSynthesisID) {

    return powbSynthesisDAO.existPowbSynthesis(powbSynthesisID);
  }

  @Override
  public List<PowbSynthesis> findAll() {

    return powbSynthesisDAO.findAll();

  }

  @Override
  public PowbSynthesis getPowbSynthesisById(long powbSynthesisID) {

    return powbSynthesisDAO.find(powbSynthesisID);
  }

  @Override
  public PowbSynthesis savePowbSynthesis(PowbSynthesis powbSynthesis) {

    return powbSynthesisDAO.save(powbSynthesis);
  }


}
