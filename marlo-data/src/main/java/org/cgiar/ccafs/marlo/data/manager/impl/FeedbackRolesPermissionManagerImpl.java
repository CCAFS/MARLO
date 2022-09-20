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


import org.cgiar.ccafs.marlo.data.dao.FeedbackRolesPermissionDAO;
import org.cgiar.ccafs.marlo.data.manager.FeedbackRolesPermissionManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackRolesPermission;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class FeedbackRolesPermissionManagerImpl implements FeedbackRolesPermissionManager {


  private FeedbackRolesPermissionDAO feedbackRolesPermissionDAO;
  // Managers


  @Inject
  public FeedbackRolesPermissionManagerImpl(FeedbackRolesPermissionDAO feedbackRolesPermissionDAO) {
    this.feedbackRolesPermissionDAO = feedbackRolesPermissionDAO;


  }

  @Override
  public void deleteFeedbackRolesPermission(long feedbackRolesPermissionId) {

    feedbackRolesPermissionDAO.deleteFeedbackRolesPermission(feedbackRolesPermissionId);
  }

  @Override
  public boolean existFeedbackRolesPermission(long feedbackRolesPermissionID) {

    return feedbackRolesPermissionDAO.existFeedbackRolesPermission(feedbackRolesPermissionID);
  }

  @Override
  public List<FeedbackRolesPermission> findAll() {

    return feedbackRolesPermissionDAO.findAll();

  }

  @Override
  public FeedbackRolesPermission getFeedbackRolesPermissionById(long feedbackRolesPermissionID) {

    return feedbackRolesPermissionDAO.find(feedbackRolesPermissionID);
  }

  @Override
  public FeedbackRolesPermission saveFeedbackRolesPermission(FeedbackRolesPermission feedbackRolesPermission) {

    return feedbackRolesPermissionDAO.save(feedbackRolesPermission);
  }


}
