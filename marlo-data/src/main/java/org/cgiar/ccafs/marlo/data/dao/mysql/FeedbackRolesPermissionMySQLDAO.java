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

import org.cgiar.ccafs.marlo.data.dao.FeedbackRolesPermissionDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackRolesPermission;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FeedbackRolesPermissionMySQLDAO extends AbstractMarloDAO<FeedbackRolesPermission, Long>
  implements FeedbackRolesPermissionDAO {


  @Inject
  public FeedbackRolesPermissionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFeedbackRolesPermission(long feedbackRolesPermissionId) {
    FeedbackRolesPermission feedbackRolesPermission = this.find(feedbackRolesPermissionId);
    this.delete(feedbackRolesPermission);
  }

  @Override
  public boolean existFeedbackRolesPermission(long feedbackRolesPermissionID) {
    FeedbackRolesPermission feedbackRolesPermission = this.find(feedbackRolesPermissionID);
    if (feedbackRolesPermission == null) {
      return false;
    }
    return true;

  }

  @Override
  public FeedbackRolesPermission find(long id) {
    return super.find(FeedbackRolesPermission.class, id);

  }

  @Override
  public List<FeedbackRolesPermission> findAll() {
    String query = "from " + FeedbackRolesPermission.class.getName();
    List<FeedbackRolesPermission> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public FeedbackRolesPermission save(FeedbackRolesPermission feedbackRolesPermission) {
    if (feedbackRolesPermission.getId() == null) {
      super.saveEntity(feedbackRolesPermission);
    } else {
      feedbackRolesPermission = super.update(feedbackRolesPermission);
    }


    return feedbackRolesPermission;
  }


}