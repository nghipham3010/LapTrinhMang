/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau10;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9012);
        System.out.println("Server đã sẵn sàng");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client đã kết nối đến server");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Nhận số từ client
        String input = in.readLine();
        int n = Integer.parseInt(input);
        System.out.println("Nhận số: " + n);

        // Kiểm tra Fibonacci
        boolean isFib = isFibonacci(n);
        String result = isFib ? "Thuộc dãy Fibonacci" : "Không thuộc dãy Fibonacci";

        // Gửi kết quả
        out.println(result);

        clientSocket.close();
        serverSocket.close();
    }

    // Hàm kiểm tra số chính phương
    public static boolean isPerfectSquare(int x) {
        int s = (int) Math.sqrt(x);
        return s * s == x;
    }

    // Hàm kiểm tra Fibonacci
    public static boolean isFibonacci(int n) {
        return isPerfectSquare(5 * n * n + 4) || isPerfectSquare(5 * n * n - 4);
    }
}

