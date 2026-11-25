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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests which classpath resources the PDF engine opens (CHG-PPE-NF-003, FN-005, design DD-1 and DD-3): the shipped
 * build of the current platform and nothing of the other platforms, and the refusal of a render whose DejaVu resource
 * is missing. The engine's classes are loaded by {@link IsolatedPdfClassLoader}, which records each resource name they
 * open and can hide one; the expected names are literals from the build table of design DD-1, not read from
 * {@link PhantomJsBuild}.
 */
public class PhantomJsResourceAccessTest {

  private static final String PACKAGE = "org.cgiar.ccafs.marlo.utils.pdf.";

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private static byte[] gzip(byte[] content) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GZIPOutputStream gz = new GZIPOutputStream(out)) {
      gz.write(content);
    }
    return out.toByteArray();
  }

  private static String sha256(byte[] content) throws Exception {
    StringBuilder hex = new StringBuilder();
    for (byte b : MessageDigest.getInstance("SHA-256").digest(content)) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }

  private static Object invoke(Object target, String name, Class<?>[] types, Object... args) throws Exception {
    Method method = target.getClass().getDeclaredMethod(name, types);
    method.setAccessible(true);
    return method.invoke(target, args);
  }

  /**
   * {@code EmbeddedPhantomJs.system()} as it runs on {@code os}/{@code arch}. The two JVM properties it reads are set
   * for that one call only and restored before anything is extracted.
   */
  private static Object systemOn(ClassLoader loader, String os, String arch) throws Exception {
    Method system = Class.forName(PACKAGE + "EmbeddedPhantomJs", true, loader).getDeclaredMethod("system");
    system.setAccessible(true);
    String hostOs = System.getProperty("os.name");
    String hostArch = System.getProperty("os.arch");
    System.setProperty("os.name", os);
    System.setProperty("os.arch", arch);
    try {
      return system.invoke(null);
    } finally {
      System.setProperty("os.name", hostOs);
      System.setProperty("os.arch", hostArch);
    }
  }

  // ---- NF-003: only the current platform's build ----------------------------------------------------------------

  @Test
  public void eachPlatformOpensOnlyItsOwnBuildAndChecksumAndNothingOfTheOtherPlatforms() throws Exception {
    String base = "pdf/phantomjs/phantomjs-2.1.1-";
    String[][] rows = {{"Linux", "amd64", base + "linux-x86_64.sha256", base + "linux-x86_64.gz"},
      {"Mac OS X", "aarch64", base + "macosx.sha256", base + "macosx.gz"},
      {"Windows 11", "amd64", base + "windows.exe.sha256", base + "windows.exe.gz"}};
    for (String[] row : rows) {
      IsolatedPdfClassLoader loader = new IsolatedPdfClassLoader();
      Object embedded = systemOn(loader, row[0], row[1]);
      assertEquals(row[0] + ": building it reads only its own checksum", Arrays.asList(row[2]), loader.requested());
      Path file = (Path) invoke(embedded, "extractTo", new Class<?>[] {Path.class}, temp.newFolder().toPath());
      assertTrue(Files.isRegularFile(file));
      assertEquals(row[0] + ": extracting it opens only its own build, never another platform's nor the libraries",
        Arrays.asList(row[2], row[3]), loader.requested());
    }
  }

  @Test
  public void aPlatformWithNoBuildOpensNoResourceAtAll() throws Exception {
    IsolatedPdfClassLoader loader = new IsolatedPdfClassLoader();
    Object embedded = systemOn(loader, "Linux", "aarch64");
    try {
      invoke(embedded, "extractTo", new Class<?>[] {Path.class}, temp.newFolder().toPath());
      fail("extracted a build on a platform that has none");
    } catch (InvocationTargetException e) {
      assertTrue(String.valueOf(e.getCause()), e.getCause() instanceof IOException);
    }
    assertEquals("not even an x86_64 build or checksum", new ArrayList<String>(), loader.requested());
  }

  // ---- FN-005 / DD-3: the page fonts are not optional -----------------------------------------------------------

  /** A renderer of the isolated classes for {@code os}/{@code arch}, whose shipped build is the fake script. */
  private static Object rendererOn(ClassLoader loader, FakePhantomJs fake, String os, String arch) throws Exception {
    Class<?> embeddedClass = Class.forName(PACKAGE + "EmbeddedPhantomJs", true, loader);
    Class<?> sourceClass = Class.forName(PACKAGE + "EmbeddedPhantomJs$Source", true, loader);
    final byte[] script = Files.readAllBytes(Path.of(fake.path()));
    final byte[] compressed = gzip(script);
    Object source = Proxy.newProxyInstance(loader, new Class<?>[] {sourceClass}, new InvocationHandler() {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
        if ("open".equals(method.getName())) {
          return new ByteArrayInputStream(compressed);
        }
        if ("equals".equals(method.getName())) {
          return proxy == args[0];
        }
        return "hashCode".equals(method.getName()) ? (Object) System.identityHashCode(proxy) : "fake source";
      }
    });
    Constructor<?> embeddedConstructor =
      embeddedClass.getDeclaredConstructor(String.class, String.class, String.class, sourceClass);
    embeddedConstructor.setAccessible(true);
    Object embedded = embeddedConstructor.newInstance(os, arch, sha256(script), source);
    Constructor<?> rendererConstructor =
      Class.forName(PACKAGE + "PhantomJsRenderer", true, loader).getDeclaredConstructor(String.class, embeddedClass);
    rendererConstructor.setAccessible(true);
    return rendererConstructor.newInstance("", embedded);
  }

  private static Object render(Object renderer) throws Exception {
    return invoke(renderer, "render", new Class<?>[] {String.class, Map.class, long.class}, "<p>x</p>", null,
      System.nanoTime() + TimeUnit.SECONDS.toNanos(20L));
  }

  @Test
  public void aMissingDejaVuResourceRefusesTheRenderAndPhantomJsNeverRunsWithoutTheFonts() throws Exception {
    Assume.assumeFalse("the fake is a shell script", System.getProperty("os.name", "").startsWith("Windows"));
    // Control: the same rig with every resource present renders, so the refusal below is the missing font's.
    FakePhantomJs control = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    Object controlRenderer = rendererOn(new IsolatedPdfClassLoader(), control, "Mac OS X", "aarch64");
    try {
      render(controlRenderer);
      assertEquals("the control rendered once", 1, control.started().size());
    } finally {
      invoke(controlRenderer, "close", new Class<?>[0]);
    }

    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    IsolatedPdfClassLoader loader = new IsolatedPdfClassLoader();
    Object renderer = rendererOn(loader, fake, "Mac OS X", "aarch64");
    try {
      // The support directory is made while the fonts are there; only the page-font block meets the missing one.
      invoke(renderer, "prepare", new Class<?>[0]);
      loader.hide("pdf/fonts/DejaVuSans.ttf");
      int before = loader.requested().size();
      try {
        render(renderer);
        fail("rendered without the DejaVu fonts in the page");
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        assertEquals(String.valueOf(cause), PACKAGE + "PdfRenderException", cause.getClass().getName());
        assertEquals("only the exception class is named, never the resource path",
          "PhantomJS render could not complete (IOException)", cause.getMessage());
      }
      List<String> after = loader.requested();
      assertTrue("the page fonts asked for the hidden face after it was hidden: " + after,
        after.subList(before, after.size()).contains("pdf/fonts/DejaVuSans.ttf"));
      assertTrue("no fallback: PhantomJS never ran on the markup without the fonts", fake.started().isEmpty());
    } finally {
      invoke(renderer, "close", new Class<?>[0]);
    }
  }
}
