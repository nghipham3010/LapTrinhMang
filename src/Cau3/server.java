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
public class server {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(1002);
        System.out.println("Server sẵn sàng");

        Socket client = server.accept();
        System.out.println("Client đã kết nối đến server");

        DataInputStream nhan = new DataInputStream(client.getInputStream());
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());

        // Nhận a, b, c
        float a = nhan.readFloat();
        float b = nhan.readFloat();
        float c = nhan.readFloat();

        // Giải phương trình
        String result = solveQuadratic(a, b, c);

        // Gửi kết quả
        gui.writeUTF(result);

        server.close();
    }

    public static String solveQuadratic(float a, float b, float c) {
        if (a == 0) {
            return b == 0 ? (c == 0 ? "Phương trình vô số nghiệm" : "Phương trình vô nghiệm") :
                   "Phương trình bậc nhất: x = " + (-c / b);
        }

        float delta = b * b - 4 * a * c;
        if (delta > 0) {
            double x1 = (-b + Math.sqrt(delta)) / (2 * a);
            double x2 = (-b - Math.sqrt(delta)) / (2 * a);
            return "Có 2 nghiệm: x1 = " + x1 + ", x2 = " + x2;
        } else if (delta == 0) {
            double x = -b / (2 * a);
            return "Nghiệm kép: x1 = x2 = " + x;
        } else {
            return "Vô nghiệm";
        }
    }
}
