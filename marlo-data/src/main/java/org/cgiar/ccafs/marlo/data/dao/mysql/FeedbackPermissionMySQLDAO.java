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

import org.cgiar.ccafs.marlo.data.dao.FeedbackPermissionDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackPermission;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FeedbackPermissionMySQLDAO extends AbstractMarloDAO<FeedbackPermission, Long>
  implements FeedbackPermissionDAO {


  @Inject
  public FeedbackPermissionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFeedbackPermission(long feedbackPermissionId) {
    FeedbackPermission feedbackPermission = this.find(feedbackPermissionId);
    this.delete(feedbackPermission);
  }

  @Override
  public boolean existFeedbackPermission(long feedbackPermissionID) {
    FeedbackPermission feedbackPermission = this.find(feedbackPermissionID);
    if (feedbackPermission == null) {
      return false;
    }
    return true;

  }

  @Override
  public FeedbackPermission find(long id) {
    return super.find(FeedbackPermission.class, id);

  }

  @Override
  public List<FeedbackPermission> findAll() {
    String query = "from " + FeedbackPermission.class.getName();
    List<FeedbackPermission> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();

  }

  @Override
  public FeedbackPermission save(FeedbackPermission feedbackPermission) {
    if (feedbackPermission.getId() == null) {
      super.saveEntity(feedbackPermission);
    } else {
      feedbackPermission = super.update(feedbackPermission);
    }

    return feedbackPermission;
  }


}