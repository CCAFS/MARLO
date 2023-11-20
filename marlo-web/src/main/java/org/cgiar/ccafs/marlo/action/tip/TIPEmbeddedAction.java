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

package org.cgiar.ccafs.marlo.action.tip;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.BiParametersManager;
import org.cgiar.ccafs.marlo.data.manager.BiReportsManager;
import org.cgiar.ccafs.marlo.data.model.BiParameters;
import org.cgiar.ccafs.marlo.data.model.BiReports;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class TIPEmbeddedAction extends BaseAction {

  private static final long serialVersionUID = 1329042468240291639L;

  private static final Logger LOG = LoggerFactory.getLogger(TIPEmbeddedAction.class);

  // Managers
  private BiReportsManager biReportsManager;
  private BiParametersManager biParametersManager;

  // Front-end
  private List<BiReports> biReports;
  private List<BiParameters> biParameters;

  @Inject
  public TIPEmbeddedAction(APConfig config, BiReportsManager biReportsManager, BiParametersManager biParametersManager) {
    super(config);
    this.biReportsManager = biReportsManager;
    this.biParametersManager = biParametersManager;
  }

  public List<BiParameters> getBiParameters() {
    return biParameters;
  }


  public List<BiReports> getBiReports() {
    return biReports;
  }


  @Override
  public void prepare() {
    biReports = biReportsManager.findAll();
    biParameters = biParametersManager.findAll();
  }


  public void setBiParameters(List<BiParameters> biParameters) {
    this.biParameters = biParameters;
  }


  public void setBiReports(List<BiReports> biReports) {
    this.biReports = biReports;
  }


}
