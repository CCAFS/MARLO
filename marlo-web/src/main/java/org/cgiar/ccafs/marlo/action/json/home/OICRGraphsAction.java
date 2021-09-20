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

public class OICRGraphsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -3968321974456586107L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(OICRGraphsAction.class);

  // Variables
  private List<StudyHomeDTO> studies;

  // Graph results
  private List<GraphCountDTO> byLevel;
  private List<GraphCountDTO> bySdgContribution;

  @Inject
  public OICRGraphsAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    this.byLevel = new ArrayList<>();
    this.studies.stream()
      .sorted((s1, s2) -> String.CASE_INSENSITIVE_ORDER.compare(s1.getStudyMaturity(), s2.getStudyMaturity()))
      .collect(Collectors.groupingBy(s -> s.getStudyMaturity()))
      .forEach((k, v) -> byLevel.add(new GraphCountDTO(k, (long) v.size())));
    return SUCCESS;
  }

  public List<GraphCountDTO> getByLevel() {
    return byLevel;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {
    try {
      this.studies = (List<StudyHomeDTO>) this.getSession().get(APConstants.USER_OICRS);
      if (studies == null) {
        studies = Collections.emptyList();
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.studies = Collections.emptyList();
    }
  }

  public void setByLevel(List<GraphCountDTO> byLevel) {
    this.byLevel = byLevel;
  }

}
