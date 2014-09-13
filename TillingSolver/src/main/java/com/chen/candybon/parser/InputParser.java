/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
  * Represent an Input analyzer.
  */
public class InputParser {

    private List<Cell> allCellList = new ArrayList<Cell>();  //all the cells in the input file
    private Map<Integer, Cell> allCellMapping = new Hashtable<Integer, Cell>(); //for fast fetching of a cell in the input file.
    private int width = 0;
    private List<Piece> pieceList = new ArrayList<Piece>();
    private Configuration configuration = null;

    //read the input file to construct the list of Pieces, and the Configuraiton
    public void parse(File input) throws FileNotFoundException {
        if (input == null) {
            return;
        }
        //Scan all the cells into a list for later dfs processing.
        Scanner scanner = new Scanner(input);
        int row = 0;
        while (scanner.hasNext()) {
            String line = scanner.nextLine().replaceAll("\\s+$", "");
            if (width < line.length()) {
                width = line.length();
            }
            for (int column = 0; column < line.length(); column++) {
                if (line.charAt(column) != ' ') {
                    Cell cell = new Cell(row, column, line.charAt(column));
                    allCellList.add(cell);
                }
            }
            row++;
        }
        //calculate the cell's absolute index and put into a map for fast fetching a cell, used in dfs to find adjencent cell.
        for (Cell aCell : allCellList) {
            allCellMapping.put(aCell.getRow() * width + aCell.getColumn(), aCell);
        }

        //dfs to put all adjencent cells to construct piece, and find the configuration (the piece with most cells)
        int maxPieceSize = 0;
        Piece largestPiece = null;
        for (Cell bCell : allCellList) {
            if (!bCell.isVisited()) {
                Piece piece = new Piece();
                dfs(bCell, piece); 
                pieceList.add(piece);
                if(piece.size() > maxPieceSize) {
                    maxPieceSize = piece.size();
                    largestPiece = piece;
                }
            }
        }
        if(pieceList.isEmpty()){
            //invalid input
            return;
        }
        pieceList.remove(largestPiece);
        configuration = new Configuration(largestPiece);
    }
    
    //DFS to find all the cell belonging to the same piece, and add them to a piece.
    private void dfs(Cell cell, Piece piece) {
        cell.visit(true);
        piece.addCell(cell);
        List<Cell> adjencyList = getAdjency(cell);
        for (Cell adjCell : adjencyList) {
            if (!adjCell.isVisited()) {
                dfs(adjCell, piece);
            }
        }
    }

    //get a cell's adjancent cells
    private List<Cell> getAdjency(Cell cell) {
        List<Cell> adjList = new ArrayList<Cell>();
        int indexes[] = {
            (cell.getRow() - 1) * width + cell.getColumn(), 
            (cell.getRow() + 1) * width + cell.getColumn(), 
            (cell.getRow() * width) + cell.getColumn() - 1, 
            (cell.getRow() * width) + cell.getColumn() + 1
        };
        for (int i = 0; i < indexes.length; i++) {
            if (allCellMapping.get(indexes[i]) != null) {
                adjList.add(allCellMapping.get(indexes[i]));
            }
        }
        return adjList;
    }
    
    public List<Piece> getPieceList() {
        return this.pieceList;
    }
    public Configuration getConfiguration() {
        return this.configuration;
    }
}
