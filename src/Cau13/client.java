/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau13;
import java.io.*;
import java.net.*;
/**
 *
 * @author PC
 */
public class client {
     public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9015);
        System.out.println("Kết nối tới server...");

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Nhập khoảng [a, b]
        System.out.print("Nhập số a: ");
        int a = Integer.parseInt(userInput.readLine());
        System.out.print("Nhập số b: ");
        int b = Integer.parseInt(userInput.readLine());

        // Gửi khoảng tới server
        out.println(a);
        out.println(b);

        // Luồng đọc dữ liệu từ server (streaming)
        Thread readerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Số nguyên tố nhận được: " + line);
                }
            } catch (IOException e) {
                System.out.println("Ngắt kết nối.");
            }
        });

        readerThread.start();

        // Client có thể gửi STOP bất cứ lúc nào
        while (true) {
            String command = userInput.readLine();
            if (command.equalsIgnoreCase("STOP")) {
                out.println("STOP");
                break;
            }
        }

        socket.close();
        System.out.println("Đã dừng nhận số.");
    }
}
