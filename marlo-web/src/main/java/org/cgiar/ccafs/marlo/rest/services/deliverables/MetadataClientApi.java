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

import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataModel;
import org.cgiar.ccafs.marlo.utils.RestConnectionUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public abstract class MetadataClientApi {

  /**
   * Extract parameters from a given URL
   * 
   * @param url
   * @return Map<key,List<values>>
   */
  public static Map<String, List<String>> getQueryParams(String url) {
    try {
      Map<String, List<String>> params = new HashMap<String, List<String>>();
      String[] urlParts = url.split("\\?");
      if (urlParts.length > 1) {
        String query = urlParts[1];
        for (String param : query.split("&")) {
          String[] pair = param.split("=");
          String key = URLDecoder.decode(pair[0], "UTF-8");
          String value = "";
          if (pair.length > 1) {
            value = URLDecoder.decode(pair[1], "UTF-8");
          }
          List<String> values = params.get(key);
          if (values == null) {
            values = new ArrayList<String>();
            params.put(key, values);
          }
          values.add(value);
        }
      }
      return params;
    } catch (UnsupportedEncodingException ex) {
      throw new AssertionError(ex);
    }
  }

  private String id;


  public String getId() {
    return id;
  }

  public MetadataModel getMetadata(String link) {
    return null;
  }

  public String parseLink(String link) {
    return link;
  }

  private void putKeyIfNotExists(JSONObject jo, String key) {
    if (!jo.has(key) || jo.get(key) == null || jo.get(key).toString().equals("{}")) {
      jo.put(key, "");
    }
  }

  public void setDefaultEmptyValues(JSONObject jo) {
    this.putKeyIfNotExists(jo, "citation");
    this.putKeyIfNotExists(jo, "title");
    this.putKeyIfNotExists(jo, "handle");
    this.putKeyIfNotExists(jo, "keywords");
    this.putKeyIfNotExists(jo, "description");
    this.putKeyIfNotExists(jo, "rights");
    this.putKeyIfNotExists(jo, "language");
    this.putKeyIfNotExists(jo, "openAccess");
    this.putKeyIfNotExists(jo, "ISI");
    this.putKeyIfNotExists(jo, "doi");
    this.putKeyIfNotExists(jo, "publicationDate");
    this.putKeyIfNotExists(jo, "country");
    this.putKeyIfNotExists(jo, "publisher");
    this.putKeyIfNotExists(jo, "journal");
    this.putKeyIfNotExists(jo, "volume");
    this.putKeyIfNotExists(jo, "issue");
    this.putKeyIfNotExists(jo, "pages");
  }

  public void setDoi(JSONObject jo, RestConnectionUtil xmlReaderConnectionUtil)
    throws JsonParseException, JsonMappingException, IOException {
    // get DOI
    if (jo.has("doi") && jo.get("doi") != null) {
      String doi = jo.get("doi").toString();
      if (doi.contains("http://dx.doi.org/") || doi.contains("https://doi.org/")) {

        if (doi.contains("http://dx.doi.org/")) {
          doi = doi.replace("http://dx.doi.org/", "https://doi.org/");
        }
        doi = doi.trim();
        if (!doi.equals("{}")) {
          String metadataDOI = xmlReaderConnectionUtil.getJsonRestClientFromDOI(doi);
          HashMap<String, Object> result = new ObjectMapper().readValue(metadataDOI, HashMap.class);
          if (result != null && !result.isEmpty()) {
            Object volume = result.get("volume");
            Object issue = result.get("issue");
            Object page = result.get("page");
            Object publisher = result.get("publisher");

            // volume
            if (volume != null) {
              jo.put("volume", volume.toString());
            }

            // issue
            if (issue != null) {
              jo.put("issue", issue.toString());
            }

            // page
            if (page != null) {
              jo.put("pages", page.toString());
            }

            // journal
            if (publisher != null) {
              jo.put("journal", publisher.toString());
            }
          }

        }
      }
    }
  }

  public void setId(String id) {
    this.id = id;
  }

}