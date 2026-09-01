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


package org.cgiar.ccafs.marlo.action.crp.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * One logical activity of one cluster(project) that is using an activity title.
 * Activities replicate forward, so the same activity exists as one activities record per phase, all of them sharing
 * the same composedId. This view model collapses that group into a single row: every phase where it exists, in
 * chronological order, and whether it is still reported in the phase the user is looking at.
 * It is read only and exists only to render the activities management popup.
 * 
 * @author IBD
 */
public class ActivityTitleRelation implements Serializable {

  private static final long serialVersionUID = 4501889721164515820L;

  private long clusterId;
  private String clusterTitle;
  private String composedId;
  private String activityDescription;
  /** Every phase where this activity exists, in chronological order. */
  private List<String> phaseLabels = new ArrayList<>();
  private boolean reportedInCurrentPhase;

  public String getActivityDescription() {
    return activityDescription;
  }

  public long getClusterId() {
    return clusterId;
  }

  public String getClusterTitle() {
    return clusterTitle;
  }

  public String getComposedId() {
    return composedId;
  }

  public int getPhaseCount() {
    return phaseLabels.size();
  }

  public List<String> getPhaseLabels() {
    return phaseLabels;
  }

  /**
   * Adds a phase to the chronological list, ignoring repetitions.
   * 
   * @param phaseLabel the label of the phase, already formatted.
   */
  public void addPhaseLabel(String phaseLabel) {
    if (phaseLabel == null || phaseLabel.isEmpty() || phaseLabels.contains(phaseLabel)) {
      return;
    }
    phaseLabels.add(phaseLabel);
  }

  public boolean isReportedInCurrentPhase() {
    return reportedInCurrentPhase;
  }

  public void setActivityDescription(String activityDescription) {
    this.activityDescription = activityDescription;
  }

  public void setClusterId(long clusterId) {
    this.clusterId = clusterId;
  }

  public void setClusterTitle(String clusterTitle) {
    this.clusterTitle = clusterTitle;
  }

  public void setComposedId(String composedId) {
    this.composedId = composedId;
  }

  public void setPhaseLabels(List<String> phaseLabels) {
    this.phaseLabels = phaseLabels == null ? new ArrayList<String>() : phaseLabels;
  }

  public void setReportedInCurrentPhase(boolean reportedInCurrentPhase) {
    this.reportedInCurrentPhase = reportedInCurrentPhase;
  }
}
