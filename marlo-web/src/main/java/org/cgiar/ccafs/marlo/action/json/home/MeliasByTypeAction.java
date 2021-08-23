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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class MeliasByTypeAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 624003349869690697L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(MeliasByTypeAction.class);

  private List<StudyHomeDTO> studies;
  private Map<String, List<StudyHomeDTO>> classifiedStudies;

  @Inject

  public MeliasByTypeAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    // TODO Auto-generated method stub
    return SUCCESS;
  }

  public Map<String, List<StudyHomeDTO>> getClassifiedStudies() {
    return classifiedStudies;
  }

  public List<StudyHomeDTO> getStudies() {
    return studies;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    // If there are parameters, take its values
    try {
      this.studies = (List<StudyHomeDTO>) parameters.get(APConstants.STUDIES_FOLDER).getObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setClassifiedStudies(Map<String, List<StudyHomeDTO>> result) {
    this.classifiedStudies = result;
  }

  public void setStudies(List<StudyHomeDTO> studies) {
    this.studies = studies;
  }
}
