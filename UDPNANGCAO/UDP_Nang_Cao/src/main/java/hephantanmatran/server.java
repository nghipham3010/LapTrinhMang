/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hephantanmatran;

import java.net.*;
import java.util.concurrent.*;

public class server {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(12345);
        byte[] buffer = new byte[8192];

        System.out.println("🟢 Server đang chờ dữ liệu...");

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String data = new String(packet.getData(), 0, packet.getLength());

        double[][] matrix = parseMatrix(data);
        double det = computeDeterminantParallel(matrix);

        String result = "Determinant = " + det;
        byte[] response = result.getBytes();

        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();
        DatagramPacket responsePacket = new DatagramPacket(response, response.length, clientAddress, clientPort);
        socket.send(responsePacket);

        System.out.println("✅ Đã gửi kết quả về client: " + result);
        socket.close();
    }

    public static double[][] parseMatrix(String data) {
        String[] lines = data.split(";");
        int n = Integer.parseInt(lines[0]);
        double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            String[] nums = lines[i + 1].trim().split(" ");
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Double.parseDouble(nums[j]);
            }
        }
        return matrix;
    }

    public static double computeDeterminantParallel(double[][] matrix) throws Exception {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2)
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Future<Double>[] futures = new Future[n];

        for (int col = 0; col < n; col++) {
            final int c = col;
            futures[c] = pool.submit(() -> {
                double[][] minor = getMinor(matrix, 0, c);
                double sign = (c % 2 == 0) ? 1 : -1;
                return sign * matrix[0][c] * computeDeterminant(minor);
            });
        }

        double result = 0;
        for (int i = 0; i < n; i++) {
            result += futures[i].get();
        }

        pool.shutdown();
        return result;
    }

    public static double computeDeterminant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2)
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        double result = 0;
        for (int col = 0; col < n; col++) {
            double[][] minor = getMinor(matrix, 0, col);
            double sign = (col % 2 == 0) ? 1 : -1;
            result += sign * matrix[0][col] * computeDeterminant(minor);
        }
        return result;
    }

    public static double[][] getMinor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] minor = new double[n - 1][n - 1];
        for (int i = 0, mi = 0; i < n; i++) {
            if (i == row) continue;
            for (int j = 0, mj = 0; j < n; j++) {
                if (j == col) continue;
                minor[mi][mj++] = matrix[i][j];
            }
            mi++;
        }
        return minor;
    }
}
