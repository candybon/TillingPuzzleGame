/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.ui;

import com.chen.candybon.parser.Cell;
import com.chen.candybon.parser.Configuration;
import com.chen.candybon.parser.Piece;
import com.chen.candybon.solver.PlacementInfo;
import com.chen.candybon.solver.Solution;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Displayer {

    //all available colors for the cell color
    private String preferedColors[] = {
        "#FF0000", "#00FF00", "#0000FF", "#FFFF00",
        "#00FFFF", "#FF00FF", "#C0C0C0", "#580000",
        "#FF7F50", "#B8860B", "#006400", "#D2691E",
        "#6495ED", "#FFF8DC", "#008B8B", "#006400",
        "#000000"};
    private Drawer drawer = new Drawer();
    private String filePath = null;
    private URI resultUri = null;
    private Configuration configuration = null;
    private List<Piece> pieceList = null;
    //the color mapping for the tile.
    private Map<Integer, String> cellColorMap = new HashMap<Integer, String>();
    //the color mapping for the piece
    private Map<Integer, String> pieceColorMap = new HashMap<Integer, String>();
    private int numColorUsed = 0;

    public Displayer(String dirPath, Configuration configuration, List<Piece> pieceList) {
        filePath = dirPath + "//result.html";
        resultUri = new File(filePath).toURI();
        this.configuration = configuration;
        this.pieceList = pieceList;

        //generate random color to represent each piece.
        for (int i = 0; i < pieceList.size(); i++) {
            pieceColorMap.put(i, getRandomColor());
        }
    }

    private String getRandomColor() {
        //generate random color to represent each piece.
        Random rand = new Random();
        return String.format("#%06X", (0xFFFFFF & new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)).getRGB()));
    }

    public void display(List<Solution> allSolutions, long timeTaken)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter resultWriter = new PrintWriter(filePath, "UTF-8");
        if (configuration == null) {
            resultWriter.write("Invalid Input!");
            resultWriter.close();
            callBrowser();
            return;
        }
        //List the Inputs
        resultWriter.write("<h1>Input:</h1>Target Configuration:<br>");
        resultWriter.write(renderPiece(configuration));
        
        resultWriter.write("<br>Input Pieces: <br>");
        char pieceNameStarter = 'A';
        resultWriter.write("<table border=\"1\"><tr>");
        for (Piece piece : pieceList) {
            resultWriter.write("<td valign=\"top\">");
            resultWriter.write((char) pieceNameStarter++);
            resultWriter.write(renderPiece(piece));
            resultWriter.write("</td>");
        }
        resultWriter.write("</tr></table><br>");
        
        
        resultWriter.write("Time Taken to read Input: "+timeTaken+" seconds<br>");
        resultWriter.write("<hr> <h1>Solutions:</h1>");
        
        resultWriter.write("<b>For simplicy, each piece with cell id can be represented as:</b><br><br>");

        pieceNameStarter = 'A';
        resultWriter.write("<table border=\"1\"><tr>");
        for (int i = 0; i < pieceList.size(); i++) {
            Piece piece = pieceList.get(i);
            String color = pieceColorMap.get(i);
            resultWriter.write("<td valign=\"top\">");
            resultWriter.write((char) pieceNameStarter++);
            resultWriter.write(paintPiece(piece, color));
            resultWriter.write("</td>");
        }
        resultWriter.write("</tr></table>");

        //List all the solutions
        int solutionCount = 1;
        for (Solution solution : allSolutions) {
            resultWriter.write("<h2>Solution" + solutionCount++ );
            String solutionString = renderSolution(solution);
            resultWriter.write(solutionString);
        }
        if (allSolutions.isEmpty()) {
            resultWriter.write("No Solution");
        }
        resultWriter.close();
        callBrowser();
    }

    //draw the piece in the original color.
    private String renderPiece(Piece piece) {
        Pixel[][] matrix = new Pixel[piece.getHeight()][piece.getWidth()];
        for (Cell cell : piece.getCellList()) {
            int intColor = cell.getColor();
            String htmlColor = cellColorMap.get(intColor);
            if (htmlColor == null) {
                if (numColorUsed >= preferedColors.length) {
                    htmlColor = getRandomColor();
                } else {
                    htmlColor = preferedColors[numColorUsed++];
                }
                cellColorMap.put(intColor, htmlColor);
            }
            Pixel pixel = new Pixel(cell.getColor() + "", htmlColor);
            matrix[cell.getRow()][cell.getColumn()] = pixel;
        }
        return drawer.drawPixelMatrix(matrix, piece.getHeight(), piece.getWidth());
    }

    //draw the piece with the color specified, also print the cell id in each cell.
    private String paintPiece(Piece piece, String color) {
        Pixel[][] matrix = new Pixel[piece.getHeight()][piece.getWidth()];
        for (Cell cell : piece.getCellList()) {
            Pixel pixel = new Pixel(cell.getIndex() + "", color);
            matrix[cell.getRow()][cell.getColumn()] = pixel;
        }
        return drawer.drawPixelMatrix(matrix, piece.getHeight(), piece.getWidth());
    }

    //draw a solution.
    private String renderSolution(Solution solution) {

        PlacementInfo placementMatrix[][] = solution.getPlacementMatrix();

        Pixel piecePlacementMatrix[][] = new Pixel[configuration.getHeight()][configuration.getWidth()];
        Pixel cellPlacementMatrix[][] = new Pixel[configuration.getHeight()][configuration.getWidth()];
        Pixel resultMatrix[][] = new Pixel[configuration.getHeight()][configuration.getWidth()];

        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo placementInfo = placementMatrix[i][j];
                if (placementInfo == null) {
                    continue;
                }
                Integer pieceId = placementInfo.pieceId;

                String pieceName = (char) (pieceId + 'A') + "";
                String pieceColor = pieceColorMap.get(pieceId);
                piecePlacementMatrix[i][j] = new Pixel(pieceName, pieceColor);

                cellPlacementMatrix[i][j] = new Pixel(placementInfo.pieceCellId.toString(), pieceColor);

                String pieceCellColorName = Character.toString(placementInfo.pieceCellColor);
                String pieceCellColor = cellColorMap.get(placementInfo.pieceCellColor + 0);
                resultMatrix[i][j] = new Pixel(pieceCellColorName, pieceCellColor);
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("<table border = \"1\"><tr><th>Actual Result</th><th>Placement of Pieces</th><th>Placement with Piece Cell Id</th></tr><tr>");
        builder.append("<td>").append(drawer.drawPixelMatrix(resultMatrix, configuration.getHeight(), configuration.getWidth())).append("</td>");
        builder.append("<td>").append(drawer.drawPixelMatrix(piecePlacementMatrix, configuration.getHeight(), configuration.getWidth())).append("</td>");
        builder.append("<td>").append(drawer.drawPixelMatrix(cellPlacementMatrix, configuration.getHeight(), configuration.getWidth())).append("</td>");
        builder.append("</tr></table>");

        if (solution.size() < pieceList.size()) {
            builder.append("Not all Pieces are used.");
        }
        return builder.toString();
    }

    private void callBrowser() {
        try {
            Desktop.getDesktop().browse(resultUri);
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }
    }
}
