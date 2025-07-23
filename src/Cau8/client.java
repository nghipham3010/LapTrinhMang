/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau8;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9010);
        System.out.println("Kết nối tới server thành công!");

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Nhập số n
        System.out.print("Nhập số nguyên n cần tính giai thừa: ");
        String n = userInput.readLine();

        // Gửi tới server
        out.println(n);

        // Nhận kết quả
        String responseLine;
        while ((responseLine = in.readLine()) != null) {
            System.out.println(responseLine);
        }

        socket.close();
    }
}

