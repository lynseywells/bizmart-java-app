/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.text.DecimalFormat;

/**
 * Money contains static variables for formatting money and calculating taxes.
 */
public abstract class Money {

    /**
     * Formatter for decimals with pattern "###,###,##0.00".
     */
    public static DecimalFormat DF = new DecimalFormat("###,###,##0.00");

    /**
     * Texas sales tax rate of 8.25%.
     */
    public static double TAX_RATE = .0825;

}
