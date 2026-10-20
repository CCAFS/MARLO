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

package org.cgiar.ccafs.marlo.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The same-origin return URL guard shared by the sign-in flows (CHG-OTP-LOGIN-001-T08, FN-011, FN-019),
 * extracted unchanged from {@code CognitoLoginAction}. The only difference is that the MARLO base URL is a
 * parameter instead of instance state.
 * <p>
 * The class is stateless and never throws on malformed input.
 */
public final class ReturnUrls {

  private static final Logger LOG = LoggerFactory.getLogger(ReturnUrls.class);

  /**
   * CHG-COGNITO-AUTH-001-T19 (V-4). A same-origin return URL is still rejected when its normalized path's
   * final segment is one of these, case-insensitively -- see {@link #isAuthenticationEndpoint(String)}. Every
   * entry is lowercase because the comparison lowercases the candidate before matching.
   */
  private static final Set<String> AUTHENTICATION_ENDPOINT_PATHS = Collections
    .unmodifiableSet(new HashSet<>(Arrays.asList("cognitologin.do", "cognitocallback.do", "validateuser.do")));

  private ReturnUrls() {
    throw new AssertionError("utility class, never instantiated");
  }

  /**
   * Keeps a deep link only when it points back into this MARLO instance.
   * <p>
   * <b>This is an open-redirect guard, not tidiness.</b> {@code returnUrl} arrives in the {@code Referer}
   * header of a GET navigation, so the page that links here chooses it -- including a page the attacker
   * controls, which can also set {@code Referrer-Policy: unsafe-url} to defeat the browser's default
   * truncation. It is then bound into the session and handed to {@code finishLogin}, whose only test is
   * {@code urlAction.contains(".do")} -- satisfied by a host such as {@code https://evil.do/}. The victim
   * would authenticate for real at the IdP and be redirected off-site immediately afterwards.
   *
   * @param candidate the raw {@code Referer} value, possibly {@code null}
   * @param baseUrl the base URL of this MARLO instance, possibly {@code null}
   * @return the value when it is same-origin with {@code baseUrl} AND does not target an authentication
   *         endpoint, otherwise {@code null}
   */
  public static String sameOriginOrNull(String candidate, String baseUrl) {
    if (candidate == null || candidate.trim().isEmpty()) {
      return null;
    }
    if (baseUrl == null || baseUrl.trim().isEmpty()) {
      return null;
    }
    // Compared on the origin, and only after a "/" is appended to both, so that a look-alike host such as
    // https://marlo.example.org.evil.com/ cannot pass a bare startsWith against https://marlo.example.org.
    String origin = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    String normalized = candidate.trim();
    if (!normalized.equals(baseUrl) && !normalized.startsWith(origin)) {
      LOG.info("Cognito login: discarding an off-site return URL");
      return null;
    }
    // CHG-COGNITO-AUTH-001-T19 (V-4): same-origin is necessary but not sufficient. A same-origin
    // cognitoCallback.do?code=...&state=... passes the check above just as well as a legitimate deep link,
    // is stored in the PendingAuthorization, and is later handed straight back to this same flow by
    // finishLogin's ".do" heuristic -- redirecting a freshly-authenticated user into a single-use
    // authorization that is already consumed. Rejected here, distinctly logged, exactly as the off-site case
    // above is.
    if (isAuthenticationEndpoint(normalized)) {
      LOG.info("Cognito login: discarding a same-origin return URL that targets an authentication endpoint");
      return null;
    }
    return normalized;
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 (V-4). {@code true} when {@code candidate}'s normalized path resolves to one of
   * {@link #AUTHENTICATION_ENDPOINT_PATHS} -- compared against the final path segment only, never a substring
   * of the whole URL. A substring match is the exact idiom that produced this defect in the first place:
   * {@code finishLogin}'s {@code !urlAction.contains("logout")} is its sibling, and both would reject a
   * legitimate deep link such as {@code notCognitoLogin.do} or one carrying {@code cognitoCallback.do} as a
   * query value.
   * <p>
   * The path comes from a parsed, normalized {@link URI}, not string surgery: {@link URI#normalize()}
   * collapses a {@code ../} traversal, {@link URI#getPath()} returns the already percent-decoded path, and a
   * trailing {@code ;jsessionid=...} matrix parameter is stripped from the final segment before comparison. A
   * candidate this cannot parse is treated as an authentication endpoint -- {@code null} is the safe answer
   * for anything unparseable, and returning {@code true} here is what makes
   * {@link #sameOriginOrNull(String, String)} produce that {@code null} instead of propagating the exception.
   *
   * @param candidate an already same-origin, trimmed, non-null return URL
   * @return {@code true} when the URL must be rejected as an authentication endpoint
   */
  private static boolean isAuthenticationEndpoint(String candidate) {
    String path;
    try {
      path = new URI(candidate).normalize().getPath();
    } catch (URISyntaxException e) {
      LOG.info("Cognito login: discarding a return URL that could not be parsed");
      return true;
    }
    if (path == null || path.isEmpty()) {
      return false;
    }
    String trimmedPath = path;
    while (trimmedPath.length() > 1 && trimmedPath.endsWith("/")) {
      trimmedPath = trimmedPath.substring(0, trimmedPath.length() - 1);
    }
    int lastSlash = trimmedPath.lastIndexOf('/');
    String lastSegment = lastSlash >= 0 ? trimmedPath.substring(lastSlash + 1) : trimmedPath;
    int matrixParam = lastSegment.indexOf(';');
    if (matrixParam >= 0) {
      lastSegment = lastSegment.substring(0, matrixParam);
    }
    return AUTHENTICATION_ENDPOINT_PATHS.contains(lastSegment.toLowerCase(Locale.ROOT));
  }
}
