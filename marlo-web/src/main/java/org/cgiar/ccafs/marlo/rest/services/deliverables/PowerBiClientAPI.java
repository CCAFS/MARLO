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

package org.cgiar.ccafs.marlo.rest.services.deliverables;

import org.cgiar.ccafs.marlo.rest.services.deliverables.model.PowerBiBody;

import java.util.Collections;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class PowerBiClientAPI {

  private static final Logger LOG = LoggerFactory.getLogger(PowerBiClientAPI.class);

  private final String NAME_INDEX_TOKEN = "token";

  private String extraeValueJson(String jsonString, String index) {
    JSONObject jsonObject = new JSONObject(jsonString);
    return jsonObject.get(index).toString();
  }

  public String generateToken(String link, String bearerToken, PowerBiBody powerBiBody) {
    try {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/json")));
      headers.add("Authorization", "Bearer " + bearerToken);

      HttpEntity<PowerBiBody> request = new HttpEntity<>(powerBiBody, headers);

      ResponseEntity<String> result = restTemplate.exchange(link, HttpMethod.POST, request, String.class);

      return this.extraeValueJson(result.getBody().toString(), NAME_INDEX_TOKEN);

    } catch (HttpClientErrorException e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());
      return null;
    }

  }

}
