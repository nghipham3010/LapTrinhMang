/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau12;

import java.io.*;
import java.net.*;

/**
 *
 * @author PC
 */
public class client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9014);
        System.out.println("Kết nối tới server");

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Nhập số bí mật x
        System.out.print("Nhập số bí mật x của bạn: ");
        String x = userInput.readLine();
        out.println(x); // gửi x thật

        // Lặp lại cho 2 câu hỏi từ server
        for (int i = 1; i <= 2; i++) {
            String question = in.readLine(); // nhận phép toán
            System.out.println("Câu hỏi " + i + ": " + question);
            System.out.print("Nhập kết quả của x: ");
            String userAnswer = userInput.readLine();
            out.println(userAnswer); // gửi kết quả x mà client tính ra
        }

        // Nhận xác nhận từ server
        String status = in.readLine();
        System.out.println("Server trả lời: " + status);

        // Nếu được phép, nhận bài toán challenge
        if ("Xác thực thành công. Bài toán chính sẽ được gửi...".equals(status)) {
            String challenge = in.readLine();
            System.out.println("Bài toán từ server: " + challenge);
        }

        socket.close();
    }
}
