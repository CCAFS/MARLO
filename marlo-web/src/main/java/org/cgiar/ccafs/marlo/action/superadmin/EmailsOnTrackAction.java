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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * System Admin -> Emails. The page only needs the options of its filters: the rows are read a page at a time by
 * EmailLogsAction. It used to load every logged email with its message and attachment (tens of megabytes) on each
 * visit, to show the ones not sent.
 *
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class EmailsOnTrackAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  // Managers
  private EmailLogManager emailLogManager;
  private GlobalUnitManager globalUnitManager;
  // Front-end
  private List<GlobalUnit> globalUnits;
  private List<String> sourceActions;

  @Inject
  public EmailsOnTrackAction(APConfig config, EmailLogManager emailLogManager, GlobalUnitManager globalUnitManager) {
    super(config);
    this.emailLogManager = emailLogManager;
    this.globalUnitManager = globalUnitManager;
  }

  /**
   * @return the global units that have logged emails, by acronym.
   */
  public List<GlobalUnit> getGlobalUnits() {
    return globalUnits;
  }

  /**
   * @return the places the logged emails were sent from, in alphabetical order.
   */
  public List<String> getSourceActions() {
    return sourceActions;
  }

  @Override
  public void prepare() throws Exception {
    globalUnits = new ArrayList<>();
    for (Long globalUnitId : emailLogManager.findGlobalUnitIds()) {
      GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitId);
      if (globalUnit != null) {
        globalUnits.add(globalUnit);
      }
    }
    globalUnits.sort(Comparator.comparing(GlobalUnit::getAcronym, Comparator.nullsLast(String::compareToIgnoreCase)));
    sourceActions = new ArrayList<>(emailLogManager.findSourceActions());
    sourceActions.removeIf(Objects::isNull);
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }
}
