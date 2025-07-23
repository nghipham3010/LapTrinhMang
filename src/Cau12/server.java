/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau12;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author PC
 */
public class server {
     private static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9014);
        System.out.println("Server xác thực đang chờ client...");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client đã kết nối đến server");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Bước 1: Nhận số bí mật x
        int x = Integer.parseInt(in.readLine());
        System.out.println("Nhận x = " + x);

        // Bước 2: Sinh 2 phép toán kiểm tra
        boolean allCorrect = true;

        for (int i = 1; i <= 2; i++) {
            // Random kiểu: "a*x + b" hoặc "a*x^2 + b*x + c"
            int type = rand.nextInt(2); // 0 or 1

            String question;
            int correctAnswer;

            if (type == 0) {
                int a = rand.nextInt(5) + 1;
                int b = rand.nextInt(10);
                correctAnswer = a * x + b;
                question = String.format("Giải phương trình ngược: %dx + %d = ?", a, b);
            } else {
                int a = rand.nextInt(3) + 1;
                int b = rand.nextInt(4);
                int c = rand.nextInt(5);
                correctAnswer = a * x * x + b * x + c;
                question = String.format("Giải phương trình ngược: %dx^2 + %dx + %d = ?", a, b, c);
            }

            // Gửi phép toán
            out.println(question);

            // Nhận lại x mà client đoán
            String clientReply = in.readLine();
            int clientX = Integer.parseInt(clientReply);

            if (clientX != x) {
                allCorrect = false;
                break;
            }
        }

        if (allCorrect) {
            out.println("Xác thực thành công. Bài toán chính sẽ được gửi...");
            out.println("Tính tổng của x^3 + 2x^2 + 7, với x bạn đã dùng.");
        } else {
            out.println("Xác thực thất bại. Đóng kết nối.");
        }

        clientSocket.close();
        serverSocket.close();
    }
}
