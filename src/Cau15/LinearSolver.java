/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau15;

/**
 *
 * @author PC
 */
public class LinearSolver {
    public static double[] solve(double[][] A, double[] B) throws Exception {
        int n = A.length;

        // Tạo augmented matrix [A|B]
        double[][] mat = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            if (A[i].length != n) throw new Exception("Ma trận A không vuông!");
            for (int j = 0; j < n; j++) {
                mat[i][j] = A[i][j];
            }
            mat[i][n] = B[i];
        }

        // Gauss-Jordan elimination
        for (int i = 0; i < n; i++) {
            // Pivoting
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(mat[k][i]) > Math.abs(mat[maxRow][i])) {
                    maxRow = k;
                }
            }
            double[] temp = mat[i]; mat[i] = mat[maxRow]; mat[maxRow] = temp;

            // Check for singular matrix
            if (Math.abs(mat[i][i]) < 1e-8)
                throw new Exception("Hệ phương trình vô nghiệm hoặc vô số nghiệm (ma trận suy biến)");

            // Chuẩn hóa hàng i
            double div = mat[i][i];
            for (int j = 0; j <= n; j++) {
                mat[i][j] /= div;
            }

            // Khử các hàng khác
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = mat[k][i];
                for (int j = 0; j <= n; j++) {
                    mat[k][j] -= factor * mat[i][j];
                }
            }
        }

        // Lấy kết quả từ cột cuối
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = mat[i][n];
        }
        return x;
    }
}
