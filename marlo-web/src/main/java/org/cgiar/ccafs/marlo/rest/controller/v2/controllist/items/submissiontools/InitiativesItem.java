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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools;

import org.cgiar.ccafs.marlo.rest.dto.EndOfInitiativeOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InitiativesDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.EOIOutcomeMapper;
import org.cgiar.ccafs.marlo.rest.mappers.InitiativeMapper;
import org.cgiar.ccafs.marlo.rest.services.submissionTools.InitiativesList;
import org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi.EOIOutcomesByInitiativesList;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Configuration
@PropertySource("classpath:global.properties")
@Named
public class InitiativesItem<T> {

  public InitiativeMapper initiativeMapper;
  public EOIOutcomeMapper eoiOutcomeMapper;

  protected APConfig config;

  @Inject
  public InitiativesItem(InitiativeMapper initiativeMapper, EOIOutcomeMapper eoiOutcomeMapper, APConfig config) {
    super();
    this.initiativeMapper = initiativeMapper;
    this.eoiOutcomeMapper = eoiOutcomeMapper;
    this.config = config;
  }

  public ResponseEntity<List<EndOfInitiativeOutcomeDTO>> getEndOfInitiativeOutcomes() {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<EndOfInitiativeOutcomeDTO> initiativeListDTO = new ArrayList<>();
    org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi.Response response = null;
    String url = config.getUrlSubmissionTools();
    if (url != null) {
      try {
        // getting token
        JsonElement json = this.getSubmissionElement(url + "stages-control/proposal/eoi/all/initiatives");

        response = new Gson().fromJson(json, org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi.Response.class);
        if (response.getResponse() != null) {
          EOIOutcomesByInitiativesList eoiOutcomesByInitiativesList = response.getResponse();
          if (eoiOutcomesByInitiativesList.getEoi_outcome_by_initiatives() != null) {
            initiativeListDTO = eoiOutcomesByInitiativesList.getEoi_outcome_by_initiatives().stream()
              .map(eoi -> this.eoiOutcomeMapper.endOfInitiativeOutcomeToEndOfInitiativeOutcomeDTO(eoi))
              .collect(Collectors.toList());
          }

        }
      } catch (Exception e) {
        e.printStackTrace();
        fieldErrors.add(new FieldErrorDTO("getInitiatives", "JSON element",
          "Error trying to get data from service  " + e.getMessage()));
      }

    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(initiativeListDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<InitiativesDTO>> getInitiatives() {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<InitiativesDTO> initiativeListDTO = new ArrayList<InitiativesDTO>();
    org.cgiar.ccafs.marlo.rest.services.submissionTools.Response response = null;
    String url = config.getUrlSubmissionTools();
    if (url != null) {
      try {
        // getting token
        JsonElement json = this.getSubmissionElement(url + "initiatives");

        response = new Gson().fromJson(json, org.cgiar.ccafs.marlo.rest.services.submissionTools.Response.class);
        if (response.getResponse() != null) {
          InitiativesList initiativesList = response.getResponse();
          if (initiativesList.getInitiatives() != null) {
            initiativeListDTO = initiativesList.getInitiatives().stream()
              .map(init -> this.initiativeMapper.initiativeToInitiativesDTO(init)).collect(Collectors.toList());
          }

        }
      } catch (Exception e) {
        e.printStackTrace();
        fieldErrors.add(new FieldErrorDTO("getInitiatives", "JSON element",
          "Error trying to get data from service  " + e.getMessage()));
      }

    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(initiativeListDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public JsonElement getSubmissionElement(String url) throws MalformedURLException, IOException {
    URL submissionToolsUrl = new URL(url);
    String loginData = config.getSubmissionToolsUser() + ":" + config.getSubmissionToolsPassword();
    String encoded = Base64.encodeBase64String(loginData.getBytes());
    HttpURLConnection conn = (HttpURLConnection) submissionToolsUrl.openConnection();
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
