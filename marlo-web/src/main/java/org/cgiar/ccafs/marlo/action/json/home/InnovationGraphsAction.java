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

package org.cgiar.ccafs.marlo.action.json.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dto.InnovationHomeDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.dto.GraphCountDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class InnovationGraphsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -8253081117301197066L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(InnovationGraphsAction.class);

  // Variables
  private List<InnovationHomeDTO> innovations;

  // Graph results
  private List<GraphCountDTO> byInnovationType;
  private List<GraphCountDTO> byInnovationLevel;

  @Inject
  public InnovationGraphsAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    this.byInnovationType = new ArrayList<>();
    this.byInnovationLevel = new ArrayList<>();

    this.innovations.stream().collect(Collectors.groupingBy(i -> i.getInnovationType()))
      .forEach((k, v) -> byInnovationType.add(new GraphCountDTO(k, (long) v.size())));
    this.innovations.stream()
      .sorted((i1, i2) -> String.CASE_INSENSITIVE_ORDER.compare(i1.getInnovationStage(), i2.getInnovationStage()))
      .collect(Collectors.groupingBy(i -> i.getInnovationStage()))
      .forEach((k, v) -> byInnovationLevel.add(new GraphCountDTO(k, (long) v.size())));

    return SUCCESS;
  }

  public List<GraphCountDTO> getByInnovationLevel() {
    return byInnovationLevel;
  }

  public List<GraphCountDTO> getByInnovationType() {
    return byInnovationType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {
    try {
      this.innovations = (List<InnovationHomeDTO>) this.getSession().get(APConstants.USER_INNOVATIONS);
      if (innovations == null) {
        innovations = Collections.emptyList();
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.innovations = Collections.emptyList();
    }
  }

  public void setByInnovationLevel(List<GraphCountDTO> byInnovationLevel) {
    this.byInnovationLevel = byInnovationLevel;
  }

  public void setByInnovationType(List<GraphCountDTO> byInnovationType) {
    this.byInnovationType = byInnovationType;
  }
}
