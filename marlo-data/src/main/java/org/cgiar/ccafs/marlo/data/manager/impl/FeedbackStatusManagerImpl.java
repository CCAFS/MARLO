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


import org.cgiar.ccafs.marlo.data.dao.FeedbackStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.FeedbackStatusManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class FeedbackStatusManagerImpl implements FeedbackStatusManager {


  private FeedbackStatusDAO feedbackStatusDAO;
  // Managers


  @Inject
  public FeedbackStatusManagerImpl(FeedbackStatusDAO feedbackStatusDAO) {
    this.feedbackStatusDAO = feedbackStatusDAO;


  }

  @Override
  public void deleteFeedbackStatus(long feedbackStatusId) {

    feedbackStatusDAO.deleteFeedbackStatus(feedbackStatusId);
  }

  @Override
  public boolean existFeedbackStatus(long feedbackStatusID) {

    return feedbackStatusDAO.existFeedbackStatus(feedbackStatusID);
  }

  @Override
  public List<FeedbackStatus> findAll() {

    return feedbackStatusDAO.findAll();

  }

  @Override
  public FeedbackStatus getFeedbackStatusById(long feedbackStatusID) {

    return feedbackStatusDAO.find(feedbackStatusID);
  }

  @Override
  public FeedbackStatus saveFeedbackStatus(FeedbackStatus feedbackStatus) {

    return feedbackStatusDAO.save(feedbackStatus);
  }


}
