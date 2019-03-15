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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressCrossCuttingMarkerMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisFlagshipProgressCrossCuttingMarker, Long>
  implements ReportSynthesisFlagshipProgressCrossCuttingMarkerDAO {


  @Inject
  public ReportSynthesisFlagshipProgressCrossCuttingMarkerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteReportSynthesisFlagshipProgressCrossCuttingMarker(long reportSynthesisFlagshipProgressCrossCuttingMarkerId) {
    ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker =
      this.find(reportSynthesisFlagshipProgressCrossCuttingMarkerId);
    this.delete(reportSynthesisFlagshipProgressCrossCuttingMarker);
  }

  @Override
  public boolean
    existReportSynthesisFlagshipProgressCrossCuttingMarker(long reportSynthesisFlagshipProgressCrossCuttingMarkerID) {
    ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker =
      this.find(reportSynthesisFlagshipProgressCrossCuttingMarkerID);
    if (reportSynthesisFlagshipProgressCrossCuttingMarker == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressCrossCuttingMarker find(long id) {
    return super.find(ReportSynthesisFlagshipProgressCrossCuttingMarker.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressCrossCuttingMarker> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressCrossCuttingMarker.class.getName();
    List<ReportSynthesisFlagshipProgressCrossCuttingMarker> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFlagshipProgressCrossCuttingMarker getCountryMarkerId(long milestoneID,
    long cgiarCrossCuttingMarkerID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT id as markerId FROM report_synthesis_flagship_progress_cross ");
    query.append("WHERE report_synthesis_flagship_progress_outcome_milestone_id = ");
    query.append(milestoneID);
    query.append(" AND cgiar_cross_cutting_marker_id = ");
    query.append(cgiarCrossCuttingMarkerID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ReportSynthesisFlagshipProgressCrossCuttingMarker> crossCuttingMarkers =
      new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
    for (Map<String, Object> map : list) {
      String markerId = map.get("markerId").toString();
      long longMarkerId = Long.parseLong(markerId);
      ReportSynthesisFlagshipProgressCrossCuttingMarker crossCuttingMarker = this.find(longMarkerId);
      crossCuttingMarkers.add(crossCuttingMarker);
    }
    if (crossCuttingMarkers.size() > 0) {
      return crossCuttingMarkers.get(0);
    } else {
      return null;
    }

  }

  @Override
  public ReportSynthesisFlagshipProgressCrossCuttingMarker
    save(ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker) {
    if (reportSynthesisFlagshipProgressCrossCuttingMarker.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressCrossCuttingMarker);
    } else {
      reportSynthesisFlagshipProgressCrossCuttingMarker =
        super.update(reportSynthesisFlagshipProgressCrossCuttingMarker);
    }


    return reportSynthesisFlagshipProgressCrossCuttingMarker;
  }


}