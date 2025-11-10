package org.cgiar.ccafs.marlo.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Clone {

  // ============================================================
  // Base project path — adjust this only if MARLO is located
  // somewhere else on your machine.
  // ============================================================
  public static final Path PROJECT_ROOT =
    Paths.get(System.getProperty("user.home"), "Documents", "MARLO-PROJECT", "GitHub");

  // ============================================================
  // Directory structure (OS-independent using Paths/resolve)
  // ============================================================
  public static final Path PATH_DAO = PROJECT_ROOT
    .resolve(Paths.get("MARLO", "marlo-data", "src", "main", "java", "org", "cgiar", "ccafs", "marlo", "data", "dao"));
  public static final Path PATH_MYSQL_DAO = PROJECT_ROOT.resolve(
    Paths.get("MARLO", "marlo-data", "src", "main", "java", "org", "cgiar", "ccafs", "marlo", "data", "dao", "mysql"));
  public static final Path PATH_MANAGER = PROJECT_ROOT.resolve(
    Paths.get("MARLO", "marlo-data", "src", "main", "java", "org", "cgiar", "ccafs", "marlo", "data", "manager"));
  public static final Path PATH_MODEL = PROJECT_ROOT.resolve(
    Paths.get("MARLO", "marlo-data", "src", "main", "java", "org", "cgiar", "ccafs", "marlo", "data", "model"));
  public static final Path PATH_MANAGER_IMPL = PROJECT_ROOT.resolve(Paths.get("MARLO", "marlo-data", "src", "main",
    "java", "org", "cgiar", "ccafs", "marlo", "data", "manager", "impl"));

  // ------------------------------------------------------------
  // Copy utility – creates parent directories if they don’t exist
  // ------------------------------------------------------------
  private static void copy(Path source, Path target) throws IOException {
    Files.createDirectories(target.getParent());
    Files.copy(source, target);
  }

  // ------------------------------------------------------------
  // DAO generation from template
  // ------------------------------------------------------------
  private static void generateDao(String name) {
    Path target = PATH_DAO.resolve(name + "DAO.java");
    Path template = PATH_DAO.resolve("ReportSynthesisMeliaStudyDAO.java");
    generateFromTemplate(template, target, name);
  }

  // ------------------------------------------------------------
  // Replaces template placeholders with the new class name
  // ------------------------------------------------------------
  private static void generateFromTemplate(Path template, Path target, String name) {
    try {
      copy(template, target);

      // Java 8-compatible file read/write
      byte[] bytes = Files.readAllBytes(target);
      String content = new String(bytes, StandardCharsets.UTF_8);

      content =
        content.replace("ReportSynthesisMeliaStudy", name).replace("reportSynthesisMeliaStudy", lowerFirst(name));

      Files.write(target, content.getBytes(StandardCharsets.UTF_8));

      System.out.println("Generated: " + target);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // ------------------------------------------------------------
  // Manager generation from template
  // ------------------------------------------------------------
  private static void generateManager(String name) {
    Path target = PATH_MANAGER.resolve(name + "Manager.java");
    Path template = PATH_MANAGER.resolve("ReportSynthesisMeliaStudyManager.java");
    generateFromTemplate(template, target, name);
  }

  // ------------------------------------------------------------
  // ManagerImpl generation from template
  // ------------------------------------------------------------
  private static void generateManagerImpl(String name) {
    Path target = PATH_MANAGER_IMPL.resolve(name + "ManagerImpl.java");
    Path template = PATH_MANAGER_IMPL.resolve("ReportSynthesisMeliaStudyManagerImpl.java");
    generateFromTemplate(template, target, name);
  }

  // ------------------------------------------------------------
  // MySQL DAO generation from template
  // ------------------------------------------------------------
  private static void generateMysqlDao(String name) {
    Path target = PATH_MYSQL_DAO.resolve(name + "MySQLDAO.java");
    Path template = PATH_MYSQL_DAO.resolve("ReportSynthesisMeliaStudyMySQLDAO.java");
    generateFromTemplate(template, target, name);
  }

  // ------------------------------------------------------------
  // Converts the first character of a string to lowercase
  // ------------------------------------------------------------
  private static String lowerFirst(String s) {
    if (s == null || s.isEmpty()) {
      return s;
    }
    return Character.toLowerCase(s.charAt(0)) + s.substring(1);
  }

  // ------------------------------------------------------------
  // Entry point: specify all model names to generate related files
  // ------------------------------------------------------------
  public static void main(String[] args) {
    String[] model = {"AiReportConfiguration"};

    for (String m : model) {
      generateDao(m);
      generateMysqlDao(m);
      generateManager(m);
      generateManagerImpl(m);
      System.out.println("✅ Generated for " + m);
    }
  }
}
