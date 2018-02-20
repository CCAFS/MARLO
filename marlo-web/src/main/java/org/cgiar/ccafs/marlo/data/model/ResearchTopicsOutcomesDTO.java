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

package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ResearchTopicsOutcomesDTO implements Serializable {


  private static final long serialVersionUID = -2702487529969621195L;

  CenterTopic researchTopic;

  List<CenterOutcome> outcomes;

  public List<CenterOutcome> getOutcomes() {
    return outcomes;
  }

  public CenterTopic getResearchTopic() {
    return researchTopic;
  }

  public void setOutcomes(List<CenterOutcome> outcomes) {
    this.outcomes = outcomes;
  }

  public void setResearchTopic(CenterTopic researchTopic) {
    this.researchTopic = researchTopic;
  }

  @Override
  public String toString() {
    return "ResearchTopicsOutcomesDTO [researchTopic=" + researchTopic + "]";
  }


}
