/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.solver.algo;

/**
  * Represent a node in a Dancing Link.
  */
public class LinkNode {

    //neighbors
    public LinkNode left; 
    public LinkNode right;
    public LinkNode up;
    public LinkNode down;
    
    //the column and row of this node in the dancing link
    public int col = -100;
    public int row = -100;
    
    //store some infomation for result processing, ie Drawing.
    public Integer pieceCellId = null;

    public LinkNode(LinkNode up, LinkNode down, LinkNode left, LinkNode right, int col, int row) {
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
        this.col = col;
        this.row = row;
    }
}
