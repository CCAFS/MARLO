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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class AzureClientAPI {

  private static final Logger LOG = LoggerFactory.getLogger(AzureClientAPI.class);

  private final String GRANT_TYPE = "grant_type";
  private final String CLIENT_ID = "client_id";
  private final String CLIENT_SECRET = "client_secret";
  private final String RESOURCE = "resource";
  private final String NAME_INDEX_TOKEN = "access_token";

  private MultiValueMap<String, String> parameters;

  public AzureClientAPI(String grantType, String clientId, String clientSecret, String resource) {
    this.parameters = new LinkedMultiValueMap<String, String>();
    this.parameters.add(GRANT_TYPE, grantType);
    this.parameters.add(CLIENT_ID, clientId);
    this.parameters.add(CLIENT_SECRET, clientSecret);
    this.parameters.add(RESOURCE, resource);
  }


  private String extraeValueJson(String jsonString, String index) {
    JSONObject jsonObject = new JSONObject(jsonString);
    return jsonObject.get(index).toString();
  }

  public String generateBearerToken(String link) {
    try {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(this.parameters, headers);

      ResponseEntity<String> result = restTemplate.postForEntity(link, request, String.class);

      return this.extraeValueJson(result.getBody().toString(), NAME_INDEX_TOKEN);

    } catch (HttpClientErrorException e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());
      return null;
    }
  }

}
