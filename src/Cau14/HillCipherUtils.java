/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau14;

/**
 *
 * @author PC
 */
public class HillCipherUtils {

    // Chuyển ký tự thành số (a=0, b=1, ..., z=25)
    public static int[] textToNumbers(String text) {
        text = text.toLowerCase().replaceAll("[^a-z]", "");
        int[] nums = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            nums[i] = text.charAt(i) - 'a';
        }
        return nums;
    }

    // Chuyển số về chữ
    public static String numbersToText(int[] nums) {
        StringBuilder sb = new StringBuilder();
        for (int n : nums) {
            sb.append((char) ((n % 26 + 26) % 26 + 'a'));
        }
        return sb.toString();
    }

    // Nhân ma trận mod 26
    public static int[] encrypt(int[][] key, int[] plain, int blockSize) {
        int[] cipher = new int[plain.length];
        for (int i = 0; i < plain.length; i += blockSize) {
            for (int row = 0; row < blockSize; row++) {
                int sum = 0;
                for (int col = 0; col < blockSize; col++) {
                    sum += key[row][col] * plain[i + col];
                }
                cipher[i + row] = sum % 26;
            }
        }
        return cipher;
    }

    // Tìm định thức ma trận
    public static int determinant(int[][] m) {
        if (m.length == 2) return m[0][0] * m[1][1] - m[0][1] * m[1][0];
        else if (m.length == 3) {
            return m[0][0]*(m[1][1]*m[2][2] - m[1][2]*m[2][1])
                 - m[0][1]*(m[1][0]*m[2][2] - m[1][2]*m[2][0])
                 + m[0][2]*(m[1][0]*m[2][1] - m[1][1]*m[2][0]);
        }
        return 0;
    }

    // Tìm nghịch đảo mod 26
    public static int modInverse(int a, int mod) {
        a = (a % mod + mod) % mod;
        for (int x = 1; x < mod; x++) {
            if ((a * x) % mod == 1) return x;
        }
        return -1;
    }

    // Tìm ma trận nghịch đảo mod 26
    public static int[][] inverseMatrix(int[][] m) {
        int det = determinant(m);
        int detInv = modInverse(det, 26);
        if (detInv == -1) return null;

        int size = m.length;
        int[][] inv = new int[size][size];

        if (size == 2) {
            inv[0][0] =  m[1][1];
            inv[0][1] = -m[0][1];
            inv[1][0] = -m[1][0];
            inv[1][1] =  m[0][0];
        } else if (size == 3) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int[][] minor = new int[2][2];
                    int mi = 0;
                    for (int r = 0; r < 3; r++) {
                        if (r == i) continue;
                        int mj = 0;
                        for (int c = 0; c < 3; c++) {
                            if (c == j) continue;
                            minor[mi][mj++] = m[r][c];
                        }
                        mi++;
                    }
                    int cofactor = ((i + j) % 2 == 0 ? 1 : -1) * determinant(minor);
                    inv[j][i] = (cofactor * detInv) % 26;
                }
            }
        }

        // Chuẩn hóa mod 26
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                inv[i][j] = (inv[i][j] % 26 + 26) % 26;

        return inv;
    }
}

