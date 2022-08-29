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


import org.cgiar.ccafs.marlo.data.dao.TimelineDAO;
import org.cgiar.ccafs.marlo.data.manager.TimelineManager;
import org.cgiar.ccafs.marlo.data.model.Timeline;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class TimelineManagerImpl implements TimelineManager {


  private TimelineDAO timelineDAO;
  // Managers


  @Inject
  public TimelineManagerImpl(TimelineDAO timelineDAO) {
    this.timelineDAO = timelineDAO;


  }

  @Override
  public void deleteTimeline(long timelineId) {

    timelineDAO.deleteTimeline(timelineId);
  }

  @Override
  public boolean existTimeline(long timelineID) {

    return timelineDAO.existTimeline(timelineID);
  }

  @Override
  public List<Timeline> findAll() {

    return timelineDAO.findAll();

  }

  @Override
  public Timeline getTimelineById(long timelineID) {

    return timelineDAO.find(timelineID);
  }

  @Override
  public Timeline saveTimeline(Timeline timeline) {

    return timelineDAO.save(timeline);
  }


}
