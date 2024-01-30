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

import org.cgiar.ccafs.marlo.data.dao.ShfrmSubActionDAO;
import org.cgiar.ccafs.marlo.data.model.ShfrmSubAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ShfrmSubActionMySQLDAO extends AbstractMarloDAO<ShfrmSubAction, Long> implements ShfrmSubActionDAO {


  @Inject
  public ShfrmSubActionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteShfrmSubAction(long shfrmSubActionId) {
    ShfrmSubAction shfrmSubAction = this.find(shfrmSubActionId);
    shfrmSubAction.setActive(false);
    this.update(shfrmSubAction);
  }

  @Override
  public boolean existShfrmSubAction(long shfrmSubActionID) {
    ShfrmSubAction shfrmSubAction = this.find(shfrmSubActionID);
    if (shfrmSubAction == null) {
      return false;
    }
    return true;

  }

  @Override
  public ShfrmSubAction find(long id) {
    return super.find(ShfrmSubAction.class, id);

  }

  @Override
  public List<ShfrmSubAction> findAll() {
    String query = "from " + ShfrmSubAction.class.getName() + " where is_active=1";
    List<ShfrmSubAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ShfrmSubAction save(ShfrmSubAction shfrmSubAction) {
    if (shfrmSubAction.getId() == null) {
      super.saveEntity(shfrmSubAction);
    } else {
      shfrmSubAction = super.update(shfrmSubAction);
    }


    return shfrmSubAction;
  }


}