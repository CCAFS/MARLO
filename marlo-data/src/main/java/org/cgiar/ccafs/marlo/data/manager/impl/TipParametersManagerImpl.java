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


import org.cgiar.ccafs.marlo.data.dao.TipParametersDAO;
import org.cgiar.ccafs.marlo.data.manager.TipParametersManager;
import org.cgiar.ccafs.marlo.data.model.TipParameters;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class TipParametersManagerImpl implements TipParametersManager {


  private TipParametersDAO tipParametersDAO;
  // Managers


  @Inject
  public TipParametersManagerImpl(TipParametersDAO tipParametersDAO) {
    this.tipParametersDAO = tipParametersDAO;


  }

  @Override
  public void deleteTipParameters(long tipParametersId) {

    tipParametersDAO.deleteTipParameters(tipParametersId);
  }

  @Override
  public boolean existTipParameters(long tipParametersID) {

    return tipParametersDAO.existTipParameters(tipParametersID);
  }

  @Override
  public List<TipParameters> findAll() {

    return tipParametersDAO.findAll();

  }

  @Override
  public TipParameters getTipParametersById(long tipParametersID) {

    return tipParametersDAO.find(tipParametersID);
  }

  @Override
  public TipParameters saveTipParameters(TipParameters tipParameters) {

    return tipParametersDAO.save(tipParameters);
  }


}
