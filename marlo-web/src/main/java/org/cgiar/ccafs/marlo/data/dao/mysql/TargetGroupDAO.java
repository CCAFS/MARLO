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

import org.cgiar.ccafs.marlo.data.dao.ITargetGroupDAO;
import org.cgiar.ccafs.marlo.data.model.TargetGroup;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class TargetGroupDAO extends AbstractMarloDAO<TargetGroup, Long> implements ITargetGroupDAO {


  @Inject
  public TargetGroupDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteTargetGroup(long targetGroupId) {
    final TargetGroup targetGroup = this.find(targetGroupId);
    // targetGroup.setActive(false);
    this.save(targetGroup);
  }

  @Override
  public boolean existTargetGroup(long targetGroupID) {
    final TargetGroup targetGroup = this.find(targetGroupID);
    if (targetGroup == null) {
      return false;
    }
    return true;

  }

  @Override
  public TargetGroup find(long id) {
    return super.find(TargetGroup.class, id);

  }

  @Override
  public List<TargetGroup> findAll() {
    final String query = "from " + TargetGroup.class.getName();
    final List<TargetGroup> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<TargetGroup> getTargetGroupsByUserId(long userId) {
    final String query = "from " + TargetGroup.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public TargetGroup save(TargetGroup targetGroup) {
    if (targetGroup.getId() == null) {
      super.saveEntity(targetGroup);
    } else {
      super.update(targetGroup);
    }
    return targetGroup;
  }

  @Override
  public TargetGroup save(TargetGroup targetGroup, String actionName, List<String> relationsName) {
    if (targetGroup.getId() == null) {
      super.saveEntity(targetGroup, actionName, relationsName);
    } else {
      super.update(targetGroup, actionName, relationsName);
    }
    return targetGroup;
  }


}