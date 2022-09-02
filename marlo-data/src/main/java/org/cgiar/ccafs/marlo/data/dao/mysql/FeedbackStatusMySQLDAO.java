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

import org.cgiar.ccafs.marlo.data.dao.FeedbackStatusDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class FeedbackStatusMySQLDAO extends AbstractMarloDAO<FeedbackStatus, Long> implements FeedbackStatusDAO {


  @Inject
  public FeedbackStatusMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFeedbackStatus(long feedbackStatusId) {
    FeedbackStatus feedbackStatus = this.find(feedbackStatusId);
    this.delete(feedbackStatus);
  }

  @Override
  public boolean existFeedbackStatus(long feedbackStatusID) {
    FeedbackStatus feedbackStatus = this.find(feedbackStatusID);
    if (feedbackStatus == null) {
      return false;
    }
    return true;

  }

  @Override
  public FeedbackStatus find(long id) {
    return super.find(FeedbackStatus.class, id);

  }

  @Override
  public List<FeedbackStatus> findAll() {
    String query = "from " + FeedbackStatus.class.getName();
    List<FeedbackStatus> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public FeedbackStatus save(FeedbackStatus feedbackStatus) {
    if (feedbackStatus.getId() == null) {
      super.saveEntity(feedbackStatus);
    } else {
      feedbackStatus = super.update(feedbackStatus);
    }

    return feedbackStatus;
  }


}