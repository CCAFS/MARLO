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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ShfrmPriorityActionDAO;
import org.cgiar.ccafs.marlo.data.model.ShfrmPriorityAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ShfrmPriorityActionMySQLDAO extends AbstractMarloDAO<ShfrmPriorityAction, Long>
  implements ShfrmPriorityActionDAO {


  @Inject
  public ShfrmPriorityActionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteShfrmPriorityAction(long shfrmPriorityActionId) {
    ShfrmPriorityAction shfrmPriorityAction = this.find(shfrmPriorityActionId);
    /*
     * shfrmPriorityAction.setActive(false);
     * this.update(shfrmPriorityAction);
     */
    this.delete(shfrmPriorityAction);
  }

  @Override
  public boolean existShfrmPriorityAction(long shfrmPriorityActionID) {
    ShfrmPriorityAction shfrmPriorityAction = this.find(shfrmPriorityActionID);
    if (shfrmPriorityAction == null) {
      return false;
    }
    return true;

  }

  @Override
  public ShfrmPriorityAction find(long id) {
    return super.find(ShfrmPriorityAction.class, id);

  }

  @Override
  public List<ShfrmPriorityAction> findAll() {
    String query = "from " + ShfrmPriorityAction.class.getName() + " where is_active=1";
    List<ShfrmPriorityAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ShfrmPriorityAction save(ShfrmPriorityAction shfrmPriorityAction) {
    if (shfrmPriorityAction.getId() == null) {
      super.saveEntity(shfrmPriorityAction);
    } else {
      shfrmPriorityAction = super.update(shfrmPriorityAction);
    }


    return shfrmPriorityAction;
  }


}