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
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataWOSModel;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DeliverableMetadataByWOS extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -1340291586140709256L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(DeliverableMetadataByWOS.class);

  private String link;
  private String jsonStringResponse;
  private MetadataWOSModel response;

  @Inject
  public DeliverableMetadataByWOS(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    if (this.jsonStringResponse == null) {
      return NOT_FOUND;
    }

    this.response = new Gson().fromJson(jsonStringResponse, MetadataWOSModel.class);

    return SUCCESS;
  }

  public String getJsonStringResponse() {
    return jsonStringResponse;
  }

  public String getLink() {
    return link;
  }

  public MetadataWOSModel getResponse() {
    return response;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    // If there are parameters, take its values
    try {
      this.link = StringUtils.trim(parameters.get(APConstants.WOS_LINK).getMultipleValues()[0]);
    } catch (Exception e) {
      this.link = StringUtils.trim(parameters.get("q").getMultipleValues()[0]);
    }

    JsonElement response = this.readWOSDataFromClarisa(this.link);

    this.jsonStringResponse = StringUtils.stripToNull(new GsonBuilder().serializeNulls().create().toJson(response));
  }

  private JsonElement readWOSDataFromClarisa(final String url) throws IOException {
    URL clarisaUrl = new URL(config.getClarisaWOSLink().replace("{1}", url));

    String loginData = config.getClarisaWOSUser() + ":" + config.getClarisaWOSPassword();
    String encoded = Base64.encodeBase64String(loginData.getBytes());
    URLConnection conn = clarisaUrl.openConnection();
    conn.setRequestProperty("Authorization", "Basic " + encoded);

    JsonElement element = null;

    try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
      element = new JsonParser().parse(reader);
    } catch (FileNotFoundException fnfe) {
      element = JsonNull.INSTANCE;
    }

    return element;
  }
}
