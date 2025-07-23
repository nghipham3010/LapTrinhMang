/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau7;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) throws IOException {
        // Tạo socket lắng nghe tại cổng 9009
        ServerSocket serverSocket = new ServerSocket(9009);
        System.out.println("Server đã sẵn sàng");

        // Chấp nhận kết nối từ client
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client đã kết nối đến server");

        // Tạo luồng vào/ra
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Nhận dữ liệu từ client
        String numberStr = in.readLine();
        String baseStr = in.readLine();

        System.out.println("Nhận số: " + numberStr + ", hệ đếm: " + baseStr);

        try {
            int number = Integer.parseInt(numberStr);
            int base = Integer.parseInt(baseStr);

            String converted = "";

            // Chuyển đổi dựa theo hệ đếm
            switch (base) {
                case 2:
                    converted = Integer.toBinaryString(number);
                    break;
                case 8:
                    converted = Integer.toOctalString(number);
                    break;
                case 16:
                    converted = Integer.toHexString(number).toUpperCase();
                    break;
                default:
                    converted = "Hệ đếm không hợp lệ (chỉ chấp nhận 2, 8 hoặc 16)";
            }

            out.println("Biểu diễn trong hệ " + base + ": " + converted);
        } catch (NumberFormatException e) {
            out.println("Dữ liệu nhập vào không hợp lệ!");
        }

        // Đóng kết nối
        clientSocket.close();
        serverSocket.close();
    }
}
