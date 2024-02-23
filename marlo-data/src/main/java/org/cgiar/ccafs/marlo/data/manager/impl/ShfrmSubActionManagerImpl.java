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


import org.cgiar.ccafs.marlo.data.dao.ShfrmSubActionDAO;
import org.cgiar.ccafs.marlo.data.manager.ShfrmSubActionManager;
import org.cgiar.ccafs.marlo.data.model.ShfrmSubAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ShfrmSubActionManagerImpl implements ShfrmSubActionManager {


  private ShfrmSubActionDAO shfrmSubActionDAO;
  // Managers


  @Inject
  public ShfrmSubActionManagerImpl(ShfrmSubActionDAO shfrmSubActionDAO) {
    this.shfrmSubActionDAO = shfrmSubActionDAO;


  }

  @Override
  public void deleteShfrmSubAction(long shfrmSubActionId) {

    shfrmSubActionDAO.deleteShfrmSubAction(shfrmSubActionId);
  }

  @Override
  public boolean existShfrmSubAction(long shfrmSubActionID) {

    return shfrmSubActionDAO.existShfrmSubAction(shfrmSubActionID);
  }

  @Override
  public List<ShfrmSubAction> findAll() {

    return shfrmSubActionDAO.findAll();

  }

  @Override
  public ShfrmSubAction getShfrmSubActionById(long shfrmSubActionID) {

    return shfrmSubActionDAO.find(shfrmSubActionID);
  }

  @Override
  public ShfrmSubAction saveShfrmSubAction(ShfrmSubAction shfrmSubAction) {

    return shfrmSubActionDAO.save(shfrmSubAction);
  }


}
