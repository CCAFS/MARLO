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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.rest.services.qa.IndicatorStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
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

public class QAAssessmentStatusAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 2110261707890251944L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(QAAssessmentStatusAction.class);

  // Types for different responses
  private final Type individualResponseType = new TypeToken<IndicatorStatus>() {
  }.getType();
  private final Type listResponseType = new TypeToken<List<IndicatorStatus>>() {
  }.getType();

  // Managers
  private GlobalUnitManager globalUnitManager;

  // Variables
  private String crpIdString;
  private Integer indicatorTypeId;
  private Integer indicatorId;
  private Integer year;
  private String jsonStringResponse;

  private List<IndicatorStatus> fullItemsAssessmentStatus;
  private IndicatorStatus individualItemAssessmentStatus;

  public QAAssessmentStatusAction(APConfig config, GlobalUnitManager globalUnitManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
  }

  @Override
  public String execute() throws Exception {
    if (this.jsonStringResponse != null && !StringUtils.equalsIgnoreCase(this.jsonStringResponse, "null")) {
      if (this.indicatorId != null) {
        this.individualItemAssessmentStatus = new Gson().fromJson(jsonStringResponse, individualResponseType);
      } else {
        this.fullItemsAssessmentStatus = new Gson().fromJson(jsonStringResponse, listResponseType);
      }
    }

    return SUCCESS;
  }

  public List<IndicatorStatus> getFullItemsAssessmentStatus() {
    return fullItemsAssessmentStatus;
  }

  public IndicatorStatus getIndividualItemAssessmentStatus() {
    return individualItemAssessmentStatus;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    // If there are parameters, take its values
    try {
      Integer crpId =
        Integer.valueOf(StringUtils.stripToEmpty(parameters.get(APConstants.CRP_ID).getMultipleValues()[0]));
      GlobalUnit globalUnit = this.globalUnitManager.getGlobalUnitById(crpId.intValue());
      this.crpIdString = globalUnit == null ? "" : globalUnit.getSmoCode();

      this.indicatorTypeId = Integer.valueOf(
        StringUtils.stripToEmpty(parameters.get(APConstants.INDICATOR_TYPE_REQUEST_ID).getMultipleValues()[0]));

      String indicatorIdString = parameters.get(APConstants.INDICATOR_ID).isDefined()
        ? StringUtils.stripToEmpty(parameters.get(APConstants.INDICATOR_ID).getMultipleValues()[0]) : null;
      this.indicatorId = StringUtils.isEmpty(indicatorIdString) ? null : Integer.valueOf(indicatorIdString);

      this.year =
        Integer.valueOf(StringUtils.stripToEmpty(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]));
    } catch (Exception e) {
      this.crpIdString = null;
    }

    if (StringUtils.isNotEmpty(this.crpIdString) && this.indicatorTypeId != null && this.year != null) {
      JsonElement response = this.readQAIndicatorStatusFromClarisa();

      this.jsonStringResponse = StringUtils.stripToNull(new GsonBuilder().serializeNulls().create().toJson(response));
    }
  }

  private JsonElement readQAIndicatorStatusFromClarisa() throws IOException {
    String clarisaUrlString = null;
    if (this.indicatorId != null) {
      clarisaUrlString = config.getQAIndividualIndicatorStatusUrl().replace("{1}", String.valueOf(this.indicatorTypeId))
        .replace("{2}", String.valueOf(this.indicatorId)).replace("{3}", this.crpIdString)
        .replace("{4}", String.valueOf(this.year));
    } else {
      clarisaUrlString = config.getQAFullIndicatorStatusUrl().replace("{1}", String.valueOf(this.indicatorTypeId))
        .replace("{2}", this.crpIdString).replace("{3}", String.valueOf(this.year));
    }

    URL clarisaUrl = new URL(clarisaUrlString);
    String loginData = config.getClarisaWOSUser() + ":" + config.getClarisaWOSPassword();
    String encoded = Base64.encodeBase64String(loginData.getBytes());

    HttpURLConnection conn = (HttpURLConnection) clarisaUrl.openConnection();
    conn.setRequestProperty("Authorization", "Basic " + encoded);
    JsonElement element = null;

    if (conn.getResponseCode() < 300) {
      try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
        element = new JsonParser().parse(reader);
      } catch (FileNotFoundException fnfe) {
        element = JsonNull.INSTANCE;
      }
    }

    return element;
  }

  public void setCrpIdString(String crpIdString) {
    this.crpIdString = crpIdString;
  }

  public void setIndicatorId(Integer indicatorId) {
    this.indicatorId = indicatorId;
  }

  public void setIndicatorTypeId(Integer indicatorTypeId) {
    this.indicatorTypeId = indicatorTypeId;
  }

  public void setYear(Integer year) {
    this.year = year;
  }
}
