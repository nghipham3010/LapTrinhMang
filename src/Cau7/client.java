/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau7;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) throws IOException {
        // Kết nối tới server tại localhost và cổng 9009
        Socket socket = new Socket("localhost", 9009);
        System.out.println("Đã kết nối đến server!");

        // Tạo luồng vào/ra
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Nhập số nguyên dương
        System.out.print("Nhập số nguyên dương: ");
        String number = userInput.readLine();

        // Nhập hệ đếm (2, 8 hoặc 16)
        System.out.print("Nhập hệ đếm muốn chuyển đổi (2, 8, 16): ");
        String base = userInput.readLine();

        // Gửi dữ liệu đến server
        out.println(number);
        out.println(base);

        // Nhận kết quả từ server
        String result = in.readLine();
        System.out.println("Kết quả từ server: " + result);

        // Đóng kết nối
        socket.close();
    }
}

