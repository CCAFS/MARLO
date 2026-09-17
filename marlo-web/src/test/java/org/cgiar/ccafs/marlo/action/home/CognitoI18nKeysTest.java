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

package org.cgiar.ccafs.marlo.action.home;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-COGNITO-AUTH-001-T13: every i18n key the login flow can render must resolve in
 * {@code global.properties} (design.md 9.4, DD-7).
 * <p>
 * <b>Discovery, not enumeration (tasks.md T13's own <i>Not evidence when</i> clause).</b> A test that reads
 * {@code global.properties} and asserts its own keys exist is circular -- it can never fail. This test instead
 * reads the <b>source</b>: the two new Cognito actions, the complete {@code LoginAction} (which the Cognito
 * callback shares its {@code finishLogin} tail with -- design.md DD-6), the one collaborator that hands a key
 * back at runtime rather than as a literal, and the login page templates. Whatever key string those call sites
 * can produce is the set this test checks -- nothing is read back from the properties file to build that set.
 * <p>
 * <b>Per-source guard (audit finding 1, 2026-09-02).</b> A single aggregate "did we find anything at all"
 * check cannot see one source going silent -- if a future refactor hoists {@code CognitoLoginAction}'s
 * refusal keys into a constant collection read from elsewhere, that file would contribute zero keys while the
 * other five sources still make the aggregate non-empty, and the two keys that file exists to protect would be
 * checked by nothing, forever, green. {@link #requireKeys(String, String, Pattern)} asserts non-emptiness
 * <b>per source</b>, naming the source that went silent; the aggregate check below it is kept only as a
 * backstop, not as the guard doing the real work.
 * <p>
 * <b>Comment stripping is string-literal-aware (audit finding 3, 2026-09-02).</b> A naive {@code //} or
 * {@code /* ... *}{@code /} regex would delete part of any line holding a URL literal such as
 * {@code "https://" + ...} -- both {@code CognitoCallbackAction} and {@code CognitoLoginAction} have one.
 * {@link #stripJavaComments(String)} walks the source character by character and never treats a {@code /}
 * inside a quoted string or char literal as the start of a comment, so a key sharing a line with such a
 * literal is never silently dropped.
 */
public class CognitoI18nKeysTest {

  private static final String GLOBAL_PROPERTIES = "src/main/resources/global.properties";

  private static final String COGNITO_LOGIN_ACTION =
    "src/main/java/org/cgiar/ccafs/marlo/action/home/CognitoLoginAction.java";

  private static final String COGNITO_CALLBACK_ACTION =
    "src/main/java/org/cgiar/ccafs/marlo/action/home/CognitoCallbackAction.java";

  /**
   * Scanned in full (no method-level narrowing). {@code login.error.userOrPass}, referenced at {@code :285} in
   * the LOCAL-only {@code login()} entry point, was judged by the user 2026-09-02 to be the same defect class
   * T13 exists to close -- defined only in {@code custom/ciat.properties}, so every other Global Unit rendered
   * the raw key -- and is now added to {@code global.properties} alongside {@code invalidUserCrp}. Nothing
   * about this file's scan is narrowed to "the Cognito path only" any more; any {@code login.*} literal it
   * contains is expected to resolve.
   */
  private static final String LOGIN_ACTION = "src/main/java/org/cgiar/ccafs/marlo/action/home/LoginAction.java";

  /**
   * Lives in the sibling {@code marlo-data} module. {@code CognitoIdentityMapper.RejectionReason#toMessageKey()}
   * is the one call site in this flow where {@code getText(...)} receives a key resolved at runtime
   * ({@code mapping.getRejectionReason().toMessageKey()} in {@code CognitoCallbackAction}) rather than a string
   * literal -- so the literal never appears anywhere in {@code marlo-web}. Its two possible return values are
   * themselves literals inside this file, which is why it must be scanned too: skipping it would silently drop
   * {@code login.error.inactive} from the discovered set.
   */
  private static final String COGNITO_IDENTITY_MAPPER =
    "../marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/CognitoIdentityMapper.java";

  private static final String LOGIN_FORM_FTL = "src/main/webapp/WEB-INF/global/pages/loginForm.ftl";

  /**
   * Overrides {@code loginForm.ftl}'s {@code loginHeadlineKey} default with a bare quoted literal
   * ({@code [#assign loginHeadlineKey = "login.headline.unauthorized" /]}) -- audit finding 2, 2026-09-02.
   * Included so that override is checked too, not just the default it replaces.
   */
  private static final String ERROR_401_FTL = "src/main/webapp/WEB-INF/global/pages/error/401.ftl";

  /**
   * A quoted string literal starting with {@code login.}, e.g. {@code "login.error.cognitoFailed"}. Used for
   * both Java (after comment stripping) and FTL (as-is) source, and deliberately <b>not</b> anchored to any
   * particular attribute name or call ({@code getText(...)}, {@code refuse(...)}, {@code name="..."},
   * {@code key="..."}, an {@code [#assign]} default). Audit finding 2 (2026-09-02) found two call-site shapes
   * an attribute-specific pattern missed ({@code [@s.submit key="login.logIn" .../]} and the bare
   * {@code !"login.headline"} FreeMarker default); a shape-agnostic literal scan closes that class of miss
   * rather than one more instance of it.
   */
  private static final Pattern KEY_LITERAL = Pattern.compile("\"(login\\.[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)*)\"");

  private static String readFile(String relativePath) {
    File file = new File(relativePath);
    try {
      return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new AssertionError("could not read " + file.getAbsolutePath() + ": " + e, e);
    }
  }

  /**
   * Removes {@code //} and {@code /* ... *}{@code /} comments so a key mentioned only in dead code (there is
   * one: {@code LoginAction}'s commented-out duplicate-session block references {@code login.error.duplicated})
   * is not required to resolve on the strength of a comment alone.
   * <p>
   * <b>String-literal-aware (audit finding 3, 2026-09-02).</b> Walks the source one character at a time and
   * tracks whether it is inside a {@code "..."} or {@code '...'} literal (honoring {@code \} escapes); a
   * {@code /} encountered there is copied through, never treated as the start of a comment. A regex-based
   * stripper does not know that distinction: {@code CognitoCallbackAction}'s
   * {@code URI.create("https://" + ...)} and {@code CognitoLoginAction}'s {@code new StringBuilder("https://")}
   * both contain a {@code //} sequence inside a string literal that a plain {@code //[^\r\n]*} pattern would
   * delete from that point to the end of the line -- silent today only because neither line also carries a key.
   */
  private static String stripJavaComments(String source) {
    StringBuilder result = new StringBuilder(source.length());
    int length = source.length();
    int i = 0;
    while (i < length) {
      char current = source.charAt(i);

      if (current == '"' || current == '\'') {
        char quote = current;
        result.append(current);
        i++;
        while (i < length) {
          char inner = source.charAt(i);
          if (inner == '\\' && i + 1 < length) {
            result.append(inner).append(source.charAt(i + 1));
            i += 2;
            continue;
          }
          result.append(inner);
          i++;
          if (inner == quote) {
            break;
          }
        }
        continue;
      }

      if (current == '/' && i + 1 < length && source.charAt(i + 1) == '/') {
        i += 2;
        while (i < length && source.charAt(i) != '\n') {
          i++;
        }
        continue;
      }

      if (current == '/' && i + 1 < length && source.charAt(i + 1) == '*') {
        i += 2;
        while (i + 1 < length && !(source.charAt(i) == '*' && source.charAt(i + 1) == '/')) {
          i++;
        }
        i = Math.min(i + 2, length);
        continue;
      }

      result.append(current);
      i++;
    }
    return result.toString();
  }

  private static Set<String> extractKeys(String text, Pattern pattern) {
    Set<String> keys = new LinkedHashSet<String>();
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      keys.add(matcher.group(1));
    }
    return keys;
  }

  /**
   * Extracts the keys {@code pattern} finds in {@code text} and asserts, by itself, that {@code label}
   * contributed at least one -- see the class javadoc on why an aggregate-only check is not enough (audit
   * finding 1, 2026-09-02). A source that goes silent fails here, naming itself, instead of hiding behind five
   * other sources that still have keys.
   */
  private static Set<String> requireKeys(String label, String text, Pattern pattern) {
    Set<String> keys = extractKeys(text, pattern);
    assertFalse("the scan of " + label + " discovered zero i18n keys -- this source has stopped contributing "
      + "to the discovered set. A file move, a call-site rewrite that no longer uses a quoted literal, or a "
      + "regex that stopped matching would all produce exactly this, silently, if only checked in aggregate",
      keys.isEmpty());
    return keys;
  }

  private static Properties loadGlobalProperties() {
    Properties properties = new Properties();
    File file = new File(GLOBAL_PROPERTIES);
    try (InputStream in = Files.newInputStream(file.toPath())) {
      properties.load(in);
    } catch (IOException e) {
      throw new AssertionError("could not read " + file.getAbsolutePath() + ": " + e, e);
    }
    return properties;
  }

  @Test
  public void everyLoginI18nKeyResolvesInGlobalProperties() {
    Set<String> discovered = new TreeSet<String>();

    discovered.addAll(requireKeys(COGNITO_LOGIN_ACTION, stripJavaComments(readFile(COGNITO_LOGIN_ACTION)),
      KEY_LITERAL));
    discovered.addAll(requireKeys(COGNITO_CALLBACK_ACTION, stripJavaComments(readFile(COGNITO_CALLBACK_ACTION)),
      KEY_LITERAL));
    discovered.addAll(requireKeys(LOGIN_ACTION, stripJavaComments(readFile(LOGIN_ACTION)), KEY_LITERAL));
    discovered.addAll(requireKeys(COGNITO_IDENTITY_MAPPER, stripJavaComments(readFile(COGNITO_IDENTITY_MAPPER)),
      KEY_LITERAL));
    discovered.addAll(requireKeys(LOGIN_FORM_FTL, readFile(LOGIN_FORM_FTL), KEY_LITERAL));
    discovered.addAll(requireKeys(ERROR_401_FTL, readFile(ERROR_401_FTL), KEY_LITERAL));

    // Backstop only -- each source above already asserted its own non-emptiness. Kept in case a future source
    // is added here without going through requireKeys.
    assertFalse("the source/FTL scan discovered zero i18n keys in aggregate -- this test has stopped checking "
      + "anything; the scanned files or the discovery pattern have drifted from the real call sites", discovered
        .isEmpty());

    Properties globalProperties = loadGlobalProperties();
    Set<String> missing = new TreeSet<String>();
    for (String key : discovered) {
      // containsKey alone would let a key that resolves to an empty string pass -- it would render nothing to
      // the user, which is exactly the failure hard rule 8 exists to prevent (audit advisory, 2026-09-02).
      String value = globalProperties.getProperty(key);
      if (value == null || value.trim().isEmpty()) {
        missing.add(key);
      }
    }

    assertTrue("the following i18n key(s), referenced by the login flow, do not resolve to a non-blank value "
      + "in " + GLOBAL_PROPERTIES + ": " + missing + " (discovered " + discovered.size() + " key(s) total from "
      + "source: " + discovered + ")", missing.isEmpty());
  }
}
