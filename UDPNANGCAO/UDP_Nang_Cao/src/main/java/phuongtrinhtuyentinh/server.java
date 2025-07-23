/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package phuongtrinhtuyentinh;
import java.net.*;
import java.util.*;
public class server {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(9999);
        byte[] buffer = new byte[4096];
        System.out.println("Linear Equation Server is running...");

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String json = new String(packet.getData(), 0, packet.getLength());

            String response;
            try {
                String[] parts = json.split("#");
                int n = Integer.parseInt(parts[0]);
                double[][] A = new double[n][n];
                double[] B = new double[n];

                for (int i = 0; i < n; i++) {
                    String[] row = parts[i + 1].split(",");
                    for (int j = 0; j < n; j++) {
                        A[i][j] = Double.parseDouble(row[j]);
                    }
                }

                String[] bRow = parts[n + 1].split(",");
                for (int i = 0; i < n; i++) {
                    B[i] = Double.parseDouble(bRow[i]);
                }

                double[] X = gaussJordan(A, B);
                response = "Nghiệm: " + Arrays.toString(X);
            } catch (Exception e) {
                response = "Lỗi: " + e.getMessage();
            }

            byte[] sendData = response.getBytes();
            DatagramPacket reply = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
            socket.send(reply);
        }
    }

    public static double[] gaussJordan(double[][] A, double[] B) throws Exception {
        int n = A.length;
        double[][] aug = new double[n][n + 1];

        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = B[i];
        }

        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++)
                if (Math.abs(aug[k][i]) > Math.abs(aug[maxRow][i]))
                    maxRow = k;

            double[] temp = aug[i];
            aug[i] = aug[maxRow];
            aug[maxRow] = temp;

            if (Math.abs(aug[i][i]) < 1e-8)
                throw new Exception("Ma trận suy biến hoặc phương trình vô nghiệm!");

            double div = aug[i][i];
            for (int j = 0; j <= n; j++)
                aug[i][j] /= div;

            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = aug[k][i];
                for (int j = 0; j <= n; j++)
                    aug[k][j] -= factor * aug[i][j];
            }
        }

        double[] X = new double[n];
        for (int i = 0; i < n; i++)
            X[i] = aug[i][n];

        return X;
    }
}
