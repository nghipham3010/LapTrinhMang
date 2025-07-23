/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau5;
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
        Socket client = new Socket("localhost", 1004);
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());
        DataInputStream nhan = new DataInputStream(client.getInputStream());

        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập biểu thức hậu tố: ");
        String expression = scanner.nextLine();

        // Gửi biểu thức đến server
        gui.writeUTF(expression);

        // Nhận kết quả từ server
        String result = nhan.readUTF();
        System.out.println("Kết quả từ server: " + result);

        client.close();
    }
}
