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

import org.cgiar.ccafs.marlo.rest.dto.WorkPackagesDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.WorkpackagesMapper;
import org.cgiar.ccafs.marlo.rest.services.submissionTools.workpackages.Response;
import org.cgiar.ccafs.marlo.rest.services.submissionTools.workpackages.WorkpackageList;
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
public class WorkpackagesItem<T> {

  private WorkpackagesMapper WorkpackagesMapper;
  protected APConfig config;

  @Inject
  public WorkpackagesItem(org.cgiar.ccafs.marlo.rest.mappers.WorkpackagesMapper workpackagesMapper, APConfig config) {
    super();
    WorkpackagesMapper = workpackagesMapper;
    this.config = config;
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

  public ResponseEntity<List<WorkPackagesDTO>> getWorkPackages() {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<WorkPackagesDTO> workpackageListDTO = new ArrayList<WorkPackagesDTO>();
    Response response = null;
    String url = config.getUrlSubmissionTools();
    if (url != null) {
      try {
        JsonElement json = this.getSubmissionElement(url + "stages-control/proposal/packages");
        response = new Gson().fromJson(json, Response.class);
        if (response.getResponse() != null) {
          WorkpackageList workpackageList = response.getResponse();
          if (workpackageList.getWorkpackage() != null) {
            workpackageListDTO = workpackageList.getWorkpackage().stream()
              .map(init -> this.WorkpackagesMapper.workpackageToWorkPackagesDTO(init)).collect(Collectors.toList());
          }

        }
      } catch (Exception e) {
        e.printStackTrace();
        fieldErrors.add(new FieldErrorDTO("getWorkPackages", "JSON element",
          "Error trying to get data from service  " + e.getMessage()));
      }

    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(workpackageListDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
