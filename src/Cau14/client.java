/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau14;
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
        Socket socket = new Socket("localhost", 9016);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.print("Chọn chế độ (ENCRYPT / DECRYPT): ");
        String mode = sc.nextLine().toUpperCase();
        out.println(mode);

        System.out.print("Nhập văn bản: ");
        String text = sc.nextLine();
        out.println(text);

        System.out.print("Nhập kích thước khóa (2 hoặc 3): ");
        int size = sc.nextInt();
        out.println(size);

        System.out.println("Nhập ma trận khóa (" + size + "x" + size + "):");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                out.print((j == 0 ? "" : ",") + sc.nextInt());
            }
            out.println();
        }

        System.out.println("Kết quả từ server: " + in.readLine());
        socket.close();
    }
}
