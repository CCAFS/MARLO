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

package org.cgiar.ccafs.marlo.utils.handle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class HandleService {

  /**
   * DOI service URL. It returns a JSON as described in the DOI
   * <a href = "https://www.doi.org/factsheets/DOIProxy.html#rest-api">factsheet</a>
   */
  public final static String HANDLE_SERVICE = "https://doi.org/api/handles/%s";

  public final static String HANDLE_SERVICE_RESPONSE_CODE = "responseCode";

  /**
   * Pattern defined according to the ShortDOI <a href = "http://shortdoi.org/">webpage</a>.
   */
  public final static Pattern SHORT_DOI_PATTERN = Pattern.compile("^\\d{2}\\/\\w{3,5}$", Pattern.MULTILINE);
  // taken from <link>https://stackoverflow.com/questions/27910/finding-a-doi-in-a-document-or-page</link>
  public static final Pattern REGEXP_PLAINDOI =
    Pattern.compile("\\b(10[.][0-9]{4,}(?:[.][0-9]+)*/(?:(?![\"&\\'])\\S)+)\\b");

  private static JsonElement getDoiElement(final String doi) throws IOException {
    URL shortDoiServiceURL = new URL(String.format(HANDLE_SERVICE, doi));
    HttpURLConnection connection = (HttpURLConnection) shortDoiServiceURL.openConnection();
    JsonElement element = JsonNull.INSTANCE;

    if (connection.getResponseCode() < 299) {
      try (InputStreamReader reader = new InputStreamReader(shortDoiServiceURL.openStream())) {
        element = new JsonParser().parse(reader);
      } catch (FileNotFoundException fnfe) {
        // nothing
        fnfe.printStackTrace();
      }
    } else {
      throw new InvalidHandleException(doi);
    }

    return element;
  }

  public static String getDoiFromShortDoi(final String shortDoi) throws IOException {
    JsonElement element = getDoiElement(shortDoi);
    String doi = null;
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      HandleHttpResponseCode responseCode =
        HandleHttpResponseCode.getByErrorCode(object.get(HANDLE_SERVICE_RESPONSE_CODE).getAsInt());
      switch (responseCode) {
        case SUCCESS:
          // TODO we are not sure if it ALWAYS is an array, so we need to check if it is a JsonArray first
          JsonArray array = object.get("values").getAsJsonArray();
          object = array.get(1).getAsJsonObject().get("data").getAsJsonObject();
          doi = object.get("value").getAsString();
          break;

        case VALUES_NOT_FOUND:
          // fall-through
        case HANDLE_NOT_FOUND:
          // fall-through
        case ERROR:
          doi = StringUtils.EMPTY;
          break;
      }
    }

    return doi;
  }


  /**
   * Tries to get a DOI name from a given string.
   * <p>
   * A DOI name is defined as the path component of an URI, e.g. if your input string is
   * {@code https://www.doi.org/10.1007/978-3-319-29794-1_9}, the result will be {@code 10.1007/978-3-319-29794-1_9}.
   * </p>
   * <p>
   * This method currently recognizes and tries to get the DOI from strings like:
   * <blockquote>
   * <ul>
   * <li>https://www.doi.org/10.1007/978-3-319-29794-1_9
   * <li>http://dx.doi.org/10.1007/978-3-319-29794-1_9
   * <li>http://shortdoi.org/10.1007/978-3-319-29794-1_9
   * <li>https%3A%2F%2Fwww.doi.org%2F10.1007%2F978-3-319-29794-1_9
   * <li>http%3A%2F%2Fdx.doi.org%2F10.1007%2F978-3-319-29794-1_9
   * <li>http%3A%2F%2Fshortdoi.org%2F10%2Fdqnq
   * <li>10/dqnq
   * <li>/10/dqnq
   * <li>10.1007/978-3-319-29794-1_9
   * <li>/10.1007/978-3-319-29794-1_9
   * <li>doi 10.1007/978-3-319-29794-1_9
   * <li>doi:10.1007/978-3-319-29794-1_9
   * </ul>
   * </p>
   * </blockquote>
   * 
   * @param possibleDoi a String containing the possible DOI name
   * @return the DOI name found, {@link org.apache.commons.lang3.StringUtils.EMPTY empty} if could not be found or is
   *         invalid
   */
  public static String tryGetDoiName(final String possibleDoi) {
    String doi = StringUtils.EMPTY;
    if (StringUtils.isBlank(possibleDoi)) {
      return doi;
    }

    Matcher matcher = REGEXP_PLAINDOI.matcher(possibleDoi);

    if (matcher.find()) {
      doi = matcher.group(0);
    }
    /*
     * if (StringUtils.contains(doi, "%")) {
     * // possible encoded url...
     * try {
     * doi = URLDecoder.decode(doi, StandardCharsets.UTF_8.name());
     * } catch (UnsupportedEncodingException e) {
     * // ...maybe not, nothing we can do.
     * e.printStackTrace();
     * }
     * }
     * if (StringUtils.contains(doi, "doi.org/")) {
     * doi = StringUtils.substringAfter(doi, "doi.org/");
     * } else if (StringUtils.startsWithIgnoreCase(doi, "doi")) {
     * doi = StringUtils.substringAfter(doi, "doi");
     * if (StringUtils.startsWith(doi, ":")) {
     * doi = doi.substring(1);
     * }
     * } else if (StringUtils.isNotBlank(doi) && Character.isLetterOrDigit(doi.charAt(0))) {
     * // continue
     * } else {
     * doi = StringUtils.EMPTY;
     * }
     * if (SHORT_DOI_PATTERN.matcher(doi).matches()) {
     * try {
     * // possible shortDOI...
     * doi = HandleService.getDoiFromShortDoi(doi);
     * } catch (IOException | InvalidHandleException e) {
     * // ... maybe not
     * doi = StringUtils.EMPTY;
     * }
     * }
     */

    // possible leading slash (/)
    return StringUtils.isNotBlank(doi) && !Character.isLetterOrDigit(doi.charAt(0)) ? doi.substring(1) : doi;
  }

}
