/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau11;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author PC
 */
public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9013);
        System.out.println("Server đã sẵn sàng");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client đã kết nối đến server");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        int n = Integer.parseInt(in.readLine());
        double[][] matrix = new double[n][n];

        // Nhận từng dòng JSON-like
        for (int i = 0; i < n; i++) {
            String[] row = in.readLine().split(",");
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Double.parseDouble(row[j]);
            }
        }

        // Tính định thức song song
        DeterminantCalculator calculator = new DeterminantCalculator(matrix);
        double result = calculator.calculateDeterminantParallel();

        out.println(result);
        clientSocket.close();
        serverSocket.close();
    }
}
