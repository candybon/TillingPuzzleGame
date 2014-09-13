/*
 * Copyright (c) XIAOWEI CHEN, 2013.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * All rights reserved.
 */
package com.chen.candybon.solver;

import com.chen.candybon.solver.algo.LinkNode;
import com.chen.candybon.solver.algo.DancingLink;
import com.chen.candybon.parser.Configuration;
import com.chen.candybon.parser.Piece;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class TilingSolver {

    private Configuration configuration = null;
    private List<Piece> pieceList = null;

    public TilingSolver(Configuration configuration, List<Piece> pieceList) {
        this.configuration = configuration;
        this.pieceList = pieceList;
    }

    public List<Solution> solve(boolean allowRotate, boolean allowFlip) throws IOException {
        if (configuration == null) {
            return null;
        }
        //print the input.
        int pieceCount = 0;
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        writer.write("Pieces:\n");
        for (Piece piece : pieceList) {
            writer.write("========" + (char) ('A' + pieceCount++) + "========\n");
            writer.write(piece.toString() + "\n");
        }
        writer.write("Configuration:\n");
        writer.write(configuration.toString() + "\n");
        writer.flush();

        //solve the problem.
        DancingLink dancingLink = new DancingLink(configuration, pieceList, allowRotate, allowFlip);
        List<List<LinkNode>> solutionsRaw = dancingLink.solve();

        //format the solution, and remove symetry solutions.
        List<Solution> allSolutions = new ArrayList();
        for (List<LinkNode> solutionRaw : solutionsRaw) {
            Solution solution = new Solution(this.configuration, this.pieceList, solutionRaw);
            if (!allSolutions.contains(solution)) {
                allSolutions.add(solution);
            }
        }

        List<Solution> result = allSolutions;

        //remove left-right symetry answers.
        if (configuration.isHReflective()) {
            result = removeSymetry(result, Method.H_Reflective);
        }

        //remove up-down symetry answers.
        if (configuration.isVReflective()) {
            result = removeSymetry(result, Method.V_Reflective);
        }

        //180 rotate symetry
        if (configuration.is180Rotatable()) {
            result = removeSymetry(result, Method.Rotate_180);
        }

        //90 rotate symetry
        if (configuration.is90Rotatable()) {
            result = removeSymetry(result, Method.Rotate_90);
        }

        //270 rotate symetry
        if (configuration.is270Rotatable()) {
            result = removeSymetry(result, Method.Rotate_270);
        }

        //reflect by diagnoal right up line
        if (configuration.isDiagonalRightUp()) {
            result = removeSymetry(result, Method.Diag_RightUp);
        }

        //reflect by diagnoal left up line
        if (configuration.isDiagonalLeftUp()) {
            result = removeSymetry(result, Method.Diag_LeftUp);
        }

        return result;
    }

    private List<Solution> removeSymetry(List<Solution> currentSolutions, Method method) {
        List<Solution> allSolutionsCopy = new ArrayList();
        allSolutionsCopy.addAll(currentSolutions);
        List<Solution> result = new ArrayList();

        for (Solution solution : currentSolutions) {
            if (allSolutionsCopy.contains(solution)) {
                allSolutionsCopy.remove(solution);
                result.add(solution);
                Solution symetry = null;
                switch (method) {
                    case H_Reflective:
                        symetry = solution.getHReflective();
                        break;
                    case V_Reflective:
                        symetry = solution.getVReflective();
                        break;
                    case Rotate_180:
                        symetry = solution.get180Rotate();
                        break;
                    case Rotate_90:
                        symetry = solution.get90Rotate();
                        break;
                    case Rotate_270:
                        symetry = solution.get270Rotate();
                        break;
                    case Diag_RightUp:
                        symetry = solution.getDiagRightUp();
                        break;
                    case Diag_LeftUp:
                        symetry = solution.getDiagLeftUp();
                }

                allSolutionsCopy.remove(symetry);
            }
        }
        return result;
    }

    private enum Method {

        H_Reflective, V_Reflective,
        Rotate_180, Rotate_90, Rotate_270,
        Diag_RightUp, Diag_LeftUp;
    }
}
