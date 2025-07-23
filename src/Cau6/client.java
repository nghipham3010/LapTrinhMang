/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau6;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) throws IOException {
        // Kết nối tới server tại localhost và cổng 9008
        Socket socket = new Socket("localhost", 9008);
        System.out.println("Kết nối tới server thành công!");

        // Tạo input/output stream
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Nhập số từ người dùng
        System.out.print("Nhập số nguyên dương: ");
        String number = userInput.readLine();

        // Gửi số đến server
        out.println(number);

        // Nhận và in kết quả từ server
        String result = in.readLine();
        System.out.println("Kết quả từ server: " + result);

        // Đóng kết nối
        socket.close();
    }
}

