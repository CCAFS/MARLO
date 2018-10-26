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
import org.cgiar.ccafs.marlo.utils.RestConnectionUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.icu.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generalization of Dataverse
 * 
 * @author avalencia
 */
public class DataverseClientApi extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(DataverseClientApi.class);
  private RestConnectionUtil restConnectionUtil;

  // Metadata identifiers
  private final String publicationDate = "publicationDate", data = "data", persistentUrl = "persistentUrl",
    latestVersion = "latestVersion", metadataBlocks = "metadataBlocks", citation = "citation",
    geospatial = "geospatial", fields = "fields", typeName = "typeName", title = "title", value = "value",
    dsDescription = "dsDescription", dsDescriptionValue = "dsDescriptionValue", language = "language",
    geographicCoverage = "geographicCoverage", country = "country", keyword = "keyword", keywordValue = "keywordValue",
    author = "author", authorName = "authorName", authorIdentifierScheme = "authorIdentifierScheme",
    authorIdentifier = "authorIdentifier", ORCID = "ORCID", otherGeographicCoverage = "otherGeographicCoverage";

  public DataverseClientApi() {
    restConnectionUtil = new RestConnectionUtil();
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = new MetadataModel();
    JSONObject jo = new JSONObject();
    try {
      String metadata = restConnectionUtil.getJsonRestClient(link);
      jo = new JSONObject(metadata);
      jo = jo.getJSONObject(data);
      if (jo != null && jo.length() > 0) {
        try {
          if (jo.has(publicationDate)) {
            String dateInString = (String) jo.get(publicationDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(dateInString);
            metadataModel.setPublicationDate(date);
          }
        } catch (ParseException e) {
          e.printStackTrace();
          LOG.warn(e.getMessage());
        }

        if (jo.has(persistentUrl)) {
          String persistentUrlValue = (String) jo.get(persistentUrl);
          if (persistentUrlValue != null && !persistentUrlValue.isEmpty()) {
            if (persistentUrlValue.contains("doi")) {
              metadataModel.setDoi(persistentUrlValue);
            }
            if (persistentUrlValue.contains("handle")) {
              metadataModel.setHandle(persistentUrlValue);
            }
          }
        }
        if (jo.has(latestVersion)) {
          JSONObject latestVersionJsonObject = (JSONObject) jo.get(latestVersion);
          if (latestVersionJsonObject.has(metadataBlocks)) {
            JSONObject metadataObject = (JSONObject) latestVersionJsonObject.get(metadataBlocks);
            List<JSONObject> metadataObjects = new ArrayList<JSONObject>();
            if (metadataObject.has(citation)) {
              JSONObject citationObject = (JSONObject) metadataObject.get(citation);
              metadataObjects.add(citationObject);
            }
            if (metadataObject.has(geospatial)) {
              JSONObject geospatialObject = (JSONObject) metadataObject.get(geospatial);
              metadataObjects.add(geospatialObject);
            }
            if (metadataObjects != null && metadataObjects.size() > 0) {
              for (JSONObject metadataJSONObject : metadataObjects) {
                if (metadataJSONObject.has(fields)) {
                  JSONArray fieldsMetadataJsonArray = metadataJSONObject.getJSONArray(fields);
                  if (fieldsMetadataJsonArray != null && fieldsMetadataJsonArray.length() > 0) {
                    for (Object fieldObject : fieldsMetadataJsonArray) {
                      JSONObject jsonObject = (JSONObject) fieldObject;
                      if (jsonObject.has(typeName)) {
                        String typeNameObject = (String) jsonObject.get(typeName);

                        switch (typeNameObject) {
                          case title:
                            metadataModel.setTitle(jsonObject.getString(value));
                            break;

                          case dsDescription:
                            if (jsonObject.has(value)) {
                              JSONArray valuesJsonArray = jsonObject.getJSONArray(value);
                              if (valuesJsonArray != null && valuesJsonArray.length() > 0) {
                                for (Object valueObject : valuesJsonArray) {
                                  JSONObject valueJsonObject = (JSONObject) valueObject;
                                  if (valueJsonObject.has(dsDescriptionValue)) {
                                    JSONObject descriptionValueObject =
                                      (JSONObject) valueJsonObject.get(dsDescriptionValue);
                                    if (descriptionValueObject.has(value)) {
                                      metadataModel.setDescription((String) descriptionValueObject.get(value));
                                    }
                                  }
                                }
                              }
                            }
                            break;

                          case language:
                            if (jsonObject.has(value)) {
                              JSONArray languagesJsonArray = jsonObject.getJSONArray(value);
                              if (languagesJsonArray != null && languagesJsonArray.length() > 0) {
                                Set<String> languages = new HashSet<String>();
                                for (Object languageObject : languagesJsonArray) {
                                  languages.add((String) languageObject);
                                }
                                if (languages != null && languages.size() > 0) {
                                  metadataModel.setLanguage(String.join(", ", languages));
                                }
                              }
                            }
                            break;

                          case geographicCoverage:
                            if (jsonObject.has(value)) {
                              JSONArray geographicJsonArray = jsonObject.getJSONArray(value);
                              if (geographicJsonArray != null && geographicJsonArray.length() > 0) {
                                Set<String> countries = new HashSet<String>();
                                for (Object geographicObject : geographicJsonArray) {
                                  JSONObject geographicJsonObject = (JSONObject) geographicObject;
                                  if (geographicJsonObject.has(country)
                                    || geographicJsonObject.has(otherGeographicCoverage)) {
                                    JSONObject countryJsonObject = null;
                                    if (geographicJsonObject.has(country)) {
                                      countryJsonObject = (JSONObject) geographicJsonObject.get(country);
                                    }
                                    if (geographicJsonObject.has(otherGeographicCoverage)) {
                                      countryJsonObject =
                                        (JSONObject) geographicJsonObject.get(otherGeographicCoverage);
                                    }

                                    if (countryJsonObject != null && countryJsonObject.length() > 0) {
                                      countries.add((String) countryJsonObject.get(value));
                                    }
                                  }

                                }
                                if (countries != null && countries.size() > 0) {
                                  metadataModel.setCountry(String.join(", ", countries));
                                }

                              }
                            }

                            break;

                          case keyword:
                            if (jsonObject.has(value)) {
                              JSONArray keywordJsonArray = jsonObject.getJSONArray(value);
                              if (keywordJsonArray != null && keywordJsonArray.length() > 0) {
                                String keywords = "";
                                for (Object keywordObject : keywordJsonArray) {
                                  JSONObject keywordJsonObject = (JSONObject) keywordObject;
                                  if (keywordJsonObject.has(keywordValue)) {
                                    JSONObject keywordValueJsonObject =
                                      (JSONObject) keywordJsonObject.get(keywordValue);
                                    if (keywordValueJsonObject != null && keywordValueJsonObject.length() > 0) {
                                      if (keywords != null && keywords.isEmpty()) {
                                        keywords = (String) keywordValueJsonObject.get(value);
                                      } else {
                                        keywords = keywords + ", " + (String) keywordValueJsonObject.get(value);
                                      }
                                    }
                                  }
                                }
                                metadataModel.setKeywords(keywords);
                              }
                            }
                            break;

                          case author:
                            if (jsonObject.has(value)) {
                              JSONArray authorJsonArray = jsonObject.getJSONArray(value);
                              if (authorJsonArray != null && authorJsonArray.length() > 0) {
                                List<Author> authors = new ArrayList<Author>();
                                for (Object authorObject : authorJsonArray) {
                                  JSONObject authorJsonObject = (JSONObject) authorObject;
                                  if (authorJsonObject.has(authorName)) {
                                    JSONObject authorNameJsonObject = (JSONObject) authorJsonObject.get(authorName);
                                    Author author =
                                      new Author(((String) authorNameJsonObject.get(value)).replaceAll(",", ""));
                                    String names[] = author.getFirstName().split(" ");
                                    // Name validations
                                    if (names.length > 0) {
                                      if (names.length == 1) {
                                        author.setFirstName(names[0]);
                                      } else if (names.length == 2) {
                                        author.setFirstName(names[0]);
                                        author.setLastName(names[1]);
                                      } else if (names.length == 3) {
                                        if (names[1].contains(".")) {
                                          author.setFirstName(names[0] + " " + names[1]);
                                          author.setLastName(names[2]);
                                        } else {
                                          author.setFirstName(names[0]);
                                          author.setLastName(names[1] + " " + names[2]);
                                        }
                                      } else {
                                        author.setFirstName(names[0] + " " + names[1]);
                                        String lastName = "";
                                        for (int i = 2; i < names.length; i++) {
                                          if (i + 1 == names.length) {
                                            lastName += names[i];
                                          } else {
                                            lastName += names[i] + " ";
                                          }
                                        }
                                        author.setLastName(lastName);
                                      }
                                      if (authorJsonObject.has(authorIdentifierScheme)) {
                                        JSONObject authorIdentifierSchemeJsonObject =
                                          (JSONObject) authorJsonObject.get(authorIdentifierScheme);
                                        String authorIdentifierScheme =
                                          authorIdentifierSchemeJsonObject.getString(value);
                                        if (authorIdentifierScheme != null && !authorIdentifierScheme.isEmpty()
                                          && authorIdentifierScheme.equals(ORCID)) {
                                          JSONObject authorIdentifierJsonObject =
                                            (JSONObject) authorJsonObject.get(authorIdentifier);
                                          String authorIdentifier = authorIdentifierJsonObject.getString(value);
                                          if (authorIdentifier != null && !authorIdentifier.isEmpty()) {
                                            author.setOrcidId(authorIdentifier);
                                          }
                                        }

                                      }
                                      authors.add(author);
                                    }
                                  }
                                }

                                Author[] authorsArr = new Author[authors.size()];
                                authorsArr = authors.toArray(authorsArr);
                                metadataModel.setAuthors(authorsArr);
                              }
                            }
                            break;


                          default:
                            break;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());
    }
    return metadataModel;

  }

}