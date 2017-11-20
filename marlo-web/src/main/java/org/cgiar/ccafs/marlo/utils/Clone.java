package org.cgiar.ccafs.marlo.utils;

/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class Clone {

  public static String pathdao = "D:\\MARLO\\marlo-web\\src\\main\\java\\org\\cgiar\\ccafs\\marlo\\data\\dao";
  public static String pathmysqldao =
    "D:\\MARLO\\marlo-web\\src\\main\\java\\org\\cgiar\\ccafs\\marlo\\data\\dao\\mysql";
  public static String pathmanager = "D:\\MARLO\\marlo-web\\src\\main\\java\\org\\cgiar\\ccafs\\marlo\\data\\manager";
  public static String pathmodel = "D:\\MARLO\\marlo-web\\src\\main\\java\\org\\cgiar\\ccafs\\marlo\\data\\model";
  public static String pathmanagerimpl =
    "D:\\MARLO\\marlo-web\\src\\main\\java\\org\\cgiar\\ccafs\\marlo\\data\\manager\\impl";

  // Copy the source file to target file.
  // In case the dst file does not exist, it is created
  public static void copy(File source, File target) throws IOException {

    final InputStream in = new FileInputStream(source);
    final OutputStream out = new FileOutputStream(target);

    // Copy the bits from instream to outstream
    final byte[] buf = new byte[1024];
    int len;

    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }

    in.close();
    out.close();
  }


  public static void generateDao(String nome) {
    final File target = new File(pathdao + "\\" + nome + "DAO.java");
    try {
      copy(new File(pathdao + "\\CrpAssumptionDAO.java"), target);
      String content = IOUtils.toString(new FileInputStream(target));
      content = content.replaceAll("CrpAssumption", nome);
      content = content.replaceAll("crpAssumption", miniscula(nome));
      IOUtils.write(content, new FileOutputStream(target));
      System.out.println();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void generateManager(String nome) {
    final File target = new File(pathmanager + "\\" + nome + "Manager.java");
    try {
      copy(new File(pathmanager + "\\CrpAssumptionManager.java"), target);
      String content = IOUtils.toString(new FileInputStream(target));
      content = content.replaceAll("CrpAssumption", nome);
      content = content.replaceAll("crpAssumption", miniscula(nome));
      IOUtils.write(content, new FileOutputStream(target));
      System.out.println();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }


  public static void generateManagerImpl(String nome) {
    final File target = new File(pathmanagerimpl + "\\" + nome + "ManagerImpl.java");
    try {
      copy(new File(pathmanagerimpl + "\\CrpAssumptionManagerImpl.java"), target);
      String content = IOUtils.toString(new FileInputStream(target));
      content = content.replaceAll("CrpAssumption", nome);
      content = content.replaceAll("crpAssumption", miniscula(nome));
      IOUtils.write(content, new FileOutputStream(target));
      System.out.println();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void generateMysqlDao(String nome) {
    final File target = new File(pathmysqldao + "\\" + nome + "MySQLDAO.java");
    try {
      copy(new File(pathmysqldao + "\\CrpAssumptionMySQLDAO.java"), target);
      String content = IOUtils.toString(new FileInputStream(target));
      content = content.replaceAll("CrpAssumption", nome);
      content = content.replaceAll("crpAssumption", miniscula(nome));
      IOUtils.write(content, new FileOutputStream(target));
      System.out.println();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    /*
     * File folder = new File(pathmodel);
     * File[] listOfFiles = folder.listFiles();
     * for (int i = 0; i < listOfFiles.length; i++) {
     * if (listOfFiles[i].isFile()) {
     * System.out.print("\"" + listOfFiles[i].getName().replace(".java", "") + "\",");
     * } else if (listOfFiles[i].isDirectory()) {
     * System.out.println("Directory " + listOfFiles[i].getName());
     * }
     * }
     */


    String[] model = {"AdUser"};
    for (String element : model) {
      generateDao(element);
      generateMysqlDao(element);
      generateManager(element);
      generateManagerImpl(element);
      System.out.println("generado para " + element);
    }

  }


  public static String miniscula(String nome) {
    final char c[] = nome.toCharArray();
    c[0] = Character.toLowerCase(c[0]);
    nome = new String(c);
    return nome;
  }

}
