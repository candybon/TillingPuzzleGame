/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */
package com.chen.candybon.solver.algo;

import com.chen.candybon.parser.Cell;
import com.chen.candybon.parser.Configuration;
import com.chen.candybon.parser.Piece;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DancingLink {
    
    private Configuration configuration = null;
    private List<Piece> pieceList = null;
    
    //indicating the head and last column's head.
    private ColumnHead head, tail = null;
    
    //for fast fetching of a column link by its col number.
    private List<ColumnHead> columnList = new ArrayList<ColumnHead>();
    
    //the col id before pivot indicates the cell id of the configuration, 
    //those after indicates the in of the pieces.
    private int pivot = 0;
    
    private List<LinkNode> solution = new ArrayList<LinkNode>();
    private List<List<LinkNode>> solutionList = new ArrayList<List<LinkNode>>();

    public DancingLink(Configuration configuration, List<Piece> pieceList, boolean allowRotate, boolean allowFlip) {
        this.configuration = configuration;
        this.pieceList = pieceList;
        pivot = configuration.size();
        init(allowRotate, allowFlip);
    }

    //prepare the the dancing link to initial state.
    private void init(boolean allowRotate, boolean allowFlip) {
        head = new ColumnHead(-100);
        tail = head;
        head.up = head;
        head.down = head;
        head.left = head;
        head.right = head;

        //create column links, the number of column links is configuration cell size + number of the pieces.
        for (int i = 0; i < configuration.size() + pieceList.size(); i++) {
            addColumn(i);
        }

        //create each row links, also put new linknode to the corresponding column link
        int rowCount = 0;
        for (int iPiece = 0; iPiece < pieceList.size(); iPiece++) {
            //get all the possibilities of putting a piece into this configuration.
            List<Map<Integer, Integer>> positionMappingList = configuration.fit(pieceList.get(iPiece), allowRotate, allowFlip);
            
            //for each possibility, create a row link, also
            for (Map<Integer, Integer> positionsMap : positionMappingList) {
                addRow(positionsMap, iPiece, rowCount++);
            }
        }
    }

    private void addColumn(int i) {
        ColumnHead columnNode = new ColumnHead(i);
        columnNode.up = columnNode;
        columnNode.down = columnNode;
        columnNode.left = tail;
        columnNode.right = head;
        tail.right = columnNode;
        head.left = columnNode;
        tail = columnNode;

        columnList.add(columnNode);
    }

    /**
    * Add a row link representing putting a piece on the configuration. At the same time
    * the linknode will be added to the column link.
    * 
    * @param positionsMap : the mapping of the K:cellId of the configuration. V:cellId of the piece.
    * @param pieceId
    * @param rowCount : row number
    */
    private void addRow(Map<Integer, Integer> positionsMap, int pieceId, int rowCount) {
        //get all the configuration cell Ids, and sort them.
        List<Integer> confCellIds = new ArrayList<Integer>();
        confCellIds.addAll(positionsMap.keySet());
        Collections.sort(confCellIds);
        
        //special case, also add the column number indicating the Piece position.
        confCellIds.add(configuration.size() + pieceId);
        
        //create each linknode in the row link, and add the linknode under the column link aswell.
        LinkNode rowHead = null;
        LinkNode prev = rowHead;
        for (Integer index : confCellIds) {
            //handling the column
            ColumnHead columnHead = columnList.get(index);
            LinkNode columnTail = columnHead.up;

            LinkNode node = new LinkNode(columnTail, columnHead, null, null, index, rowCount);
            Integer pieceCellId = positionsMap.get(index);
            if(pieceCellId != null){
                node.pieceCellId = pieceCellId;
            }
            columnTail.down = node;
            columnHead.up = node;
            columnTail = node;
            columnHead.size++;

            //handling the row
            if (prev == null) {
                rowHead = node;
                prev = node;
            }
            prev.right = node;
            node.right = rowHead;
            node.left = prev;
            rowHead.left = node;
            prev = node;
        }
    }
    
    public List<List<LinkNode>> solve() {
        System.out.println("Start searching...");
        find();
        System.out.println("Done.");
        return solutionList;
    }
    
    //recursive and backtrace to find the solutions.
    private void find() {
        ColumnHead leftMost = (ColumnHead) head.right;
        //if all the column links has been removed, then the configuration is fully covered.
        if (leftMost.col >= pivot || leftMost == head) {
            //a solution
            List<LinkNode> solutionCopy = new ArrayList<LinkNode>();
            solutionCopy.addAll(solution);
            solutionList.add(solutionCopy);
            System.out.println("Solution : =================" + solutionList.size());
            renderSolution(configuration, solution);
            return;
        } else {
            //choose a column link to start with. 
//            ColumnHead columnNode = (ColumnHead) head.right;
            ColumnHead columnNode = pickColumn();
            
            //remove all the row links out of consideration.
            cover(columnNode);
            
            //for each row link that is removed.
            for (LinkNode rowNode = columnNode.down; rowNode != columnNode; rowNode = rowNode.down) {
                solution.add(rowNode);
                //also cover the columns taht intersect with the row link.
                for (LinkNode vNode = rowNode.right; vNode != rowNode; vNode = vNode.right) {
                    cover(vNode);
                }
                find();
                
                //undo all teh covers to prepare for the next row link.
                solution.remove(rowNode);
                for (LinkNode vNode = rowNode.left; vNode != rowNode; vNode = vNode.left) {
                    unCover(vNode);
                }
            }
            unCover(columnNode);
        }
    }

    //when a column is added to consideration, its column link should be removed from future
    //consideration. All the row links that intersect with this column link should be selected.
    //for each row link, remove all the linknode out of its column link. So, next time when
    //another column is chosen, the row links selected by this column will not be considered.
    private void cover(LinkNode columnNode) {
        ColumnHead columnHead = columnList.get(columnNode.col);
        columnHead.right.left = columnHead.left;
        columnHead.left.right = columnHead.right;

        //for each row, remove the linknode in it.
        for (LinkNode row = columnHead.down; row != columnHead; row = row.down) {
            for (LinkNode nodeInColumn = row.right; nodeInColumn != row; nodeInColumn = nodeInColumn.right) {
                nodeInColumn.up.down = nodeInColumn.down;
                nodeInColumn.down.up = nodeInColumn.up;
                ColumnHead top = columnList.get(nodeInColumn.col);
                top.size--;
            }
        }
    }
    
    //undo things in the cover method.
    //find all the row links insect with this column. for each row link, add all the linknode back
    //to the column link as they were. all back this column link.
    private void unCover(LinkNode column) {
        ColumnHead columnHead = columnList.get(column.col);

        //for each row, restore the node in each column
        for (LinkNode row = columnHead.up; row != columnHead; row = row.up) {
            for (LinkNode nodeInColumn = row.left; nodeInColumn != row; nodeInColumn = nodeInColumn.left) {
                nodeInColumn.up.down = nodeInColumn;
                nodeInColumn.down.up = nodeInColumn;
                ColumnHead top = columnList.get(nodeInColumn.col);
                top.size++;
            }
        }

        columnHead.right.left = columnHead;
        columnHead.left.right = columnHead;
    }
    
    //a helper method that pick a column link with fewest linknode under it.
    //but the column links indicating the Piece ids should not be considered.
    private ColumnHead pickColumn() {
        ColumnHead selected = head;
        int maxSize = Integer.MAX_VALUE;
        for(LinkNode top = head.right; top != head; top = top.right) {
            if(top.col>=pivot) {
                return selected;
            } else if(((ColumnHead)top).size < maxSize){
                selected = (ColumnHead)top;
            }
        }
        return selected;
    }

    //print a solution
    private void renderSolution(Configuration configuration, List<LinkNode> solution) {
        char matrix[][] = new char[configuration.getHeight()][configuration.getWidth()];
        for (LinkNode lineStart : solution) {
            List<Integer> lineValues = new ArrayList<Integer>();
            lineValues.add(lineStart.col);
            for (LinkNode node = lineStart.right; node != lineStart; node = node.right) {
                lineValues.add(node.col);
            }
            Collections.sort(lineValues);

            //the last one should be the pieceId
            int pieceId = lineValues.get(lineValues.size() - 1) - configuration.size();
            for (int i = 0; i < lineValues.size() - 1; i++) {
                int cellIndex = lineValues.get(i);
                Cell cell = configuration.getCellList().get(cellIndex);
                matrix[cell.getRow()][cell.getColumn()] = (char) ('A' + pieceId);
            }
        }
        for (int i = 0; i < configuration.getHeight(); i++) {
            for (int j = 0; j < configuration.getWidth(); j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
    }
}
