/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau6;

/**
 *
 * @author PC
 */
import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) throws IOException {
        // Tạo server socket ở cổng 9008
        ServerSocket serverSocket = new ServerSocket(9008);
        System.out.println("Server đã sẵn sàng");

        // Chấp nhận kết nối từ client
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client đã kết nối đến server");

        // Tạo input/output stream để nhận và gửi dữ liệu
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Nhận số từ client
        String numberStr = in.readLine();
        System.out.println("Nhận số từ client: " + numberStr);

        // Kiểm tra số có đối xứng không
        boolean isPalindrome = isPalindrome(numberStr);
        String result = isPalindrome ? "Là số đối xứng" : "Không phải số đối xứng";

        // Gửi kết quả về client
        out.println(result);

        // Đóng kết nối
        clientSocket.close();
        serverSocket.close();
    }

    // Hàm kiểm tra chuỗi có đối xứng không
    public static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}

