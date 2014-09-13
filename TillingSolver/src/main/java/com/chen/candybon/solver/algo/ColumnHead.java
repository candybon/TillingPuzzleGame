/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */

package com.chen.candybon.solver.algo;

/**
  * Represent a special node in a Dancing Link, which indicating the head node of a column link.
  */
public class ColumnHead extends LinkNode {

    //indicating the size of the linknodes under this node.
    int size = 0;
    
    public ColumnHead(int col) {
        super(null, null, null, null, col, -100);    
    }
}
