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

package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * A2-2747: recognises an OICR that "Add an OICR" created but nobody ever saved.
 * <p>
 * The add action persists the study (and its info rows, replicated to every later phase) before the user types
 * anything. Saving always writes a section status, so a study with no section status in any phase and no title
 * anywhere was never saved. The phase guard keeps legacy data out: 2018-2020 studies also lack section statuses, but
 * their info rows sit in earlier years, so they never match, and a match never touches a past phase.
 */
public final class ExpectedStudyDrafts {

  /** How long an unsaved draft is kept before it is removed, so a form still open in another tab keeps working. */
  public static final long CLEANUP_AGE_MILLIS = 24L * 60L * 60L * 1000L;

  private ExpectedStudyDrafts() {
  }

  /**
   * @param study the study to check
   * @param actualPhase the phase being viewed
   * @return true when the study was created by "Add" and never saved
   */
  public static boolean isUnsavedDraft(ProjectExpectedStudy study, Phase actualPhase) {
    if (study == null || !study.isActive() || actualPhase == null) {
      return false;
    }
    if (study.getSectionStatuses() != null && !study.getSectionStatuses().isEmpty()) {
      return false;
    }
    Set<ProjectExpectedStudyInfo> infos = study.getProjectExpectedStudyInfos();
    if (infos == null || infos.isEmpty()) {
      return false;
    }
    for (ProjectExpectedStudyInfo info : infos) {
      if (StringUtils.isNotBlank(info.getTitle())) {
        return false;
      }
      if (info.getPhase() == null || info.getPhase().getYear() < actualPhase.getYear()) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param study the study to check
   * @param actualPhase the phase being viewed
   * @param user the logged user
   * @param now the current time
   * @return true when the study is an unsaved draft of this user, old enough to be removed
   */
  public static boolean isRemovableDraft(ProjectExpectedStudy study, Phase actualPhase, User user, Date now) {
    if (user == null || now == null || !isUnsavedDraft(study, actualPhase)) {
      return false;
    }
    User createdBy = study.getCreatedBy();
    Date activeSince = study.getActiveSince();
    return createdBy != null && user.getId() != null && user.getId().equals(createdBy.getId()) && activeSince != null
      && now.getTime() - activeSince.getTime() > CLEANUP_AGE_MILLIS;
  }
}
