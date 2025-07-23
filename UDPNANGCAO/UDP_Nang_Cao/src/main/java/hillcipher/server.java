/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hillcipher;
import java.net.*;
import java.util.*;
public class server {
     public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(9999);
        byte[] buffer = new byte[4096];
        System.out.println("Hill Cipher UDP Server is running...");

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());

            String[] parts = received.split("#");
            String mode = parts[0];
            String matrixStr = parts[1];
            String text = parts[2];

            int[][] keyMatrix = parseMatrix(matrixStr);
            String response = "";

            if (mode.equals("ENCODE")) {
                if (!isInvertible(keyMatrix)) {
                    response = "Error: Matrix not invertible modulo 26!";
                } else {
                    response = encrypt(text, keyMatrix);
                }
            } else if (mode.equals("DECODE")) {
                int[][] invMatrix = invertMatrix(keyMatrix);
                if (invMatrix == null) {
                    response = "Error: Cannot invert matrix!";
                } else {
                    response = decrypt(text, invMatrix);
                }
            }

            byte[] sendData = response.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, packet.getAddress(), packet.getPort());
            socket.send(sendPacket);
        }
    }

    static int[][] parseMatrix(String str) {
        String[] rows = str.split(";");
        int size = rows.length;
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            String[] nums = rows[i].split(",");
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Integer.parseInt(nums[j]) % 26;
            }
        }
        return matrix;
    }

    static String encrypt(String text, int[][] key) {
        int size = key.length;
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        while (text.length() % size != 0) text += "X";

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += size) {
            int[] vector = new int[size];
            for (int j = 0; j < size; j++) vector[j] = text.charAt(i + j) - 'A';

            int[] res = new int[size];
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) res[r] += key[r][c] * vector[c];
                res[r] %= 26;
                result.append((char) (res[r] + 'A'));
            }
        }
        return result.toString();
    }

    static String decrypt(String cipher, int[][] key) {
        return encrypt(cipher, key); // vì giải mã = mã hóa bằng nghịch đảo
    }

    static boolean isInvertible(int[][] matrix) {
        return gcd(determinant(matrix), 26) == 1;
    }

    static int determinant(int[][] m) {
        if (m.length == 2)
            return (m[0][0]*m[1][1] - m[0][1]*m[1][0]) % 26;
        if (m.length == 3) {
            int a = m[0][0]*(m[1][1]*m[2][2] - m[1][2]*m[2][1]);
            int b = m[0][1]*(m[1][0]*m[2][2] - m[1][2]*m[2][0]);
            int c = m[0][2]*(m[1][0]*m[2][1] - m[1][1]*m[2][0]);
            return (a - b + c) % 26;
        }
        return 0;
    }

    static int[][] invertMatrix(int[][] m) {
        int det = determinant(m);
        det = (det + 26) % 26;
        int detInv = modInverse(det, 26);
        if (detInv == -1) return null;

        int[][] inv = new int[m.length][m.length];

        if (m.length == 2) {
            inv[0][0] = m[1][1];
            inv[0][1] = -m[0][1];
            inv[1][0] = -m[1][0];
            inv[1][1] = m[0][0];
        } else if (m.length == 3) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    inv[j][i] = cofactor(m, i, j); // transpose of cofactor
        }

        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m.length; j++) {
                inv[i][j] = (inv[i][j] * detInv) % 26;
                if (inv[i][j] < 0) inv[i][j] += 26;
            }

        return inv;
    }

    static int cofactor(int[][] m, int row, int col) {
        int[][] minor = new int[2][2];
        int r = 0;
        for (int i = 0; i < 3; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < 3; j++) {
                if (j == col) continue;
                minor[r][c++] = m[i][j];
            }
            r++;
        }
        int cof = minor[0][0]*minor[1][1] - minor[0][1]*minor[1][0];
        if ((row + col) % 2 == 1) cof = -cof;
        return cof;
    }

    static int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    static int modInverse(int a, int m) {
        a = (a % m + m) % m;
        for (int x = 1; x < m; x++)
            if ((a * x) % m == 1) return x;
        return -1;
    }
}
