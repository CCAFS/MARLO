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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A stand-in for the PhantomJS executable (src/test/resources/pdf/fake-phantomjs.sh) and a reader of what it
 * recorded. The behaviour is selected by the {@code mode} file; see the script for the modes.
 */
final class FakePhantomJs {

  private final Path executable;
  private final Path record;

  /** Installs the script in {@code dir} and sets its first mode. */
  FakePhantomJs(Path dir, String mode) throws IOException {
    this.record = Files.createDirectory(dir.resolve("record"));
    this.executable = dir.resolve("phantomjs");
    String script;
    try (InputStream in = FakePhantomJs.class.getResourceAsStream("/pdf/fake-phantomjs.sh")) {
      script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
    Files.write(executable, script.replace("__REC__", record.toString()).getBytes(StandardCharsets.UTF_8));
    if (!executable.toFile().setExecutable(true)) {
      throw new IOException("cannot make the fake executable");
    }
    mode(mode);
  }

  String path() {
    return executable.toString();
  }

  void mode(String mode) throws IOException {
    Files.write(record.resolve("mode"), mode.getBytes(StandardCharsets.UTF_8));
  }

  /** Process ids of the renders that started, in order (not the --version calls). */
  List<String> started() throws IOException {
    Path file = record.resolve("started");
    if (!Files.exists(file)) {
      return Collections.emptyList();
    }
    List<String> pids = new ArrayList<String>();
    for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
      if (!line.trim().isEmpty()) {
        pids.add(line.trim());
      }
    }
    return pids;
  }

  /** Blocks until {@code count} renders have started, or fails after about ten seconds. */
  void awaitStarted(int count) throws IOException, InterruptedException {
    for (int i = 0; i < 200 && started().size() < count; i++) {
      Thread.sleep(50L);
    }
    if (started().size() < count) {
      throw new IllegalStateException("started " + started().size() + " renders, expected " + count);
    }
  }

  /** The text a render recorded under {@code kind} ("stdin", "env", "args", "home", ...), by pid. */
  String recorded(String kind, String pid) throws IOException {
    return new String(Files.readAllBytes(record.resolve(kind + "." + pid)), StandardCharsets.UTF_8);
  }

  boolean hasRecorded(String kind, String pid) {
    return Files.exists(record.resolve(kind + "." + pid));
  }

  /** The pid of the descendant ("sleep") the hanging modes left running, or null. */
  String childOf(String pid) throws IOException {
    Path file = record.resolve("child." + pid);
    for (int i = 0; i < 100 && !Files.exists(file); i++) {
      try {
        Thread.sleep(20L);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
      }
    }
    return Files.exists(file) ? new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim() : null;
  }

  /** True while the process with this pid is alive. */
  static boolean isAlive(String pid) {
    return ProcessHandle.of(Long.parseLong(pid)).map(ProcessHandle::isAlive).orElse(false);
  }

  /** Waits up to three seconds for the process to be gone. */
  static boolean awaitDead(String pid) throws InterruptedException {
    for (int i = 0; i < 60 && isAlive(pid); i++) {
      Thread.sleep(50L);
    }
    return !isAlive(pid);
  }
}
