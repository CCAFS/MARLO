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
import org.dom4j.Element;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CIMMYTClientAPI extends MetadataClientApi {

  private static final Logger LOG = LoggerFactory.getLogger(CIMMYTClientAPI.class);

  // {0} is the identifier
  private final String Dataverse_OAI_PMH_HANDLE =
    "http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&metadataPrefix=oai_dc&identifier={0}";
  private final String Dataverse_OAI_PMH_HANDLE_URL =
    "http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&metadataPrefix=oai_dc&identifier=";
  // This should be the parameter
  private final String Dataverse_OAI_PMH_URL =
    "http://data.cimmyt.org/dvn/dv/csisadvn/faces/study/StudyPage.xhtml?globalId=";

  private RestConnectionUtil xmlReaderConnectionUtil;
  private Map<String, String> coverterAtrributes;

  public CIMMYTClientAPI() {
    xmlReaderConnectionUtil = new RestConnectionUtil();
    coverterAtrributes = new HashMap<String, String>();
    coverterAtrributes.put("subject", "keywords");
    coverterAtrributes.put("publicationDate", "date");
  }

  @Override
  public MetadataModel getMetadata(String link) {
    MetadataModel metadataModel = null;
    JSONObject jo = new JSONObject();
    try {
      Element metadata = xmlReaderConnectionUtil.getXmlRestClient(link);
      List<Author> authors = new ArrayList<Author>();
      List<Element> elements = metadata.elements();
      for (Element element : elements) {
        // get recordOAI
        Element recordOAI = element.element("record");
        if (recordOAI != null) {
          // get metadataOAI
          Element metadataOAI = recordOAI.element("metadata");
          if (metadataOAI != null) {
            // get oai_dc:dc
            List<Element> elements2 = metadataOAI.elements();
            for (Element oai_dc : elements2) {
              // get authors
              List<Element> authorsElements = oai_dc.elements("creator");
              if (authorsElements != null && authorsElements.size() > 0) {
                for (Element value : authorsElements) {
                  Author author = new Author(value.getStringValue());
                  String names[] = author.getFirstName().split(" ");
                  // Name validations
                  if (names.length > 0) {
                    if (names.length == 1) {
                      author.setFirstName(names[0]);
                      authors.add(author);
                    } else if (names.length == 2) {
                      author.setFirstName(names[0]);
                      author.setLastName(names[1]);
                      authors.add(author);
                    } else if (names.length == 3) {
                      if (names[1].contains(".")) {
                        author.setFirstName(names[0] + " " + names[1]);
                        author.setLastName(names[2]);
                        authors.add(author);
                      } else {
                        author.setFirstName(names[0]);
                        author.setLastName(names[1] + " " + names[2]);
                        authors.add(author);
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
                      authors.add(author);
                    }
                  }
                }
              }
              // get citation and description
              List<Element> descriptionsElements = oai_dc.elements("description");
              if (descriptionsElements != null && descriptionsElements.size() > 0) {

                for (Element descriptionElement : descriptionsElements) {
                  String description = descriptionElement.getStringValue();
                  if (description != null && !description.isEmpty()) {
                    String descriptionArray[] = description.split(" ");
                    // is citation
                    if (descriptionArray[0].equals("Citation:")) {
                      jo.put("citation", description);
                    } else {
                      jo.put(descriptionElement.getName(), description);
                    }
                  }
                }
              }
              // get title
              Element titleElement = oai_dc.element("title");
              String titleValue = titleElement.getStringValue();
              jo.put(titleElement.getName(), titleValue);
              // get keywords
              List<Element> subjectsElements = oai_dc.elements("subject");
              if (subjectsElements != null && subjectsElements.size() > 0) {
                String keyWords = "";
                for (Element subjectElement : subjectsElements) {
                  if (keyWords.isEmpty()) {
                    keyWords += subjectElement.getStringValue();
                  } else {
                    keyWords += " " + subjectElement.getStringValue();
                  }
                }
                jo.put(subjectsElements.get(0).getName(), keyWords);
              }
              // get rights
              Element rightsElement = oai_dc.element("rights");
              String rightsValue = rightsElement.getStringValue();
              jo.put(rightsElement.getName(), rightsValue);

              // no language, no openacces no doi, no country, handle
              // get date
              Element dateElement = oai_dc.element("date");
              String dateValue = dateElement.getStringValue();
              jo.put(dateElement.getName(), dateValue);

            }
          }
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
      System.out.println(metadataModel.getPublicationDate());
      Author[] authorsArr = new Author[authors.size()];
      authorsArr = authors.toArray(authorsArr);
      metadataModel.setAuthors(authorsArr);

    } catch (

    Exception e) {
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
    /*
     * if the link contains http://data.cimmyt.org/dvn/OAIHandler?verb=GetRecord&metadataPrefix=oai_dc&identifier= we
     * remove it from the link
     */
    if (link.contains(Dataverse_OAI_PMH_HANDLE_URL)) {
      this.setId(link.replace(Dataverse_OAI_PMH_HANDLE_URL, ""));
    }
    /*
     * if the link http://data.cimmyt.org/dvn/dv/csisadvn/faces/study/StudyPage.xhtml?globalId= we remove it from the
     * link
     */
    if (link.contains(Dataverse_OAI_PMH_URL)) {
      this.setId(link.replace(Dataverse_OAI_PMH_URL, ""));
    }

    String handleUrl = Dataverse_OAI_PMH_HANDLE.replace("{0}", this.getId());
    RestConnectionUtil connection = new RestConnectionUtil();
    Element elementHandle = connection.getXmlRestClient(handleUrl);
    this.setId(elementHandle.element("id").getStringValue());
    return handleUrl;
  }
}
