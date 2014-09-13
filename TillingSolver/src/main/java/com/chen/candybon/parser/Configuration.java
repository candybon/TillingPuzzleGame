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
 * Represent an input Configuration, it preserve all the 
 * methods of a Piece.
 */
public class Configuration extends Piece {

    //Copy constructor
    public Configuration(Piece piece) {
        this.cellList = piece.cellList;
        this.cellMap = piece.cellMap;
        this.width = piece.width;
        this.height = piece.height;
        this.leftMost = piece.leftMost;
        this.rightMost = piece.rightMost;
        this.top = piece.top;
        this.bottom = piece.bottom;
        this.calibrated = piece.calibrated;
        this.matrix = piece.matrix;
    }

    /**
     * Find all the possibilities of putting a piece into this configuration. A
     * possibility will be represented as a map containing the K:cellId of the
     * configuration, V:cellId of the piece.
     *
     * @param basePiece : the piece that is put on the configuration.
     * @param allowRotate
     * @param allowFlip
     * @return the list of all possibilities.
     */
    public List<Map<Integer, Integer>> fit(Piece basePiece, boolean allowRotate, boolean allowFlip) {
        calibrate();
        List<Map<Integer, Integer>> positionMappingList = new ArrayList<Map<Integer, Integer>>();
        //spawn distinct shape of the base piece
        List<Piece> pieceList = basePiece.spawn(allowRotate, allowFlip);

        //for each shape of the piece get all the possible position occupation list.
        for (Piece piece : pieceList) {
            //check with each cell in the configuration
            for (int i = 0; i < cellList.size(); i++) {
                Map<Integer, Integer> positionMapping = probe(this.matrix, cellList.get(i), piece);
                if (!positionMapping.isEmpty()) {
                    positionMappingList.add(positionMapping);
                }
            }
        }
        return positionMappingList;
    }

    /**
     * Helper method used by fit method. Check if possible to put the Piece
     * where the first cell(head) of the Piece should be placed at the specified
     * cell of the configuration. If possible, return all the cell ids that is
     * occupied by this piece, in the format of K:cellId of the configuration.
     * V:cellId of the piece.
     *
     * @param matrix : the matrix representing the configuration
     * @param cell : a cell on the configuration
     * @param piece : the piece to be put on to the configuration
     * @return the mapping of the K:cellId of the configuration. V:cellId of the
     * piece.
     */
    private Map<Integer, Integer> probe(char[][] matrix, Cell cell, Piece piece) {
        calibrate();
        Map<Integer, Integer> positionMap = new Hashtable<Integer, Integer>();

        //try each cell of the Piece
        for (int i = 0; i < piece.size(); i++) {
            //calculate the position where this cell should be on the configuration.
            Cell pieceCell = piece.getCellList().get(i);
            int rowOffset = pieceCell.getRow() - piece.head.getRow();
            int colOffset = pieceCell.getColumn() - piece.head.getColumn();
            int row = cell.getRow() + rowOffset;
            int col = cell.getColumn() + colOffset;

            //check the possibility
            if (row < this.height && row >= 0 && col < this.width && col >= 0 && matrix[row][col] == pieceCell.getColor()) {
                int mapIndex = row * this.width + col;
                Cell confCell = this.cellMap.get(mapIndex);
                positionMap.put(confCell.getIndex(), i);
            } else {
                positionMap.clear();
                return positionMap;
            }
        }
        return positionMap;
    }
    private boolean hReflective = false;
    private boolean hReflectiveChecked = false;
    private boolean vReflective = false;
    private boolean vReflectiveChecked = false;

    //check if the configuration is left-right sysmetry
    public boolean isHReflective() {
        if (hReflectiveChecked) {
            return hReflective;
        }
        hReflectiveChecked = true;
        calibrate();
        for (Cell cell : this.cellList) {
            int reflectiveCol = this.width - cell.getColumn() - 1;
            int reflectiveRow = cell.getRow();
            Cell reflectiveCell = this.cellMap.get(reflectiveRow * width + reflectiveCol);
            if (reflectiveCell == null || reflectiveCell.getColor() != cell.getColor()) {
                hReflective = false;
                return hReflective;
            }
        }
        hReflective = true;
        return hReflective;
    }

    //check if the configuration is up-down sysmetry
    public boolean isVReflective() {
        if (vReflectiveChecked) {
            return vReflective;
        }
        vReflectiveChecked = true;
        calibrate();
        for (Cell cell : this.cellList) {
            int reflectiveCol = cell.getColumn();
            int reflectiveRow = this.height - cell.getRow() - 1;
            Cell reflectiveCell = this.cellMap.get(reflectiveRow * width + reflectiveCol);
            if (reflectiveCell == null || reflectiveCell.getColor() != cell.getColor()) {
                vReflective = false;
                return vReflective;
            }
        }
        vReflective = true;
        return vReflective;
    }
    private boolean rotatable180 = false;
    private boolean rotatable180checked = false;

    public boolean is180Rotatable() {
        if (rotatable180checked) {
            return rotatable180;
        }
        rotatable180checked = true;
        calibrate();
        for (Cell cell : this.cellList) {
            int row = this.height - cell.getRow() - 1;
            int col = this.width - cell.getColumn() - 1;
            Cell rotated = this.cellMap.get(row * width + col);
            if (rotated == null || rotated.getColor() != cell.getColor()) {
                rotatable180 = false;
                return rotatable180;
            }
        }
        rotatable180 = true;
        return rotatable180;
    }
    private boolean rotatable90 = false;
    private boolean rotatable90checked = false;

    public boolean is90Rotatable() {
        if (rotatable90checked) {
            return rotatable90;
        }
        rotatable90checked = true;
        calibrate();
        if (this.width != this.height) {
            rotatable90 = false;
            return rotatable90;
        }

        for (Cell cell : this.cellList) {
            int row = cell.getColumn();
            int col = this.width - cell.getRow() - 1;
            Cell rotated = this.cellMap.get(row * width + col);
            if (rotated == null || rotated.getColor() != cell.getColor()) {
                rotatable90 = false;
                return rotatable90;
            }
        }
        rotatable90 = true;
        return rotatable90;
    }
    private boolean rotatable270 = false;
    private boolean rotatable270checked = false;

    public boolean is270Rotatable() {
        if (rotatable270checked) {
            return rotatable270;
        }
        rotatable270checked = true;
        calibrate();
        if (this.width != this.height) {
            rotatable270 = false;
            return rotatable270;
        }

        for (Cell cell : this.cellList) {
            int row = this.width - cell.getColumn() - 1;
            int col = cell.getRow();
            Cell rotated = this.cellMap.get(row * width + col);
            if (rotated == null || rotated.getColor() != cell.getColor()) {
                rotatable270 = false;
                return rotatable270;
            }
        }
        rotatable270 = true;
        return rotatable270;
    }
    private boolean diagRightUp = false;
    private boolean diagRightUpChecked = false;

    public boolean isDiagonalRightUp() {
        if (diagRightUpChecked) {
            return diagRightUp;
        }
        diagRightUpChecked = true;
        calibrate();
        if (this.width != this.height) {
            diagRightUp = false;
            return diagRightUp;
        }
        for (Cell cell : this.cellList) {
            int row = cell.getColumn();
            int col = cell.getRow();
            Cell rightUp = this.cellMap.get(row * width + col);
            if (rightUp == null || rightUp.getColor() != cell.getColor()) {
                diagRightUp = false;
                return diagRightUp;
            }
        }
        diagRightUp = true;
        return diagRightUp;
    }
    private boolean diagLeftUp = false;
    private boolean diagLeftUpChecked = false;

    public boolean isDiagonalLeftUp() {
        if (diagLeftUpChecked) {
            return diagLeftUp;
        }
        diagLeftUpChecked = true;
        calibrate();
        if (this.width != this.height) {
            diagLeftUp = false;
            return diagLeftUp;
        }
        for (Cell cell : this.cellList) {
            int row = width - cell.getColumn() - 1;
            int col = width - cell.getRow() - 1;
            Cell leftUp = this.cellMap.get(row * width + col);
            if (leftUp == null || leftUp.getColor() != cell.getColor()) {
                diagLeftUp = false;
                return diagLeftUp;
            }
        }
        diagLeftUp = true;
        return diagLeftUp;
    }
}
