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


import org.cgiar.ccafs.marlo.data.dao.ShfrmPriorityActionDAO;
import org.cgiar.ccafs.marlo.data.manager.ShfrmPriorityActionManager;
import org.cgiar.ccafs.marlo.data.model.ShfrmPriorityAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ShfrmPriorityActionManagerImpl implements ShfrmPriorityActionManager {


  private ShfrmPriorityActionDAO shfrmPriorityActionDAO;
  // Managers


  @Inject
  public ShfrmPriorityActionManagerImpl(ShfrmPriorityActionDAO shfrmPriorityActionDAO) {
    this.shfrmPriorityActionDAO = shfrmPriorityActionDAO;


  }

  @Override
  public void deleteShfrmPriorityAction(long shfrmPriorityActionId) {

    shfrmPriorityActionDAO.deleteShfrmPriorityAction(shfrmPriorityActionId);
  }

  @Override
  public boolean existShfrmPriorityAction(long shfrmPriorityActionID) {

    return shfrmPriorityActionDAO.existShfrmPriorityAction(shfrmPriorityActionID);
  }

  @Override
  public List<ShfrmPriorityAction> findAll() {

    return shfrmPriorityActionDAO.findAll();

  }

  @Override
  public ShfrmPriorityAction getShfrmPriorityActionById(long shfrmPriorityActionID) {

    return shfrmPriorityActionDAO.find(shfrmPriorityActionID);
  }

  @Override
  public ShfrmPriorityAction saveShfrmPriorityAction(ShfrmPriorityAction shfrmPriorityAction) {

    return shfrmPriorityActionDAO.save(shfrmPriorityAction);
  }


}
