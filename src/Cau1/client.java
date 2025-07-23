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
public class client {
     public static void main(String[] args) throws IOException {
       // Bước 2
        Socket client = new Socket("localhost", 1001);
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());
        DataInputStream nhan = new DataInputStream(client.getInputStream());
        Scanner nhap = new Scanner(System.in);
       // Bước 3 - 1: Client thực hiện trước
        System.out.println("Nhập a: ");
        int a = Integer.parseInt(nhap.nextLine());
        gui.writeInt(a);
        
        System.out.println("Nhập b: ");
        int b = Integer.parseInt(nhap.nextLine());
        gui.writeInt(b);
        // đến đây gửi dữ liệu đã hoàn tất ở bước 3 -1.
        // nhận kết quả từ server trả về.
        int c = nhan.readInt();
        System.out.println("Tổng của hai số a và b là : " + c);
        //Bước 4
        client.close();
    }
}
