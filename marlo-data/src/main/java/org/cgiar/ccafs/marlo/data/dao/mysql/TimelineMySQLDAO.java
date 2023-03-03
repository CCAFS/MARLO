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

import org.cgiar.ccafs.marlo.data.dao.TimelineDAO;
import org.cgiar.ccafs.marlo.data.model.Timeline;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class TimelineMySQLDAO extends AbstractMarloDAO<Timeline, Long> implements TimelineDAO {


  @Inject
  public TimelineMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteTimeline(long timelineId) {
    Timeline timeline = this.find(timelineId);
    this.delete(timeline);
  }

  @Override
  public boolean existTimeline(long timelineID) {
    Timeline timeline = this.find(timelineID);
    if (timeline == null) {
      return false;
    }
    return true;

  }

  @Override
  public Timeline find(long id) {
    return super.find(Timeline.class, id);

  }

  @Override
  public List<Timeline> findAll() {
    String query = "from " + Timeline.class.getName();
    List<Timeline> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Timeline save(Timeline timeline) {
    if (timeline.getId() == null) {
      super.saveEntity(timeline);
    } else {
      timeline = super.update(timeline);
    }


    return timeline;
  }


}