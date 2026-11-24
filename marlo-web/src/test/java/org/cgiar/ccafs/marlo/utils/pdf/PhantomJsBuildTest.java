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

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * The platform table (CHG-PPE-T02, FN-001, DD-1): every row asserts the resource, the checksum resource, the file
 * name, the font mode and the environment style the selector returned, or that it returned none. The expected values
 * are written out here, not read from the table.
 */
public class PhantomJsBuildTest {

  private static final String LINUX_GZ = "/pdf/phantomjs/phantomjs-2.1.1-linux-x86_64.gz";
  private static final String LINUX_SHA = "/pdf/phantomjs/phantomjs-2.1.1-linux-x86_64.sha256";
  private static final String MAC_GZ = "/pdf/phantomjs/phantomjs-2.1.1-macosx.gz";
  private static final String MAC_SHA = "/pdf/phantomjs/phantomjs-2.1.1-macosx.sha256";
  private static final String WIN_GZ = "/pdf/phantomjs/phantomjs-2.1.1-windows.exe.gz";
  private static final String WIN_SHA = "/pdf/phantomjs/phantomjs-2.1.1-windows.exe.sha256";

  private static void assertLinux(String os, String arch) {
    PhantomJsBuild build = PhantomJsBuild.forPlatform(os, arch).orElse(null);
    String row = os + "/" + arch;
    assertTrue(row + " has a build", build != null);
    assertEquals(row, LINUX_GZ, build.resource());
    assertEquals(row, LINUX_SHA, build.checksumResource());
    assertEquals(row, "phantomjs", build.fileName());
    assertEquals(row, PhantomJsBuild.FontMode.LINUX_FONTCONFIG, build.fontMode());
    assertEquals(row, PhantomJsBuild.EnvironmentStyle.POSIX, build.environmentStyle());
  }

  private static void assertMac(String os, String arch) {
    PhantomJsBuild build = PhantomJsBuild.forPlatform(os, arch).orElse(null);
    String row = os + "/" + arch;
    assertTrue(row + " has a build", build != null);
    assertEquals(row, MAC_GZ, build.resource());
    assertEquals(row, MAC_SHA, build.checksumResource());
    assertEquals(row, "phantomjs", build.fileName());
    assertEquals(row, PhantomJsBuild.FontMode.PAGE, build.fontMode());
    assertEquals(row, PhantomJsBuild.EnvironmentStyle.POSIX, build.environmentStyle());
  }

  private static void assertWindows(String os, String arch) {
    PhantomJsBuild build = PhantomJsBuild.forPlatform(os, arch).orElse(null);
    String row = os + "/" + arch;
    assertTrue(row + " has a build", build != null);
    assertEquals(row, WIN_GZ, build.resource());
    assertEquals(row, WIN_SHA, build.checksumResource());
    assertEquals(row, "phantomjs.exe", build.fileName());
    assertEquals(row, PhantomJsBuild.FontMode.PAGE, build.fontMode());
    assertEquals(row, PhantomJsBuild.EnvironmentStyle.WINDOWS, build.environmentStyle());
  }

  private static void assertNone(String os, String arch) {
    Optional<PhantomJsBuild> build = PhantomJsBuild.forPlatform(os, arch);
    assertFalse(os + "/" + arch + " has no build", build.isPresent());
  }

  @Test
  public void linuxOnAmd64OrX8664GetsTheLinuxBuildWithTheOriginalResources() {
    assertLinux("Linux", "amd64");
    assertLinux("Linux", "x86_64");
  }

  @Test
  public void linuxOnArmGetsNoBuild() {
    assertNone("Linux", "aarch64");
    assertNone("Linux", "arm");
  }

  @Test
  public void macOsOnIntelOrAppleSiliconGetsTheMacBuild() {
    assertMac("Mac OS X", "x86_64");
    assertMac("Mac OS X", "aarch64");
  }

  @Test
  public void windowsOnAmd64X8664OrX86GetsTheWindowsBuild() {
    assertWindows("Windows 10", "amd64");
    assertWindows("Windows 11", "amd64");
    assertWindows("Windows 11", "x86_64");
    assertWindows("Windows 10", "x86");
  }

  @Test
  public void windowsOnArmGetsNoBuild() {
    assertNone("Windows 11", "aarch64");
  }

  @Test
  public void anyOtherOperatingSystemOrEmptyValuesGetNoBuild() {
    assertNone("FreeBSD", "amd64");
    assertNone("", "");
    assertNone("Linux", "");
    assertNone("", "amd64");
    assertNone(null, null);
  }

  @Test
  public void theMatchingIsCaseInsensitive() {
    assertLinux("LINUX", "AMD64");
    assertSame(PhantomJsBuild.MACOS, PhantomJsBuild.forPlatform("mac os x", "AARCH64").get());
  }
}
