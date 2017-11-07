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
public class OutcomeOutputs implements Serializable {


  private static final long serialVersionUID = -891647133437368282L;

  private CenterOutcome outcome;

  private List<CenterOutput> outputs;

  public CenterOutcome getOutcome() {
    return outcome;
  }

  public List<CenterOutput> getOutputs() {
    return outputs;
  }

  public void setOutcome(CenterOutcome outcome) {
    this.outcome = outcome;
  }

  public void setOutputs(List<CenterOutput> outputs) {
    this.outputs = outputs;
  }

  @Override
  public String toString() {
    return "OutcomeOutputs [outcome=" + outcome + "]";
  }

}
