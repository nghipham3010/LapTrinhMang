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
public class client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9013);
        System.out.println("Kết nối đến server...");

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        // Nhập kích thước ma trận
        System.out.print("Nhập kích thước ma trận (n >= 2): ");
        int n = Integer.parseInt(userInput.readLine());

        double[][] matrix = new double[n][n];
        System.out.println("Nhập ma trận (các phần tử cách nhau bằng dấu cách):");
        for (int i = 0; i < n; i++) {
            System.out.print("Hàng " + (i + 1) + ": ");
            String[] line = userInput.readLine().trim().split("\\s+");
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Double.parseDouble(line[j]);
            }
        }

        // Chuyển thành JSON đơn giản (gửi từng dòng)
        out.println(n);
        for (int i = 0; i < n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(matrix[i][j]);
                if (j < n - 1) sb.append(",");
            }
            out.println(sb.toString());
        }

        // Nhận kết quả
        String response = in.readLine();
        System.out.println("Định thức ma trận là: " + response);

        socket.close();
    }
}
