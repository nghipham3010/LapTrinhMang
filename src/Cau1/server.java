/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau1;
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
    public static void main(String[] args) throws IOException {
        //Bước 1:
        ServerSocket server = new ServerSocket(1001);
        System.out.println("Server đã sẵn sàng");
        
        Socket client = server.accept();
        System.out.println("Client đã kết nối đến server");
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());
        DataInputStream nhan = new DataInputStream(client.getInputStream());
        //Bước 3 - 2:
        int a = nhan.readInt();
        int b = nhan.readInt();
        // nhận dữ liệu từ bước 3-1:
        //Xử lý yêu cầu từ client.
        
        server sv = new server();
        int c = a+b;
        //Trả kết quả cho client.
        gui.writeInt(c);
    server.close();
    }
}
