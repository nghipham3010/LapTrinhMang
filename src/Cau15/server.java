/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau15;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author PC
 */
public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9017);
        System.out.println("Server sẵn sàng giải hệ phương trình tuyến tính...");

        Socket socket = serverSocket.accept();
        System.out.println("Client đã kết nối");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        try {
            int n = Integer.parseInt(in.readLine());

            double[][] A = new double[n][n];
            double[] B = new double[n];

            for (int i = 0; i < n; i++) {
                String[] row = in.readLine().split(",");
                if (row.length != n) throw new Exception("Số phần tử hàng A không đúng!");
                for (int j = 0; j < n; j++) {
                    A[i][j] = Double.parseDouble(row[j]);
                }
            }

            String[] bParts = in.readLine().split(",");
            if (bParts.length != n) throw new Exception("Vector B không đúng kích thước!");
            for (int i = 0; i < n; i++) {
                B[i] = Double.parseDouble(bParts[i]);
            }

            double[] result = LinearSolver.solve(A, B);

            StringBuilder sb = new StringBuilder("Nghiệm X = [ ");
            for (double x : result) {
                sb.append(String.format("%.4f ", x));
            }
            sb.append("]");
            out.println(sb.toString());

        } catch (Exception e) {
            out.println(e.getMessage());
        }

        socket.close();
        serverSocket.close();
    }
}
