/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau3;
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
public class client {
     public static void main(String[] args) throws Exception {
        Socket client = new Socket("localhost", 1002); // Cổng 1002 cho bài 3
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());
        DataInputStream nhan = new DataInputStream(client.getInputStream());

        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập hệ số a: ");
        float a = Float.parseFloat(scanner.nextLine());

        System.out.print("Nhập hệ số b: ");
        float b = Float.parseFloat(scanner.nextLine());

        System.out.print("Nhập hệ số c: ");
        float c = Float.parseFloat(scanner.nextLine());

        // Gửi hệ số
        gui.writeFloat(a);
        gui.writeFloat(b);
        gui.writeFloat(c);

        // Nhận chuỗi kết quả
        String result = nhan.readUTF();
        System.out.println("Kết quả từ server: " + result);

        client.close();
    }
}
