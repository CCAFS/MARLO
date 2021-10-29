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

import org.cgiar.ccafs.marlo.rest.services.deliverables.model.Author;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataModel;
import org.cgiar.ccafs.marlo.utils.DateTypeAdapter;
import org.cgiar.ccafs.marlo.utils.RestConnectionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MELDSpaceClientAPI extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(MELDSpaceClientAPI.class);
  private final String HTTP_URL = "http";
  private final String HTTPS_URL = "https";
  private String HANDLE_URL = "://hdl.handle.net/";
  private String DSPACE_URL = "://repo.mel.cgiar.org/handle/";
  private String DSPACE_HANDLE = "://repo.mel.cgiar.org/rest/handle/{0}";
  private String REST_URL = "://repo.mel.cgiar.org/rest/items/{0}/metadata";
  private RestConnectionUtil xmlReaderConnectionUtil;
  private Map<String, String> coverterAtrributes;

  public MELDSpaceClientAPI() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
    coverterAtrributes = new HashMap<String, String>();
    coverterAtrributes.put("description.abstract", "description");
    coverterAtrributes.put("date", "publicationDate");
    coverterAtrributes.put("subject", "keywords");
    coverterAtrributes.put("identifier.citation", "citation");
    coverterAtrributes.put("identifier.uri", "handle");
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = null;
    List<Element> subjectElement = new ArrayList<>();
    List<Element> agrovocElement = new ArrayList<>();
    JSONObject jo = new JSONObject();
    this.setDefaultEmptyValues(jo);
    try {
      Element metadata = xmlReaderConnectionUtil.getXmlRestClient(link);
      List<Author> authors = new ArrayList<Author>();
      List<Element> elements = metadata.elements();

      // Get author ORCID
      Map<String, String> authorMap = new HashMap<String, String>();
      for (Element element : elements) {
        Element key = element.element("key");
        Element value = element.element("value");
        String keyValue = key.getStringValue();
        keyValue = keyValue.substring(3);
        if (keyValue.equals("creator.id")) {
          if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
            String authorInfo[] = value.getStringValue().split(": ");
            authorMap.put(authorInfo[0].trim(), authorInfo[1].trim());
          }
        } else if (keyValue.contains("subject")) {
          if (keyValue.equals("subject.agrovoc")) {
            agrovocElement.add(element);
          } else {
            subjectElement.add(element);
          }
        }
      }

      if (!agrovocElement.isEmpty()) {
        for (Element pos : subjectElement) {
          elements.remove(pos);
        }

        for (Element element : agrovocElement) {
          Element value = element.element("value");
          if (jo.has("keywords") && jo.get("keywords") != null && jo.get("keywords") != "") {
            jo.put("keywords", jo.get("keywords") + "," + value.getStringValue());
          } else {
            jo.put("keywords", value.getStringValue());
          }
          elements.remove(element);
        }
      }

      for (Element element : elements) {
        Element key = element.element("key");
        Element value = element.element("value");
        String keyValue = key.getStringValue();
        keyValue = keyValue.substring(3);

        if (keyValue.equals("contributor.author") || keyValue.equals("contributor") || keyValue.equals("creator")) {
          Author author = new Author(value.getStringValue());
          String names[] = author.getFirstName().split(", ");
          if (names.length == 2) {
            author.setFirstName(names[1]);
            author.setLastName(names[0]);
          }
          if (authorMap != null && !authorMap.isEmpty() && authorMap.size() > 0) {
            if (authorMap.containsKey(value.getStringValue().trim())) {
              author.setOrcidId(authorMap.get(value.getStringValue().trim()));
            } else {
              author.setOrcidId("No ORCID");
            }
          } else {
            author.setOrcidId("No ORCID");
          }

          if (keyValue.equals("creator")) {
            authors.add(0, author);
          } else {
            authors.add(author);
          }
        } else if (keyValue.equals("identifier.status")) {
          if (value.getStringValue().equals("Open Access")) {
            jo.put("openAccess", "true");
          } else {
            jo.put("openAccess", "false");
          }
        } else if (keyValue.equals("identifier.citation")) {
          jo.put("citation", value.getStringValue());
        } else if (keyValue.equals("identifier.uri")) {
          jo.put("handle", value.getStringValue());
        } else if (keyValue.equals("isijournal")) {
          if (value.getStringValue().contains("ISI")) {
            jo.put("ISI", "true");
          } else {
            jo.put("ISI", "false");
          }
        } else if (keyValue.equals("coverage.country")) {
          jo.put("countries", value.getStringValue());
        } else if (keyValue.equals("identifier")) {
          if (value.getStringValue() != null && value.getStringValue().contains("doi")) {
            jo.put("doi", value.getStringValue());
          }
        } else if (keyValue.equals("subject")) {

        } else {
          if (jo.has(keyValue) && jo.get(keyValue) != null && jo.get(keyValue) != "") {
            jo.put(keyValue, jo.get(keyValue) + "," + value.getStringValue());
          } else {
            jo.put(keyValue, value.getStringValue());
          }
        }

      }

      this.setDoi(jo);
      // date trick
      if (jo.has("publicationDate") && StringUtils.isBlank(jo.get("publicationDate").toString())) {
        Object value = jo.remove("publicationDate");
        if (jo.has("date") && StringUtils.isNotBlank(jo.get("date").toString())) {
          jo.put("publicationDate", jo.get("date").toString());
        }
      }

      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
      Gson gson = gsonBuilder.create();
      String data = jo.toString();
      for (String key : coverterAtrributes.keySet()) {
        data = data.replace(key, coverterAtrributes.get(key));
      }
      metadataModel = gson.fromJson(data, MetadataModel.class);
      Author[] authorsArr = new Author[authors.size()];
      authorsArr = authors.toArray(authorsArr);
      metadataModel.setAuthors(authorsArr);

    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());

    }
    return metadataModel;
  }

  /**
   * with the link get the id and make a connection to get the Metadata id connnection and format into the rest url
   * 
   * @return the link to get the metadata
   */
  @Override
  public String parseLink(String link) {

    if (link.contains(HTTPS_URL)) {
      HANDLE_URL = HTTPS_URL + HANDLE_URL;
      DSPACE_HANDLE = HTTPS_URL + DSPACE_HANDLE;
      REST_URL = HTTPS_URL + REST_URL;
      DSPACE_URL = HTTPS_URL + DSPACE_URL;
    } else {
      HANDLE_URL = HTTP_URL + HANDLE_URL;
      DSPACE_HANDLE = HTTP_URL + DSPACE_HANDLE;
      REST_URL = HTTP_URL + REST_URL;
      DSPACE_URL = HTTP_URL + DSPACE_URL;
    }

    if (link.contains(HANDLE_URL)) {
      this.setId(link.replace(HANDLE_URL, ""));
    }

    if (link.contains(DSPACE_URL)) {
      this.setId(link.replace(DSPACE_URL, ""));
    }

    String handleUrl = DSPACE_HANDLE.replace("{0}", this.getId());
    Element elementHandle = xmlReaderConnectionUtil.getXmlRestClient(handleUrl);
    this.setId(elementHandle.element("UUID").getStringValue());
    String linkRest = (REST_URL.replace("{0}", this.getId()));
    return linkRest;
  }
}
