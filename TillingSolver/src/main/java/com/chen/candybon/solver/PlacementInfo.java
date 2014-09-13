/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */
package com.chen.candybon.solver;

import java.util.Objects;

public class PlacementInfo {

    public Integer pieceId = null;
    public Integer pieceCellId = null;
    public char pieceCellColor = 0;

    public PlacementInfo(Integer pieceId, Integer pieceCellId, char pieceCellColor) {
        this.pieceCellId = pieceCellId;
        this.pieceId = pieceId;
        this.pieceCellColor = pieceCellColor;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.pieceId);
        hash = 53 * hash + Objects.hashCode(this.pieceCellId);
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
        final PlacementInfo other = (PlacementInfo) obj;
        if (!Objects.equals(this.pieceId, other.pieceId)) {
            return false;
        }
        if (this.pieceCellColor != other.pieceCellColor) {
            return false;
        }
        return true;
    }
    
    
}
