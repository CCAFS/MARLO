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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CGSpaceClientAPI extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(CGSpaceClientAPI.class);
  private final String BASE_URL = "https://cgspace.cgiar.org/";
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
      // 🔹 Ensure we are not appending an invalid /metadata suffix (DSpace 7 no longer uses it)
      String requestUrl = link.replace("/metadata", "");
      String metadataJson = xmlReaderConnectionUtil.getJsonRestClient(requestUrl);

      // Detect whether response is DSpace 7 (JSON Object) or DSpace 6 (JSON Array)
      boolean isDSpace7 = metadataJson.trim().startsWith("{");

      List<Author> authors = new ArrayList<>();
      StringJoiner countries = new StringJoiner(", ");
      StringJoiner keywords = new StringJoiner(", ");

      if (isDSpace7) {
        // ============================================================
        // 🔸 DSPACE 7 STRUCTURE
        // ============================================================
        /*
         * In DSpace 7, the API endpoint /server/api/core/items/{uuid}
         * returns a JSON object containing all metadata under the field "metadata".
         * Each metadata key has an array of value objects with structure:
         * "metadata": {
         * "dc.title": [ { "value": "Some title" } ],
         * "dcterms.abstract": [ { "value": "Abstract text" } ],
         * ...
         * }
         */
        JSONObject jsonItem = new JSONObject(metadataJson);
        JSONObject metadataNode = jsonItem.optJSONObject("metadata");

        if (metadataNode != null) {
          for (String key : metadataNode.keySet()) {
            JSONArray values = metadataNode.getJSONArray(key);
            for (int i = 0; i < values.length(); i++) {
              String value = values.getJSONObject(i).optString("value", "").trim();
              if (value.isEmpty()) {
                continue;
              }

              switch (key) {
                case "dc.contributor.author":
                  Author author = new Author(value);
                  String[] names = author.getFirstName().split(", ");
                  if (names.length == 2) {
                    author.setFirstName(names[1]);
                    author.setLastName(names[0]);
                  }
                  authors.add(author);
                  break;

                case "dcterms.accessRights":
                  jo.put("openAccess", value.equalsIgnoreCase("Open Access"));
                  break;

                case "dcterms.bibliographicCitation":
                  jo.put("citation", value);
                  break;

                case "dc.identifier.uri":
                  jo.put("handle", value);
                  break;

                case "cg.isijournal":
                  jo.put("ISI", StringUtils.containsIgnoreCase(value.toLowerCase(), "isi"));
                  break;

                case "cg.coverage.country":
                case "dc.coverage.country":
                  countries.add(value);
                  break;

                case "dcterms.extent":
                  jo.put("pages", value);
                  break;

                case "cg.edition":
                  // Example format: volume(issue)
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
                  // Append unrecognized metadata keys as plain fields
                  if (jo.has(key) && jo.get(key) != null && !jo.getString(key).isEmpty()) {
                    jo.put(key, jo.getString(key) + "," + value);
                  } else {
                    jo.put(key, value);
                  }
              }
            }
          }
        }

      } else {
        // ============================================================
        // 🔸 DSPACE 6 STRUCTURE
        // ============================================================
        /*
         * In DSpace 6, the endpoint /rest/items/{uuid}/metadata returns
         * a JSON array of key-value pairs:
         * [ { "key": "dc.title", "value": "Some title" }, ... ]
         */
        Type typeOfT = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        List<Map<String, Object>> jsonResponse =
          new GsonBuilder().serializeNulls().create().fromJson(metadataJson, typeOfT);

        if (jsonResponse != null && !jsonResponse.isEmpty()) {
          for (Map<String, Object> element : jsonResponse) {
            if (element == null || element.isEmpty()) {
              continue;
            }

            String key = String.valueOf(element.get("key"));
            String value = String.valueOf(element.get("value"));

            if (key == null || value == null || key.equalsIgnoreCase("null") || value.equalsIgnoreCase("null")) {
              continue;
            }

            switch (key) {
              case "dc.contributor.author":
                Author author = new Author(value);
                String[] names = author.getFirstName().split(", ");
                if (names.length == 2) {
                  author.setFirstName(names[1]);
                  author.setLastName(names[0]);
                }
                authors.add(author);
                break;

              case "dcterms.accessRights":
                jo.put("openAccess", value.equalsIgnoreCase("Open Access"));
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
                if (jo.has(key) && jo.get(key) != null && !jo.getString(key).isEmpty()) {
                  jo.put(key, jo.getString(key) + "," + value);
                } else {
                  jo.put(key, value);
                }
            }
          }
        }
      }

      // ============================================================
      // 🔸 Finalize and convert JSON into MetadataModel
      // ============================================================
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

      Author[] authorsArr = authors.toArray(new Author[0]);
      metadataModel.setAuthors(authorsArr);

    } catch (RuntimeException | IOException e) {
      e.printStackTrace();
      LOG.error("Error parsing metadata from CGSpace: {}", e.getMessage());
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
    if (link == null) {
      return "";
    }

    String in = StringUtils.trim(link);

    // ==========================================================
    // 1. SUPPORT FOR DSPACE 7: URL pattern with /items/{uuid}
    // Example: https://cgspace.cgiar.org/items/d55ee33d-848c-464a-9587-b227aa79c8c5
    // This pattern directly provides the UUID of the item.
    // The corresponding REST endpoint is:
    // https://cgspace.cgiar.org/server/api/core/items/{uuid}/metadata
    // ==========================================================
    Pattern pUuidInItems = Pattern.compile("(?i)/items/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})");
    Matcher mItems = pUuidInItems.matcher(in);
    if (mItems.find()) {
      String uuid = mItems.group(1);
      this.setId(uuid);

      // Build the DSpace 7 metadata REST URL
      String DSPACE7_ITEMS_METADATA = BASE_URL + "server/api/core/items/{0}/metadata";
      return DSPACE7_ITEMS_METADATA.replace("{0}", uuid);
    }

    // ==========================================================
    // 2. LEGACY SUPPORT FOR HANDLES (DSpace 6)
    // This section handles links that contain:
    // - http://hdl.handle.net/{handle}
    // - https://hdl.handle.net/{handle}
    // - https://cgspace.cgiar.org/handle/{handle}
    // The method extracts the handle and resolves it into a UUID
    // through the /rest/handle/{handle} endpoint.
    // ==========================================================
    if (in.contains(HANDLE_URL)) {
      this.setId(in.replace(HANDLE_URL, ""));
    } else if (in.contains(HANDLE_HTTPS_URL)) {
      this.setId(in.replace(HANDLE_HTTPS_URL, ""));
    } else if (in.contains(CGSPACE_URL)) {
      this.setId(in.replace(CGSPACE_URL, ""));
    }

    // If a handle ID was found, resolve it into a UUID and build the REST URL
    if (this.getId() != null) {
      String handleUrl = CGSPACE_HANDLE.replace("{0}", this.getId());
      RestConnectionUtil connection = new RestConnectionUtil();
      Element elementHandle = connection.getXmlRestClient(handleUrl);

      if (elementHandle != null && elementHandle.element("UUID") != null) {
        this.setId(elementHandle.element("UUID").getStringValue());
        if (this.getId() != null) {
          // Build the legacy DSpace 6 REST URL using the resolved UUID
          return REST_URL.replace("{0}", this.getId());
        }
      }
    }

    // ==========================================================
    // 3. FALLBACK: Direct REST endpoint already provided
    // If the provided link is already in the REST API format,
    // simply return it as-is without any transformation.
    // ==========================================================
    if (in.matches("(?i)^https?://[^\\s]+/server/api/core/items/[0-9a-f-]{36}/metadata.*$")
      || in.matches("(?i)^https?://[^\\s]+/rest/items/[0-9a-f-]{36}/metadata.*$")) {
      return in;
    }

    // ==========================================================
    // 4. If no valid pattern was matched, return an empty string.
    // This will cause the calling action (MetadataByLink)
    // to return NOT_FOUND, indicating an invalid or unsupported URL.
    // ==========================================================
    return "";
  }

}
