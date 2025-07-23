/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package truyvansonguyento;
import java.net.*;
import java.util.*;
public class server {
     public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData;

        System.out.println("UDP Prime Streaming Server đang chạy...");

        while (true) {
            // Nhận khoảng a,b từ client
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String input = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

            if (input.equalsIgnoreCase("STOP")) continue;

            String[] parts = input.split(",");
            int a = Integer.parseInt(parts[0].trim());
            int b = Integer.parseInt(parts[1].trim());

            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            // Gửi từng số nguyên tố từ a đến b, cách nhau 1s
            for (int i = a; i <= b; i++) {
                if (isPrime(i)) {
                    String msg = String.valueOf(i);
                    sendData = msg.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    serverSocket.send(sendPacket);
                    Thread.sleep(1000);  // Chờ 1 giây
                }

                // Kiểm tra tín hiệu dừng (non-blocking check)
                serverSocket.setSoTimeout(10); // ngắn để không chờ lâu
                try {
                    DatagramPacket checkStop = new DatagramPacket(new byte[1024], 1024);
                    serverSocket.receive(checkStop);
                    String checkMsg = new String(checkStop.getData(), 0, checkStop.getLength()).trim();
                    if (checkMsg.equalsIgnoreCase("STOP")) {
                        System.out.println("Nhận yêu cầu dừng luồng.");
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    // không có gói tin nào, tiếp tục
                }
            }
        }
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++)
            if (n % i == 0) return false;
        return true;
    }
}
