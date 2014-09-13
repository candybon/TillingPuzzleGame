/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.parser;

/**
 * Represent a tile in a Piece.
 */
public class Cell {

    private int index = -1; //the id of this cell in the Piece
    private int row;        //row number of the cell position in the Piece
    private int column;     //column number 
    private final char color;     //the color text
    private boolean visited = false;   //used in the dfs, indicating if this cell has been travesed.

    public Cell(int x, int y, char color) {
        this.row = x;
        this.column = y;
        this.color = color;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit(boolean visited) {
        this.visited = visited;
    }

    public char getColor() {
        return color;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
