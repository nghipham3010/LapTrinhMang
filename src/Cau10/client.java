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

public class client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9012);
        System.out.println("Kết nối tới server thành công!");

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Nhập số từ người dùng
        System.out.print("Nhập số nguyên dương cần kiểm tra Fibonacci: ");
        String n = userInput.readLine();

        // Gửi đến server
        out.println(n);

        // Nhận và in kết quả
        String result = in.readLine();
        System.out.println("Kết quả từ server: " + result);

        socket.close();
    }
}

