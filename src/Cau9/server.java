/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau9;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9011);
        System.out.println("Server đã sẵn sàng");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client đã kết nối đến server");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Nhận chuỗi danh sách số từ client
        String input = in.readLine();
        System.out.println("Dữ liệu nhận: " + input);

        // Phân tích chuỗi thành mảng số thực
        String[] tokens = input.split(",");
        double[] numbers = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            numbers[i] = Double.parseDouble(tokens[i].trim());
        }

        // Tính toán
        double sum = 0, min = numbers[0], max = numbers[0];
        for (double num : numbers) {
            sum += num;
            if (num > max) max = num;
            if (num < min) min = num;
        }
        double average = sum / numbers.length;

        // Gửi kết quả
        out.println("Trung bình: " + average);
        out.println("Số lớn nhất: " + max);
        out.println("Số nhỏ nhất: " + min);

        clientSocket.close();
        serverSocket.close();
    }
}

