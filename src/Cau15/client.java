/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau15;
import java.io.*;
import java.net.*;
import java.util.Scanner;
/**
 *
 * @author PC
 */
public class client {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Socket socket = new Socket("localhost", 9017);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.print("Nhập số ẩn (n): ");
        int n = Integer.parseInt(sc.nextLine());
        out.println(n);

        System.out.println("Nhập ma trận A (" + n + "x" + n + "):");
        for (int i = 0; i < n; i++) {
            System.out.print("A hàng " + (i + 1) + ": ");
            out.println(sc.nextLine()); // chuỗi "1,2,3"
        }

        System.out.print("Nhập vector B (dạng a1,a2,...,an): ");
        out.println(sc.nextLine());

        System.out.println("Kết quả từ server:");
        System.out.println(in.readLine());

        socket.close();
    }
}
