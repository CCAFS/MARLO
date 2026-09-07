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

package org.cgiar.ccafs.marlo.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-COGNITO-AUTH-001-SEC-004, second clause: <b>no Cognito user-pool id, app-client id, client secret,
 * hosted-UI domain or callback URL may appear as a literal in any {@code .java} source file.</b> Added by
 * {@code /akili-test}'s coverage extension to close {@code test-report.md} 8 finding 4.
 * <p>
 * <b>Why it exists.</b> {@code design.md} D-6 names an automated grep as the control for this clause and no
 * such test was ever written. The clause was therefore held only by a grep somebody ran by hand: the tree was
 * clean on the day it was checked and would have stayed silently clean-looking on every day afterwards,
 * including the day a debugging session pasted a real pool id into a constant and forgot to take it out.
 * Every other SEC-004 control ({@code APConfigCognitoDefaultsTest}) proves the settings <i>can</i> come from
 * {@code marlo-<profile>.properties}; nothing proved they are not <i>also</i> sitting in the source.
 * <p>
 * <b>String literals only, deliberately -- and comments deliberately not.</b> A "literal" here means the
 * contents of a Java string literal, which is the only place a credential can sit and actually be
 * <i>used</i>. Two measurements decided that boundary rather than taste:
 * <ul>
 * <li>Scanning raw source makes ordinary Java identifiers match the shape patterns
 * ({@code deliverablemetadataelement} is exactly 26 lowercase characters), so a raw scan would be a
 * permanent false alarm rather than a control.</li>
 * <li>Scanning comments would fail on {@code CrpSiteIntegrationAction:658} today, where a javadoc line cites
 * a 40-character git commit SHA. A commit SHA is not a leaked credential, and a standing test that fails on a
 * pre-existing, unrelated line is a test people delete.</li>
 * </ul>
 * <b>The stated limitation that follows from it:</b> a credential pasted into a comment is not caught. That is
 * a smaller hole than the one this test closes, and it is recorded here rather than papered over.
 * <p>
 * <b>Self-guards, because a scanner that finds nothing passes forever</b> (the failure mode
 * {@code StrutsConfigurationWellFormedTest} guards against, and the reason that test is trustworthy). Three
 * of them: every root must contribute at least one file, the whole scan must reach a floor of files far below
 * today's count but far above a mis-rooted one, and a non-zero number of string literals must actually have
 * been extracted -- a walker that silently returned nothing would otherwise satisfy the first two. A fourth,
 * {@link #everyPatternStillMatchesTheShapeItIsSupposedToCatch}, holds the patterns themselves to the same
 * standard: a regex that stopped matching would leave this file green while checking nothing.
 * <p>
 * <b>The patterns do not match their own source.</b> Each is written with the regex metacharacters that make
 * it a pattern ({@code \b}, {@code \.}, character classes, quantifiers) rather than as an example value, so
 * no line of this file is itself a match -- and {@code src/test} is outside the scanned roots in any case.
 * The representative values in the guard test below are assembled by concatenation at run time for the same
 * reason.
 */
public class CognitoCredentialLiteralScanTest {

  /**
   * Relative to the module root Surefire runs in ({@code marlo-web}), matching the convention
   * {@code CognitoI18nKeysTest} and {@code APConfigCognitoDefaultsTest} already use. {@code marlo-data} holds
   * the security package and {@code marlo-utils} holds {@code APConfig} itself, so both are in scope --
   * limiting the scan to {@code marlo-web} would leave the class that actually reads the settings unchecked.
   */
  private static final String[] SOURCE_ROOTS =
    {"src/main/java", "../marlo-data/src/main/java", "../marlo-utils/src/main/java"};

  /**
   * Far below the 3458 files present when this test was written, far above what a broken root walk would
   * reach. A floor rather than an exact count: this test must not need editing every time a class is added.
   */
  private static final int MINIMUM_FILES_SCANNED = 1000;

  /**
   * An AWS Cognito user-pool id: the region, an underscore, then the pool suffix -- for example the shape
   * {@code <region>_<9 alphanumerics>}. The embedded region is what makes this specific: a bare
   * {@code "https://cognito-idp."} prefix that production concatenates a configured region onto is a
   * template, not a credential, and must not match.
   */
  private static final Pattern USER_POOL_ID = Pattern.compile("\\b[a-z]{2}-[a-z]+-[0-9]_[A-Za-z0-9]{6,}\\b");

  /**
   * A Cognito app-client id: 26 characters of lowercase alphanumerics. The lookahead requiring at least one
   * digit is what keeps ordinary lowercase words out; a randomly generated 26-character base-32 id without a
   * single digit occurs about twice in ten thousand, and that residue is preferred to a test that cries wolf.
   */
  private static final Pattern CLIENT_ID = Pattern.compile("\\b(?=[a-z0-9]*[0-9])[a-z0-9]{26}\\b");

  /**
   * A Cognito app-client secret: a long run of lowercase alphanumerics containing at least one digit, the
   * shape Cognito issues (52 characters at the time of writing; the range is widened so a change in length
   * does not silently disable the check). Mixed case is deliberately excluded -- {@code [A-Za-z0-9]{40,64}}
   * matches long CamelCase Java identifiers such as {@code ReportSynthesisFlagshipProgressDeliverable}, which
   * appear inside string literals in this repository.
   */
  private static final Pattern CLIENT_SECRET = Pattern.compile("\\b(?=[a-z0-9]*[0-9])[a-z0-9]{40,64}\\b");

  /**
   * A fully-formed Cognito host: either the hosted-UI domain or an issuer/JWKS host with the region baked
   * into it. Both name a specific deployment; neither can be produced by concatenating a configured value
   * onto a constant prefix, which is exactly how {@code CognitoCallbackAction} and {@code CognitoLoginAction}
   * legitimately build their URLs today.
   */
  private static final Pattern COGNITO_HOST =
    Pattern.compile("amazoncognito\\.com|cognito-idp\\.[a-z0-9-]+\\.amazonaws\\.com");

  /**
   * An absolute URL pointing at MARLO's Cognito callback action. The scheme and host are what make it a
   * deployment-specific callback URL rather than the bare Struts action name, which is not a credential and
   * must not match.
   */
  private static final Pattern CALLBACK_URL = Pattern.compile("https?://[^\\s\"]*[cC]ognitoCallback");

  private static final Map<String, Pattern> FORBIDDEN = forbiddenPatterns();

  private static Map<String, Pattern> forbiddenPatterns() {
    Map<String, Pattern> patterns = new LinkedHashMap<String, Pattern>();
    patterns.put("Cognito user-pool id", USER_POOL_ID);
    patterns.put("Cognito app-client id", CLIENT_ID);
    patterns.put("Cognito app-client secret", CLIENT_SECRET);
    patterns.put("Cognito domain / issuer host", COGNITO_HOST);
    patterns.put("Cognito callback URL", CALLBACK_URL);
    return patterns;
  }

  private static List<File> javaFilesUnder(File root) {
    List<File> found = new ArrayList<File>();
    File[] children = root.listFiles();
    if (children == null) {
      return found;
    }
    for (File child : children) {
      if (child.isDirectory()) {
        found.addAll(javaFilesUnder(child));
      } else if (child.getName().endsWith(".java")) {
        found.add(child);
      }
    }
    return found;
  }

  private static String readFile(File file) {
    try {
      return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new AssertionError("could not read " + file.getAbsolutePath() + ": " + e, e);
    }
  }

  /**
   * Walks Java source one character at a time and returns the contents of every string literal, skipping
   * {@code //} and {@code /* ... *}{@code /} comments and stepping over character literals so a {@code '"'}
   * cannot desynchronise the walk. A regex cannot do this: it has no way to know whether a given quote opens
   * a literal, closes one, sits inside a comment, or is escaped.
   */
  private static List<String> stringLiteralsIn(String source) {
    List<String> literals = new ArrayList<String>();
    int length = source.length();
    int i = 0;
    while (i < length) {
      char current = source.charAt(i);

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

      if (current == '\'') {
        i++;
        while (i < length && source.charAt(i) != '\'') {
          i += source.charAt(i) == '\\' ? 2 : 1;
        }
        i++;
        continue;
      }

      if (current == '"') {
        StringBuilder literal = new StringBuilder();
        i++;
        while (i < length && source.charAt(i) != '"') {
          if (source.charAt(i) == '\\' && i + 1 < length) {
            literal.append(source.charAt(i)).append(source.charAt(i + 1));
            i += 2;
            continue;
          }
          literal.append(source.charAt(i));
          i++;
        }
        i++;
        literals.add(literal.toString());
        continue;
      }

      i++;
    }
    return literals;
  }

  /**
   * The whole detector -- extract this source's string literals, match every forbidden pattern against each
   * one, and record what was found -- in one method, so the scan over the real tree and the guard test that
   * proves the detector detects are running <b>the same code</b>. A guard test that re-implemented the
   * matching would only prove its own copy works.
   *
   * @return the number of string literals examined
   */
  private static int collectViolations(String label, String source, Set<String> violations) {
    int literalsScanned = 0;
    for (String literal : stringLiteralsIn(source)) {
      literalsScanned++;
      for (Map.Entry<String, Pattern> forbidden : FORBIDDEN.entrySet()) {
        Matcher matcher = forbidden.getValue().matcher(literal);
        if (matcher.find()) {
          violations.add(label + " -- " + forbidden.getKey() + ": \"" + matcher.group() + "\"");
        }
      }
    }
    return literalsScanned;
  }

  /**
   * The D-6 control itself. <b>If this fails, a Cognito credential is sitting in the source tree</b> -- the
   * failure message names the file, the kind of credential, and the offending literal, and the fix is to move
   * the value into {@code marlo-<profile>.properties} (or the deployment's secret store, for the client
   * secret) and rotate it, never to loosen the pattern.
   */
  @Test
  public void noCognitoCredentialAppearsAsALiteralInAnyJavaSource() {
    int filesScanned = 0;
    int literalsScanned = 0;
    Set<String> violations = new LinkedHashSet<String>();

    for (String root : SOURCE_ROOTS) {
      File rootDirectory = new File(root);
      assertTrue("the source root " + rootDirectory.getAbsolutePath() + " does not exist -- this test is "
        + "pointed at the wrong place and would otherwise pass by scanning nothing", rootDirectory.isDirectory());

      List<File> sources = javaFilesUnder(rootDirectory);
      assertFalse("the source root " + rootDirectory.getAbsolutePath() + " contributed zero .java files -- a "
        + "module move would silently remove it from this scan while the other roots kept the total non-zero",
        sources.isEmpty());

      for (File source : sources) {
        filesScanned++;
        literalsScanned += collectViolations(source.getPath(), readFile(source), violations);
      }
    }

    // Self-guards: a scanner that found no files, or found files but extracted no literals from them, would
    // report a clean tree forever without ever having looked at one.
    assertTrue("only " + filesScanned + " .java file(s) were scanned; expected at least " + MINIMUM_FILES_SCANNED
      + " -- the scan has collapsed and is no longer checking the tree", filesScanned >= MINIMUM_FILES_SCANNED);
    assertTrue("zero string literals were extracted from " + filesScanned + " file(s) -- the literal walker "
      + "has stopped working and every assertion below it is vacuous", literalsScanned > 0);

    assertTrue("SEC-004: Cognito credential literal(s) found in Java source. Move the value into "
      + "marlo-<profile>.properties (the client secret into the deployment's secret store), then ROTATE it -- "
      + "it is in version control history. Scanned " + filesScanned + " file(s), " + literalsScanned
      + " literal(s). Offender(s): " + violations, violations.isEmpty());
  }

  /**
   * Guards the guard, the way {@code CognitoI18nKeysTest#requireKeys} does for its own discovery pattern: a
   * regex that silently stopped matching -- a typo, an over-tightened character class, a quantifier edited
   * during a refactor -- would leave the test above permanently, meaninglessly green.
   * <p>
   * The representative values are assembled by concatenation rather than written out, so that no
   * credential-shaped literal exists in this file even in the abstract. They are structurally realistic and
   * entirely invented; none of them is, or resembles, a value from MARLO's Cognito pool.
   */
  @Test
  public void everyPatternStillMatchesTheShapeItIsSupposedToCatch() {
    assertTrue("the user-pool id pattern no longer matches a user-pool id",
      USER_POOL_ID.matcher("us-east-1" + "_" + "Ab3dEf9hJ").find());
    assertTrue("the app-client id pattern no longer matches an app-client id",
      CLIENT_ID.matcher("1h57kf5cpparf3m9i1qhsm" + "4d0e").find());
    assertTrue("the client-secret pattern no longer matches a client secret",
      CLIENT_SECRET.matcher("1ab2cd3ef4gh5ij6kl7mn8op9qr0st1uv2wx3yz4" + "ab5cd6ef7gh8").find());
    assertTrue("the domain pattern no longer matches a hosted-UI domain",
      COGNITO_HOST.matcher("marlo-example.auth.us-east-1." + "amazoncognito" + ".com").find());
    assertTrue("the domain pattern no longer matches an issuer host",
      COGNITO_HOST.matcher("https://cognito-idp.us-east-1." + "amazonaws" + ".com/us-east-1_Ab3dEf9hJ").find());
    assertTrue("the callback-url pattern no longer matches a callback URL",
      CALLBACK_URL.matcher("https://marlo.example.org/" + "cognitoCallback" + ".do").find());

    // And the other half of a useful pattern: the templates production legitimately builds URLs from must
    // NOT match, or this test would be unfixable without deleting the production code it is protecting.
    assertFalse("a bare scheme+prefix template is not a credential and must not match",
      COGNITO_HOST.matcher("https://cognito-idp.").find());
    assertFalse("the bare Struts action name is not a credential and must not match",
      CALLBACK_URL.matcher("cognitoCallback.do").find());
    assertFalse("an ordinary long CamelCase identifier is not a secret",
      CLIENT_SECRET.matcher("ReportSynthesisFlagshipProgressDeliverableManagerImpl").find());

    assertEquals("all five credential kinds must remain wired into the scan above", 5, FORBIDDEN.size());
  }

  /**
   * <b>The detector, end to end, on source it is handed rather than source it happens to find.</b> The two
   * tests above prove the scan reaches 3458 files and that each pattern still matches its shape; neither
   * proves the glue between them -- walk the source, hand each literal to each pattern, record what matched.
   * A literal walker that returned an empty list, a violation set that was never added to, or a
   * {@code find()} whose result was dropped would leave both of them green and this repository's SEC-004
   * control permanently inert.
   * <p>
   * Rather than writing a credential into the real tree and deleting it afterwards -- a probe that mutates
   * production source and can be left behind by a killed turn -- the probe source is built here as a string
   * and run through {@link #collectViolations(String, String, Set)}, the same method
   * {@link #noCognitoCredentialAppearsAsALiteralInAnyJavaSource} uses over every real file.
   * <p>
   * The second half pins the boundary this test deliberately draws: a credential in a <b>comment</b> is not
   * reported. That is the documented, measured limitation (see the class javadoc on
   * {@code CrpSiteIntegrationAction:658}), and pinning it here means a future change to the walker cannot
   * silently widen or narrow it without someone reading this assertion first.
   */
  @Test
  public void theDetectorActuallyReportsACredentialPlantedInSource() {
    String pool = "us-east-1" + "_" + "Zz9qWe1Rt";
    String clientId = "1h57kf5cpparf3m9i1qhsm" + "4d0e";
    String secret = "1ab2cd3ef4gh5ij6kl7mn8op9qr0st1uv2wx3yz4" + "ab5cd6ef7gh8";
    String domain = "marlo-probe.auth.us-east-1." + "amazoncognito" + ".com";
    String callback = "https://marlo.example.org/" + "cognitoCallback" + ".do";

    StringBuilder plantedSource = new StringBuilder();
    plantedSource.append("package org.cgiar.ccafs.marlo.utils;\n");
    plantedSource.append("public final class Leak {\n");
    plantedSource.append("  static final String POOL = \"").append(pool).append("\";\n");
    plantedSource.append("  static final String CLIENT = \"").append(clientId).append("\";\n");
    plantedSource.append("  static final String SECRET = \"").append(secret).append("\";\n");
    plantedSource.append("  static final String DOMAIN = \"").append(domain).append("\";\n");
    plantedSource.append("  static final String CALLBACK = \"").append(callback).append("\";\n");
    plantedSource.append("}\n");

    Set<String> violations = new LinkedHashSet<String>();
    int literals = collectViolations("Leak.java", plantedSource.toString(), violations);

    assertEquals("the walker must find the five planted literals", 5, literals);
    assertEquals("all five planted credentials must be reported, one line each: " + violations, 5,
      violations.size());
    for (String kind : FORBIDDEN.keySet()) {
      boolean reported = false;
      for (String violation : violations) {
        if (violation.contains(kind)) {
          reported = true;
          break;
        }
      }
      assertTrue("the detector did not report the planted " + kind + "; reported: " + violations, reported);
    }

    // Clean source: the templates production really uses, and a Struts action name. None may be reported,
    // or the scan above could not stay green over a tree that legitimately contains them.
    StringBuilder cleanSource = new StringBuilder();
    cleanSource.append("public final class Clean {\n");
    cleanSource.append("  static final String PREFIX = \"https://cognito-idp.\";\n");
    cleanSource.append("  static final String ACTION = \"cognitoCallback.do\";\n");
    cleanSource.append("  static final String MANAGER = \"ReportSynthesisFlagshipProgressDeliverableManager\";\n");
    cleanSource.append("}\n");

    Set<String> cleanViolations = new LinkedHashSet<String>();
    collectViolations("Clean.java", cleanSource.toString(), cleanViolations);
    assertTrue("the templates production legitimately concatenates must never be reported: " + cleanViolations,
      cleanViolations.isEmpty());

    // The documented boundary: comments are not scanned, so a credential in one is NOT caught. Recorded as
    // an assertion so the limitation is visible and cannot drift unnoticed.
    Set<String> commentViolations = new LinkedHashSet<String>();
    collectViolations("Commented.java", "// a pool id in a comment: " + pool + "\nclass C { }\n",
      commentViolations);
    assertTrue("stated limitation: a credential inside a comment is deliberately NOT reported -- see the "
      + "class javadoc for the measurement that decided this boundary", commentViolations.isEmpty());
  }
}
