/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau11;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author PC
 */
public class DeterminantCalculator {
    private final double[][] matrix;
    private final int size;

    public DeterminantCalculator(double[][] matrix) {
        this.matrix = matrix;
        this.size = matrix.length;
    }
    
    public double calculateDeterminantParallel() {
        if (size == 1) return matrix[0][0];
        if (size == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Double>> futures = new ArrayList<>();

        for (int col = 0; col < size; col++) {
            final int c = col;
            Callable<Double> task = () -> {
                double sign = (c % 2 == 0) ? 1 : -1;
                double[][] minor = getMinor(matrix, 0, c);
                DeterminantCalculator subCalc = new DeterminantCalculator(minor);
                return sign * matrix[0][c] * subCalc.calculateDeterminant();
            };
            futures.add(executor.submit(task));
        }

        double det = 0;
        try {
            for (Future<Double> f : futures) {
                det += f.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return det;
    }

    public double calculateDeterminant() {
        if (size == 1) return matrix[0][0];
        if (size == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        double det = 0;
        for (int col = 0; col < size; col++) {
            double sign = (col % 2 == 0) ? 1 : -1;
            double[][] minor = getMinor(matrix, 0, col);
            det += sign * matrix[0][col] * new DeterminantCalculator(minor).calculateDeterminant();
        }
        return det;
    }

    private double[][] getMinor(double[][] mat, int rowToRemove, int colToRemove) {
        int newSize = mat.length - 1;
        double[][] minor = new double[newSize][newSize];
        int r = 0;

        for (int i = 0; i < mat.length; i++) {
            if (i == rowToRemove) continue;
            int c = 0;
            for (int j = 0; j < mat.length; j++) {
                if (j == colToRemove) continue;
                minor[r][c++] = mat[i][j];
            }
            r++;
        }
        return minor;
    }
}
