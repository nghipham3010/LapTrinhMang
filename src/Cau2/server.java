/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau2;
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
        ServerSocket server = new ServerSocket(1001);
        System.out.println("Server đã sẵn sàng");

        Socket client = server.accept();
        System.out.println("Client đã kết nối đến server");

        DataInputStream nhan = new DataInputStream(client.getInputStream());
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());

        // Nhận số từ client
        int number = nhan.readInt();

        // Kiểm tra số nguyên tố
        String result = isPrime(number) ? "Prime" : "Not Prime";

        // Gửi kết quả về client
        gui.writeUTF(result);

        server.close();
    }

    // Hàm kiểm tra số nguyên tố
    public static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
