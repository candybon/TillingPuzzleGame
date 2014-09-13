/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
  * Represent an input Piece.
  */
public class Piece {

    protected List<Cell> cellList = new ArrayList<Cell>(); //the list of all the cells in this piece.
    protected Map<Integer, Cell> cellMap = new Hashtable<Integer, Cell>(); //the mapping of sequence id and the cell. used for fast fetching of a cell, base on row, and column number.
    protected int width = 0;
    protected int height = 0;
    protected Cell leftMost = null; //one of the left most cell in this piece
    protected Cell rightMost = null; //one of the right most cell in this piece
    protected Cell top = null; //one of the top cell in this piece.
    protected Cell bottom = null; //one of bottom cell in this piece.
    protected Cell head = null; // the top and the left most point.
    protected boolean calibrated = false; //indicating if this piece has been rearranged to a standard position.
    protected char[][] matrix = null; //char array representation of this piece.

    //add a new cell to the piece
    public void addCell(Cell cell) {
        if (leftMost == null) {
            leftMost = cell;
            rightMost = cell;
            top = cell;
            bottom = cell;
            head = cell;
        } else {
            if (leftMost.getColumn() > cell.getColumn()) {
                leftMost = cell;
            }
            if (rightMost.getColumn() < cell.getColumn()) {
                rightMost = cell;
            }
            if (top.getRow() > cell.getRow()) {
                top = cell;
            }
            if (bottom.getRow() < cell.getRow()) {
                bottom = cell;
            }
            if (head.getRow() > cell.getRow() || (head.getRow() == cell.getRow() && head.getColumn() > cell.getColumn())) {
                head = cell;
            }
        }
        cell.setIndex(cellList.size());
        cellList.add(cell);
        this.width = rightMost.getColumn() - leftMost.getColumn() + 1;
        this.height = bottom.getRow() - top.getRow() + 1;
        this.calibrated = false;
    }

    public int size() {
        return cellList.size();
    }

    public List<Cell> getCellList() {
        return this.cellList;
    }

    //spawn a list of distinct Pieces.
    public List<Piece> spawn(boolean allowRotate, boolean allowFlip) {
        List<Piece> pieceList = new ArrayList<Piece>();
        calibrate();
        Piece originalCopy = new Piece();
        for (int i = 0; i < this.cellList.size(); i++) {
            Cell cell = this.getCellList().get(i);
            Cell newCell = new Cell(cell.getRow(), cell.getColumn(), cell.getColor());
            originalCopy.addCell(newCell);
        }
        originalCopy.calibrate();
//        pieceList.add(originalCopy);

        //if both rotate and flip allowed.
        if (allowRotate && allowFlip) {
            //added the rotated shapes
            Piece rotated = originalCopy;
            for (int i = 0; i < 4; i++) {
                rotated = rotate(rotated);
                //remove symetry pieces.
                if (!pieceList.contains(rotated)) {
                    pieceList.add(rotated);
                    Piece flipped = flip(rotated, true);
                    if (!pieceList.contains(flipped)) {
                        pieceList.add(flipped);
                    }
                }
            }
        } else if (allowRotate) { //only rotate is allowed
            Piece rotated = this;
            for (int i = 0; i < 4; i++) {
                rotated = rotate(rotated);
                if (!pieceList.contains(rotated)) {
                    pieceList.add(rotated);
                }
            }
        } else if (allowFlip) { //only fip is allowed
            Piece flippedRight = flip(this, true);
            Piece flippedDown = flip(this, false);
            Piece flippedDownRight = flip(flippedDown, true);
            if (!pieceList.contains(flippedRight)) {
                pieceList.add(flippedRight);
            }
            if (!pieceList.contains(flippedDown)) {
                pieceList.add(flippedDown);
            }
            if(!pieceList.contains(flippedDownRight)) {
                pieceList.add(flippedDownRight);
            }
        }

        return pieceList;
    }

    //clockwise 90 degree rotate
    //preserve the id of the cell the same as original Piece
    private Piece rotate(Piece basePiece) {
        Piece piece = new Piece();
        //for each cell, rotate clockwise by 90degree, centre point is (0, 0)
        for (int i = 0; i < basePiece.cellList.size(); i++) {
            Cell cell = basePiece.getCellList().get(i);
            int newRow = cell.getColumn();
            int newCol = 0 - cell.getRow();
            Cell newCell = new Cell(newRow, newCol, cell.getColor());
            piece.addCell(newCell);
        }
        
        piece.calibrate();
        return piece;
    }

    //flip by direction, true for flipRight, false for flipDown
    private Piece flip(Piece basePiece, boolean flipRight) {
        Piece piece = new Piece();
        for (int i = 0; i < basePiece.cellList.size(); i++) {
            Cell cell = basePiece.getCellList().get(i);
            Cell newCell = null;
            if (flipRight) {
                newCell = new Cell(cell.getRow(), 0 - cell.getColumn(), cell.getColor());
            } else {
                newCell = new Cell(0 - cell.getRow(), cell.getColumn(), cell.getColor());
            }
            piece.addCell(newCell);
        }
        piece.calibrate();
        return piece;
    }

    //align the piece to a standard coordinate.
    protected void calibrate() {
        if (calibrated) {
            return;
        }
        int topBound = top.getRow();
        int leftBound = leftMost.getColumn();
        for (Cell cell : cellList) {
            cell.setRow(cell.getRow() - topBound);
            cell.setColumn(cell.getColumn() - leftBound);
        }

        //order the cells into a mapping table.
        cellMap.clear();
        for (Cell cell : cellList) {
            cellMap.put(cell.getRow() * width + cell.getColumn(), cell);
        }

        //re-generate the matrix that represent this piece.
        this.matrix = new char[this.height][this.width];
        for (Cell cell : cellList) {
            matrix[cell.getRow()][cell.getColumn()] = cell.getColor();
        }
        this.calibrated = true;
    }

    @Override
    public String toString() {
        this.calibrate();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                builder.append(this.matrix[i][j]);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Piece other = (Piece) obj;
        if (this.cellList.size() != other.cellList.size()) {
            return false;
        }
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        this.calibrate();
        other.calibrate();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (this.matrix[i][j] != other.matrix[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
