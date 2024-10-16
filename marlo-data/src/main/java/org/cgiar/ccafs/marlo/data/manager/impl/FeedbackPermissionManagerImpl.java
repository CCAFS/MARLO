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


import org.cgiar.ccafs.marlo.data.dao.FeedbackPermissionDAO;
import org.cgiar.ccafs.marlo.data.manager.FeedbackPermissionManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackPermission;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class FeedbackPermissionManagerImpl implements FeedbackPermissionManager {


  private FeedbackPermissionDAO feedbackPermissionDAO;
  // Managers


  @Inject
  public FeedbackPermissionManagerImpl(FeedbackPermissionDAO feedbackPermissionDAO) {
    this.feedbackPermissionDAO = feedbackPermissionDAO;


  }

  @Override
  public void deleteFeedbackPermission(long feedbackPermissionId) {

    feedbackPermissionDAO.deleteFeedbackPermission(feedbackPermissionId);
  }

  @Override
  public boolean existFeedbackPermission(long feedbackPermissionID) {

    return feedbackPermissionDAO.existFeedbackPermission(feedbackPermissionID);
  }

  @Override
  public List<FeedbackPermission> findAll() {

    return feedbackPermissionDAO.findAll();

  }

  @Override
  public FeedbackPermission getFeedbackPermissionById(long feedbackPermissionID) {

    return feedbackPermissionDAO.find(feedbackPermissionID);
  }

  @Override
  public FeedbackPermission saveFeedbackPermission(FeedbackPermission feedbackPermission) {

    return feedbackPermissionDAO.save(feedbackPermission);
  }


}
