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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.qa;

import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.services.qa.IndicatorStatus;
import org.cgiar.ccafs.marlo.rest.services.qa.ResponseQA;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class IndicatorStatusItem<T> {

  // Variables
  private static final Type INDIVIDUAL_RESPONSE_TYPE = new TypeToken<ResponseQA<IndicatorStatus>>() {
  }.getType();
  private static final Type LIST_RESPONSE_TYPE = new TypeToken<ResponseQA<List<IndicatorStatus>>>() {
  }.getType();

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(IndicatorStatusItem.class);

  protected APConfig config;

  // Managers

  @Inject
  public IndicatorStatusItem(APConfig config) {
    super();
    this.config = config;
  }

  public ResponseEntity<List<IndicatorStatus>> findAllIndicatorStatusByIndicatorIdCrpAndPhase(Long indicatorId,
    String crpSmoCode, Long year) {
    List<IndicatorStatus> indicatorStati = null;
    ResponseQA<List<IndicatorStatus>> response = null;
    String url = config.getUrlQA();

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    if (url != null && indicatorId != null && StringUtils.isNotBlank(crpSmoCode) && year != null) {
      try {
        url += "indicator/" + indicatorId + "/crp/" + crpSmoCode + "/items?AR=" + year;

        JsonElement json = this.getQAElement(url);

        response = new Gson().fromJson(json, LIST_RESPONSE_TYPE);
        if (response.getData() != null) {
          indicatorStati = response.getData();
        }
      } catch (Exception e) {
        e.printStackTrace();
        fieldErrors.add(new FieldErrorDTO("findAllIndicatorStatusByIndicatorIdCrpAndPhase", "JSON element",
          "Error trying to get data from service: " + e.getMessage()));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(indicatorStati).map(ResponseEntity::ok)
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<IndicatorStatus> findIndicatorStatusByIndicatorIdCrpIdAndPhase(Long indicatorId,
    String crpSmoCode, Long id, Long year) {
    IndicatorStatus indicatorStatus = null;
    ResponseQA<IndicatorStatus> response = null;
    String url = config.getUrlQA();

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    if (url != null && indicatorId != null && StringUtils.isNotBlank(crpSmoCode) && id != null && year != null) {
      try {
        url += "indicator/" + indicatorId + "/crp/" + crpSmoCode + "/item/" + id + "?AR=" + year;

        JsonElement json = this.getQAElement(url);

        response = new Gson().fromJson(json, INDIVIDUAL_RESPONSE_TYPE);
        if (response.getData() != null) {
          indicatorStatus = response.getData();
        }
      } catch (Exception e) {
        e.printStackTrace();
        fieldErrors.add(new FieldErrorDTO("findIndicatorStatusByIndicatorIdCrpIdAndPhase", "JSON element",
          "Error trying to get data from service: " + e.getMessage()));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(indicatorStatus).map(ResponseEntity::ok)
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public JsonElement getQAElement(String url) throws MalformedURLException, IOException {
    URL submissionToolsUrl = new URL(url);
    String loginData = config.getQAUser() + ":" + config.getQAPassword();
    String encoded = Base64.encodeBase64String(loginData.getBytes());
    HttpURLConnection conn = (HttpURLConnection) submissionToolsUrl.openConnection();
    conn.setRequestProperty("Authorization", "Basic " + encoded);

    JsonElement element = null;
    try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
      String responseString = IOUtils.toString(reader);
      LOG.debug("QA response: {}", responseString);
      element = new JsonParser().parse(responseString);
    } catch (FileNotFoundException fnfe) {
      element = JsonNull.INSTANCE;
    }

    return element;
  }
}
