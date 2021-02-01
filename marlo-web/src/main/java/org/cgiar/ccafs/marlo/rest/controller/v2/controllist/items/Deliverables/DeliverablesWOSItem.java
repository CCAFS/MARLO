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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Deliverables;

import org.cgiar.ccafs.marlo.rest.dto.PublicationsWOSDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.PublicationWOSMapper;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.PublicationWOS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class DeliverablesWOSItem<T> {

  private PublicationWOSMapper publicationWOSMapper;


  @Inject
  public DeliverablesWOSItem(PublicationWOSMapper publicationWOSMapper) {
    super();
    this.publicationWOSMapper = publicationWOSMapper;
  }

  public JsonElement getServiceWOS(String url) throws MalformedURLException, IOException {
    URL clarisaUrl = new URL(url);

    URLConnection conn = clarisaUrl.openConnection();
    conn.setRequestProperty("Authorization", "3174h8-c40e68-5ge392-218caa-a664b3");
    JsonElement element = null;
    try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
      element = new JsonParser().parse(reader);
    } catch (FileNotFoundException fnfe) {
      element = JsonNull.INSTANCE;
    }
    return element;
  }

  public ResponseEntity<PublicationsWOSDTO> validateDeliverable(String url) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    PublicationsWOSDTO publicationDTO = null;
    if (url != null) {
      try {
        JsonElement json = this.getServiceWOS("http://clarisa.wos.api.mel.cgiar.org/?link=" + url);
        PublicationWOS publication = new Gson().fromJson(json, PublicationWOS.class);
        publicationDTO = this.publicationWOSMapper.publicationWOSToPublicationWOSDTO(publication);
        publicationDTO.setUrl(url);

      } catch (Exception e) {
        e.printStackTrace();
        fieldErrors.add(
          new FieldErrorDTO("getServiceWOS", "JSON element", "Error trying to get data from WOS " + e.getMessage()));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("getServiceWOS", "URL", "URL is missing"));
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(publicationDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
