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

public class client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9011);
        System.out.println("Kết nối tới server thành công!");

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Nhập chuỗi danh sách số
        System.out.print("Nhập danh sách số thực (dạng 2.5,3.7,5.0): ");
        String list = userInput.readLine();

        // Gửi tới server
        out.println(list);

        // Nhận và in kết quả
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }

        socket.close();
    }
}

