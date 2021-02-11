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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CGSpaceClientAPI extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(CGSpaceClientAPI.class);
  private final String BASE_URL = "https://dspacetest.cgiar.org/";
  private final String CGSPACE_URL = BASE_URL + "handle/";
  private final String CGSPACE_HANDLE = BASE_URL + "rest/handle/{0}";
  private final String REST_URL = BASE_URL + "rest/items/{0}/metadata";
  private final String HANDLE_URL = "http://hdl.handle.net/";
  private final String HANDLE_HTTPS_URL = "https://hdl.handle.net/";
  private RestConnectionUtil xmlReaderConnectionUtil;
  private Map<String, String> coverterAtrributes;

  public CGSpaceClientAPI() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
    coverterAtrributes = new HashMap<String, String>();
    coverterAtrributes.put("description.abstract", "description");
    coverterAtrributes.put("date.issued", "publicationDate");
    coverterAtrributes.put("language.iso", "language");
    coverterAtrributes.put("subject", "keywords");
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = null;
    JSONObject jo = new JSONObject();
    this.setDefaultEmptyValues(jo);

    try {
      String metadata = xmlReaderConnectionUtil.getJsonRestClient(link);
      Type typeOfT = new TypeToken<List<Map<String, Object>>>() {
      }.getType();
      List<Map<String, Object>> jsonResponse = new GsonBuilder().serializeNulls().create().fromJson(metadata, typeOfT);
      List<Author> authors = new ArrayList<>();
      StringJoiner countries = new StringJoiner(", ");
      StringJoiner keywords = new StringJoiner(", ");

      if (jsonResponse != null && !jsonResponse.isEmpty()) {
        for (Map<String, Object> element : jsonResponse) {
          if (element != null && !element.isEmpty()) {
            String key = String.valueOf(element.get("key"));
            String value = String.valueOf(element.get("value"));

            if (key != null && !key.equalsIgnoreCase("null") && value != null && !value.equalsIgnoreCase("null")) {
              switch (key) {
                case "dc.contributor.author":
                  Author author = new Author(value);
                  String names[] = author.getFirstName().split(", ");
                  if (names.length == 2) {
                    author.setFirstName(names[1]);
                    author.setLastName(names[0]);
                  }
                  authors.add(author);
                  break;

                case "dcterms.accessRights":
                  jo.put("openAccess", value.toLowerCase().equalsIgnoreCase("Open Access"));
                  break;

                case "dcterms.bibliographicCitation":
                  jo.put("citation", value);
                  break;

                case "dc.identifier.uri":
                  jo.put("handle", value);
                  break;

                case "cg.isijournal":
                  jo.put("ISI", StringUtils.containsIgnoreCase(value.toLowerCase(), "ISI"));
                  break;

                case "dc.coverage.country":
                  countries.add(value);
                  break;

                case "dcterms.extent":
                  jo.put("pages", value);
                  break;

                case "cg.edition":
                  // format is supposed to be volume(issue)
                  if (StringUtils.containsAny(value, '(', ')')) {
                    String issue = StringUtils.substringBetween(value, "(", ")");
                    jo.put("issue", issue);
                    jo.put("volume", StringUtils.substringBefore(value, "("));
                  } else {
                    jo.put("volume", value);
                  }
                  break;

                case "dc.title":
                  jo.put("title", value);
                  break;

                case "dcterms.abstract":
                  jo.put("description", value);
                  break;

                case "dcterms.language":
                  jo.put("language", value);
                  break;

                case "cg.identifier.doi":
                  jo.put("doi", value);
                  break;

                case "cg.journal":
                  jo.put("journal", value);
                  break;

                case "dcterms.issued":
                  jo.put("publicationDate", value);
                  break;

                case "dcterms.subject":
                  keywords.add(value);
                  break;

                default:
                  if (jo.has(key) && jo.get(key) != null && jo.get(key) != "") {
                    jo.put(key, jo.get(key) + "," + value);
                  } else {
                    jo.put(key, value);
                  }
              }
            }
          }
        }

        jo.put("countries", countries.toString());
        jo.put("keywords", keywords.toString());
        this.setDoi(jo);

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
      }
    } catch (RuntimeException | IOException e) {
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
    String linkRest = "";

    // if the link contains http://hdl.handle.net/ we remove it from the link
    if (link.contains(HANDLE_URL)) {
      this.setId(link.replace(HANDLE_URL, ""));
    }
    // if the link contains https://hdl.handle.net/ we remove it from the link
    if (link.contains(HANDLE_HTTPS_URL)) {
      this.setId(link.replace(HANDLE_HTTPS_URL, ""));
    }
    // if the link https://cgspace.cgiar.org/handle/ we remove it from the link
    if (link.contains(CGSPACE_URL)) {
      this.setId(link.replace(CGSPACE_URL, ""));
    }

    if (this.getId() != null) {
      String handleUrl = CGSPACE_HANDLE.replace("{0}", this.getId());
      RestConnectionUtil connection = new RestConnectionUtil();
      Element elementHandle = connection.getXmlRestClient(handleUrl);
      if (elementHandle != null && elementHandle.element("UUID") != null) {
        this.setId(elementHandle.element("UUID").getStringValue());
        if (this.getId() != null) {
          linkRest = (REST_URL.replace("{0}", this.getId()));
        }
      }
    }

    return linkRest;
  }
}
