/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau4;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.*;
//import java.io.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
/**
 *
 * @author PC
 */
public class server {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(1003);
        System.out.println("Server đã sẵn sàng");

        Socket client = server.accept();
        System.out.println("Client đã kết nối đến server");

        DataInputStream nhan = new DataInputStream(client.getInputStream());
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());

        // Nhận chuỗi dãy số
        String input = nhan.readUTF();
        float sum = calculateSum(input);

        // Gửi tổng về client
        gui.writeFloat(sum);

        server.close();
    }

    // Hàm xử lý tách chuỗi và tính tổng
    public static float calculateSum(String input) {
        String[] parts = input.split(",");
        float sum = 0;
        for (String part : parts) {
            try {
                sum += Float.parseFloat(part.trim());
            } catch (NumberFormatException e) {
                // Bỏ qua phần tử không hợp lệ
                System.out.println("Bỏ qua phần tử không hợp lệ: " + part);
            }
        }
        return sum;
    }
}
