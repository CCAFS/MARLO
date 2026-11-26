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

package org.cgiar.ccafs.marlo.utils.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Resource integrity of the PhantomJS builds that ship inside MARLO (CHG-PPE-T01, SEC-001): for each build, the
 * gzip resource, once decompressed, hashes to the value in its {@code .sha256} file. Resources are loaded from the
 * classpath, as the application does.
 */
@RunWith(Parameterized.class)
public class ShippedPhantomJsBuildsTest {

  private static final String BASE = "/pdf/phantomjs/";

  @Parameters(name = "{0}")
  public static Collection<Object[]> builds() {
    return Arrays.asList(new Object[][] {{"phantomjs-2.1.1-linux-x86_64"}, {"phantomjs-2.1.1-macosx"},
      {"phantomjs-2.1.1-windows.exe"}});
  }

  private final String build;

  public ShippedPhantomJsBuildsTest(String build) {
    this.build = build;
  }

  @Test
  public void decompressedBinaryMatchesItsChecksumFile() throws Exception {
    String expected;
    try (InputStream in = open(build + ".sha256")) {
      String line = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
      expected = line.split("\\s+")[0];
    }
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    try (InputStream in = new GZIPInputStream(open(build + ".gz"))) {
      byte[] buffer = new byte[65536];
      int read;
      while ((read = in.read(buffer)) != -1) {
        digest.update(buffer, 0, read);
      }
    }
    StringBuilder actual = new StringBuilder();
    for (byte b : digest.digest()) {
      actual.append(String.format("%02x", b));
    }
    assertEquals("decompressed " + build, expected, actual.toString());
  }

  private InputStream open(String name) throws IOException {
    InputStream in = ShippedPhantomJsBuildsTest.class.getResourceAsStream(BASE + name);
    assertNotNull("missing classpath resource " + BASE + name, in);
    return in;
  }
}
