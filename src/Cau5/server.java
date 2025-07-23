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
import java.util.Stack;
/**
 *
 * @author PC
 */
public class server {
     public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(1004);
        System.out.println("Server đã sẵn sàng");

        Socket client = server.accept();
        System.out.println("Client đã kết nối đến server");

        DataInputStream nhan = new DataInputStream(client.getInputStream());
        DataOutputStream gui = new DataOutputStream(client.getOutputStream());

        String expression = nhan.readUTF();
        String result;

        try {
            double value = evaluatePostfix(expression);
            result = "Giá trị biểu thức là: " + value;
        } catch (Exception e) {
            result = "Lỗi khi xử lý biểu thức: " + e.getMessage();
        }

        gui.writeUTF(result);

        server.close();
    }

    // Hàm tính biểu thức hậu tố dùng Stack
    public static double evaluatePostfix(String expr) throws Exception {
        Stack<Double> stack = new Stack<>();
        String[] tokens = expr.trim().split("\\s+");

        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (token.matches("[+\\-*/]")) {
                if (stack.size() < 2) throw new Exception("Không đủ toán hạng");
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new Exception("Chia cho 0");
                        stack.push(a / b); break;
                }
            } else {
                throw new Exception("Ký tự không hợp lệ: " + token);
            }
        }

        if (stack.size() != 1) throw new Exception("Biểu thức không hợp lệ");

        return stack.pop();
    }
}
