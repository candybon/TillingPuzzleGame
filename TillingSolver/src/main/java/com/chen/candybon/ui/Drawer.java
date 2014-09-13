/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.ui;

public class Drawer {
    //return a html string representing a matrix in the form of html table.
    public String drawPixelMatrix(Pixel[][] matrix, int height, int width) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("<table>");
        for(int i = 0; i < height; i++) {
            resultBuilder.append("<tr>");
            for(int j = 0; j < width; j++) {
                Pixel pixel = matrix[i][j];
                if(pixel != null) {
                    resultBuilder.append("<td align=\"center\" width=\"20px\" bgcolor=\"").append(pixel.color).append("\">").append(pixel.text).append("</td>");
                } else {
                    resultBuilder.append("<td></td>");
                }
            }
            resultBuilder.append("</tr>");
        }
        resultBuilder.append("</table>");
        return resultBuilder.toString();
    }
}
