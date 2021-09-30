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
import org.cgiar.ccafs.marlo.data.dto.StudyHomeDTO;
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

public class MeliaGraphsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 624003349869690697L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(MeliaGraphsAction.class);

  // Variables
  private List<StudyHomeDTO> studies;

  // Graph results
  private List<GraphCountDTO> byStudyType;

  @Inject
  public MeliaGraphsAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    this.byStudyType = new ArrayList<>();
    this.studies.stream().collect(Collectors.groupingBy(s -> s.getStudyType()))
      .forEach((k, v) -> byStudyType.add(new GraphCountDTO(k, (long) v.size())));
    return SUCCESS;
  }

  public List<GraphCountDTO> getByStudyType() {
    return byStudyType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {
    try {
      this.studies = (List<StudyHomeDTO>) this.getSession().get(APConstants.USER_MELIAS);
      if (studies == null) {
        studies = Collections.emptyList();
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.studies = Collections.emptyList();
    }
  }

  public void setByStudyType(List<GraphCountDTO> result) {
    this.byStudyType = result;
  }
}
