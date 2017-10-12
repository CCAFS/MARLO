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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.rest.services.deliverables.MetadataApiFactory;
import org.cgiar.ccafs.marlo.rest.services.deliverables.MetadataClientApi;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataModel;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MetadataByLink extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -8827720754396815474L;


  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(MetadataByLink.class);


  // Model
  private String page;


  private String link;
  private MetadataModel metadata;

  @Inject
  public MetadataByLink(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    if (page.equals("-1")) {
      return NOT_FOUND;
    }
    if (link.equals("-1")) {
      return NOT_FOUND;
    }
    // Api is created depending on the page we are looking for, if you are going to add a new repository you must create
    // the class and write the methods and add it here in the factory
    MetadataClientApi metadataClientApi = MetadataApiFactory.getMetadataClientApi(page);
    String handleUrl = metadataClientApi.parseLink(link);
    metadata = metadataClientApi.getMetadata(handleUrl);
    return SUCCESS;
  }


  public MetadataModel getMetadata() {
    return metadata;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    // If there is a parameter take its values
    try {
      link = StringUtils.trim(((String[]) parameters.get(APConstants.METADATA_REQUEST_ID))[0]);
    } catch (Exception e) {
      link = StringUtils.trim(((String[]) parameters.get("q"))[0]);
    }
    page = StringUtils.trim(((String[]) parameters.get(APConstants.PAGE_ID))[0]);

  }


  public void setMetadata(MetadataModel metadata) {
    this.metadata = metadata;
  }

}
