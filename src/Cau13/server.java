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
public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9015);
        System.out.println("Server sẵn sàng nhận truy vấn...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Kết nối từ client");

            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Nhận khoảng [a, b]
            int a = Integer.parseInt(in.readLine());
            int b = Integer.parseInt(in.readLine());

            System.out.println("Yêu cầu từ client: từ " + a + " đến " + b);

            // Gửi số nguyên tố liên tục theo TCP streaming
            for (int i = a; i <= b; i++) {
                // Kiểm tra nếu client gửi STOP
                if (in.ready()) {
                    String stopCmd = in.readLine();
                    if (stopCmd.equalsIgnoreCase("STOP")) {
                        System.out.println("Client yêu cầu dừng.");
                        break;
                    }
                }

                if (isPrime(i)) {
                    out.println(i); // gửi dòng
                    Thread.sleep(1000); // chờ 1 giây
                }
            }

            socket.close();
            System.out.println("Đã đóng phiên làm việc với client.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i*i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
