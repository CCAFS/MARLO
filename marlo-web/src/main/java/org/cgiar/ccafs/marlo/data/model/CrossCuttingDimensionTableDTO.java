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


package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;


public class CrossCuttingDimensionTableDTO implements Serializable {

  private static final long serialVersionUID = -1392933273584642611L;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  private int genderPrincipal;

  private int genderSignificant;

  private int genderScored;

  private int youthPrincipal;

  private int youthSignificant;

  private int youthScored;

  private int capDevPrincipal;

  private int capDevSignificant;

  private int capDevScored;

  private double percentageGenderSignificant;

  private double percentageGenderPrincipal;

  private double percentageGenderNotScored;

  private double percentageYouthPrincipal;

  private double percentageYouthSignificant;

  private double percentageYouthNotScored;

  private double percentageCapDevSignificant;

  private double percentageCapDevPrincipal;

  private double percentageCapDevNotScored;

  private double total;


  public int getCapDevPrincipal() {
    return capDevPrincipal;
  }


  public int getCapDevScored() {
    return capDevScored;
  }


  public int getCapDevSignificant() {
    return capDevSignificant;
  }


  public int getGenderPrincipal() {
    return genderPrincipal;
  }


  public int getGenderScored() {
    return genderScored;
  }


  public int getGenderSignificant() {
    return genderSignificant;
  }


  public double getPercentageCapDevNotScored() {
    return percentageCapDevNotScored;
  }


  public double getPercentageCapDevPrincipal() {
    return percentageCapDevPrincipal;
  }


  public double getPercentageCapDevSignificant() {
    return percentageCapDevSignificant;
  }


  public double getPercentageGenderNotScored() {
    return percentageGenderNotScored;
  }


  public double getPercentageGenderPrincipal() {
    return percentageGenderPrincipal;
  }


  public double getPercentageGenderSignificant() {
    return percentageGenderSignificant;
  }


  public double getPercentageYouthNotScored() {
    return percentageYouthNotScored;
  }


  public double getPercentageYouthPrincipal() {
    return percentageYouthPrincipal;
  }


  public double getPercentageYouthSignificant() {
    return percentageYouthSignificant;
  }


  public double getTotal() {
    return total;
  }


  public int getYouthPrincipal() {
    return youthPrincipal;
  }


  public int getYouthScored() {
    return youthScored;
  }


  public int getYouthSignificant() {
    return youthSignificant;
  }


  public void setCapDevPrincipal(int capDevPrincipal) {
    this.capDevPrincipal = capDevPrincipal;
  }


  public void setCapDevScored(int capDevScored) {
    this.capDevScored = capDevScored;
  }


  public void setCapDevSignificant(int capDevSignificant) {
    this.capDevSignificant = capDevSignificant;
  }


  public void setGenderPrincipal(int genderPrincipal) {
    this.genderPrincipal = genderPrincipal;
  }


  public void setGenderScored(int genderScored) {
    this.genderScored = genderScored;
  }


  public void setGenderSignificant(int genderSignificant) {
    this.genderSignificant = genderSignificant;
  }


  public void setPercentageCapDevNotScored(double percentageCapDevNotScored) {
    this.percentageCapDevNotScored = percentageCapDevNotScored;
  }


  public void setPercentageCapDevPrincipal(double percentageCapDevPrincipal) {
    this.percentageCapDevPrincipal = percentageCapDevPrincipal;
  }


  public void setPercentageCapDevSignificant(double percentageCapDevSignificant) {
    this.percentageCapDevSignificant = percentageCapDevSignificant;
  }


  public void setPercentageGenderNotScored(double percentageGenderNotScored) {
    this.percentageGenderNotScored = percentageGenderNotScored;
  }


  public void setPercentageGenderPrincipal(double percentageGenderPrincipal) {
    this.percentageGenderPrincipal = percentageGenderPrincipal;
  }


  public void setPercentageGenderSignificant(double percentageGenderSignificant) {
    this.percentageGenderSignificant = percentageGenderSignificant;
  }


  public void setPercentageYouthNotScored(double percentageYouthNotScored) {
    this.percentageYouthNotScored = percentageYouthNotScored;
  }


  public void setPercentageYouthPrincipal(double percentageYouthPrincipal) {
    this.percentageYouthPrincipal = percentageYouthPrincipal;
  }


  public void setPercentageYouthSignificant(double percentageYouthSignificant) {
    this.percentageYouthSignificant = percentageYouthSignificant;
  }


  public void setTotal(double total) {
    this.total = total;
  }


  public void setYouthPrincipal(int youthPrincipal) {
    this.youthPrincipal = youthPrincipal;
  }


  public void setYouthScored(int youthScored) {
    this.youthScored = youthScored;
  }


  public void setYouthSignificant(int youthSignificant) {
    this.youthSignificant = youthSignificant;
  }


}
