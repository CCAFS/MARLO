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

import org.cgiar.ccafs.marlo.data.dao.ICenterMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMilestone;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterMilestoneDAO extends AbstractMarloDAO<CenterMilestone, Long> implements ICenterMilestoneDAO {


  @Inject
  public CenterMilestoneDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCenterMilestone(long centerMilestoneId) {
    CenterMilestone centerMilestone = this.find(centerMilestoneId);
    centerMilestone.setActive(false);
    this.save(centerMilestone);
  }

  @Override
  public boolean existCenterMilestone(long centerMilestoneID) {
    CenterMilestone centerMilestone = this.find(centerMilestoneID);
    if (centerMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterMilestone find(long id) {
    return super.find(CenterMilestone.class, id);

  }

  @Override
  public List<CenterMilestone> findAll() {
    String query = "from " + CenterMilestone.class.getName();
    List<CenterMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterMilestone> getCenterMilestonesByUserId(long userId) {
    String query = "from " + CenterMilestone.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterMilestone save(CenterMilestone centerMilestone) {
    if (centerMilestone.getId() == null) {
      super.saveEntity(centerMilestone);
    } else {
      centerMilestone = super.update(centerMilestone);
    }
    return centerMilestone;
  }

  @Override
  public CenterMilestone save(CenterMilestone centerMilestone, String actionName, List<String> relationsName) {
    if (centerMilestone.getId() == null) {
      super.saveEntity(centerMilestone, actionName, relationsName);
    } else {
      centerMilestone = super.update(centerMilestone, actionName, relationsName);
    }
    return centerMilestone;
  }


}