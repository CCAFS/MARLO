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


import org.cgiar.ccafs.marlo.data.dao.ActivityTitleDAO;
import org.cgiar.ccafs.marlo.data.manager.ActivityTitleManager;
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ActivityTitleManagerImpl implements ActivityTitleManager {


  private ActivityTitleDAO activityTitleDAO;
  // Managers


  @Inject
  public ActivityTitleManagerImpl(ActivityTitleDAO activityTitleDAO) {
    this.activityTitleDAO = activityTitleDAO;


  }

  @Override
  public void deleteActivityTitle(long activityTitleId) {

    activityTitleDAO.deleteActivityTitle(activityTitleId);
  }

  @Override
  public boolean existActivityTitle(long activityTitleID) {

    return activityTitleDAO.existActivityTitle(activityTitleID);
  }

  @Override
  public List<ActivityTitle> findAll() {

    return activityTitleDAO.findAll();

  }

  @Override
  public List<ActivityTitle> findByCurrentYear(int year) {
    return activityTitleDAO.findByCurrentYear(year);
  }

  @Override
  public ActivityTitle getActivityTitleById(long activityTitleID) {

    return activityTitleDAO.find(activityTitleID);
  }

  @Override
  public ActivityTitle saveActivityTitle(ActivityTitle activityTitle) {

    return activityTitleDAO.save(activityTitle);
  }


}
