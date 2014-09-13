/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.solver;

import com.chen.candybon.parser.Cell;
import com.chen.candybon.parser.Configuration;
import com.chen.candybon.parser.Piece;
import com.chen.candybon.solver.algo.LinkNode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//calculate the placement info in a solution.
public class Solution {

    private Configuration configuration = null;
    private List<Piece> pieceList = null;
    private PlacementInfo placementMatrix[][] = null;
    private int size = 0; //number of pieces used.

    public Solution(Configuration configuration, List<Piece> pieceList, List<LinkNode> solution) {
        this.configuration = configuration;
        this.pieceList = pieceList;
        this.placementMatrix = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];

        //place each piece on the board
        for (LinkNode nodeStart : solution) {
            //Find out the pieceId. The largest node.col is the pieceId+conf.size
            int pieceId = nodeStart.col;
            int numOfLinkNode = 1;
            for (LinkNode node = nodeStart.right; node != nodeStart; node = node.right) {
                if (pieceId < node.col) {
                    pieceId = node.col;
                }
                numOfLinkNode++;
            }
            pieceId -= configuration.size();

            //fill the placement matrix with info indicated by each linknode at the same row
            LinkNode node = nodeStart;
            for (int i = 0; i < numOfLinkNode; i++) {
                node = node.right;
                if (node.col >= configuration.size()) {
                    continue; //skip the node that is representing the piece
                }

                Cell pieceCell = pieceList.get(pieceId).getCellList().get(node.pieceCellId);
                PlacementInfo aPlacement = new PlacementInfo(pieceId, pieceCell.getIndex(), pieceCell.getColor());

                Cell confCell = configuration.getCellList().get(node.col);
                placementMatrix[confCell.getRow()][confCell.getColumn()] = aPlacement;
            }
            this.size++;
        }
    }

    public Solution(Configuration configuration, List<Piece> pieceList, PlacementInfo reflective[][], int size) {
        this.configuration = configuration;
        this.pieceList = pieceList;
        this.placementMatrix = reflective;
        this.size = size;
    }

    public Solution getHReflective() {
        PlacementInfo reflective[][] = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo info = placementMatrix[i][configuration.getWidth() - j - 1];
                if (info != null) {
                    reflective[i][j] = new PlacementInfo(info.pieceId, info.pieceCellId, info.pieceCellColor);
                }
            }
        }
        return new Solution(this.configuration, this.pieceList, reflective, this.size);
    }

    public Solution getVReflective() {
        PlacementInfo reflective[][] = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo info = placementMatrix[configuration.getHeight() - i - 1][j];
                if (info != null) {
                    reflective[i][j] = new PlacementInfo(info.pieceId, info.pieceCellId, info.pieceCellColor);
                }
            }
        }
        return new Solution(this.configuration, this.pieceList, reflective, this.size);
    }

    public Solution get180Rotate() {
        PlacementInfo reflective[][] = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo info = placementMatrix[configuration.getHeight() - i - 1][configuration.getWidth() - j - 1];
                if (info != null) {
                    reflective[i][j] = new PlacementInfo(info.pieceId, info.pieceCellId, info.pieceCellColor);
                }
            }
        }
        return new Solution(this.configuration, this.pieceList, reflective, this.size);
    }

    public Solution get90Rotate() {
        PlacementInfo reflective[][] = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo info = placementMatrix[j][configuration.getWidth() - i - 1];
                if (info != null) {
                    reflective[i][j] = new PlacementInfo(info.pieceId, info.pieceCellId, info.pieceCellColor);
                }
            }
        }
        return new Solution(this.configuration, this.pieceList, reflective, this.size);
    }

    public Solution get270Rotate() {
        PlacementInfo reflective[][] = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo info = placementMatrix[configuration.getWidth() - j - 1][i];
                if (info != null) {
                    reflective[i][j] = new PlacementInfo(info.pieceId, info.pieceCellId, info.pieceCellColor);
                }
            }
        }
        return new Solution(this.configuration, this.pieceList, reflective, this.size);
    }

    public Solution getDiagRightUp() {
        PlacementInfo reflective[][] = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo info = placementMatrix[j][i];
                if (info != null) {
                    reflective[i][j] = new PlacementInfo(info.pieceId, info.pieceCellId, info.pieceCellColor);
                }
            }
        }
        return new Solution(this.configuration, this.pieceList, reflective, this.size);
    }

    public Solution getDiagLeftUp() {
        PlacementInfo reflective[][] = new PlacementInfo[configuration.getHeight()][configuration.getWidth()];
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                PlacementInfo info = placementMatrix[configuration.getWidth() - j - 1][configuration.getWidth() - i -1];
                if (info != null) {
                    reflective[i][j] = new PlacementInfo(info.pieceId, info.pieceCellId, info.pieceCellColor);
                }
            }
        }
        return new Solution(this.configuration, this.pieceList, reflective, this.size);
    }

    public PlacementInfo[][] getPlacementMatrix() {
        return placementMatrix;
    }

    public int size() {
        return size;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.configuration);
        hash = 97 * hash + Arrays.deepHashCode(this.placementMatrix);
        hash = 97 * hash + this.size;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Solution other = (Solution) obj;
        if (!Objects.equals(this.configuration, other.configuration)) {
            return false;
        }
        if (!Arrays.deepEquals(this.placementMatrix, other.placementMatrix)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        return true;
    }
}
