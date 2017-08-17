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
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.ClientRepository;

import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.json.JSONObject;
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


  private String id;
  private String metadata;

  // Managers
  private ClientRepository clientRepository;

  // http://cdm15738.contentdm.oclc.org/oai/oai.php?verb=GetRecord&metadataPrefix=oai_dc&identifier=oai:cdm15738.contentdm.oclc.org:p15738coll2/541
  private final String CGSPACE = "https://cgspace.cgiar.org/rest/items/{0}/metadata";
  private final String IFPRI = "https://server15738.contentdm.oclc.org/dmwebservices/index.php";
  private final String ILRI = "http://data.ilri.org/portal/api/3/action/package_show";
  private final String AGTRIALS = "http://oai2.agtrials.org/oai2.php";
  private final String AMKN = "http://lab.amkn.org/oai/";

  @Inject
  public MetadataByLink(APConfig config, ClientRepository clientRepository) {
    super(config);
    this.clientRepository = clientRepository;
  }


  @Override
  public String execute() throws Exception {

    if (page.equals("-1")) {
      return NOT_FOUND;
    }

    if (id.equals("-1")) {
      return NOT_FOUND;
    }

    String linkRequest = "";
    switch (page) {
      case "cgspace":
        linkRequest = CGSPACE;
        JSONObject metadataObject = clientRepository.getMetadata(linkRequest, id);
        metadata = metadataObject.toString();
        break;
      case "ifpri":
        linkRequest = IFPRI;
        metadata = clientRepository.getMetadataIFPRI(linkRequest, id);
        break;
      case "ilri":
        linkRequest = ILRI;
        metadata = clientRepository.getMetadataILRI(linkRequest, id);
        break;
      default:
        break;
    }

    return SUCCESS;
  }


  public String getMetadata() {
    return metadata;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    // If there is a parameter take its values
    try {
      // id = StringUtils.trim(((String[]) parameters.get(APConstants.METADATA_REQUEST_ID))[0]);
      id = StringUtils.trim(parameters.get(APConstants.METADATA_REQUEST_ID).getMultipleValues()[0]);
    } catch (Exception e) {
      // id = StringUtils.trim(((String[]) parameters.get("q"))[0]);
      id = StringUtils.trim(parameters.get("q").getMultipleValues()[0]);
    }

    // page = StringUtils.trim(((String[]) parameters.get(APConstants.PAGE_ID))[0]);
    page = StringUtils.trim(parameters.get(APConstants.PAGE_ID).getMultipleValues()[0]);

  }


  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

}
