/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xacthusohoc3buoc;
import java.net.*;
import java.util.*;
public class server {
     public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9999);
        byte[] receiveData = new byte[1024];
        byte[] sendData;

        System.out.println("Server xác thực đang chạy...");

        int successCount = 0;

        while (successCount < 2) {
            // Nhận số x bí mật từ client
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String xStr = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            int x = Integer.parseInt(xStr);

            // Tạo ngẫu nhiên phép toán
            Random rand = new Random();
            int type = rand.nextInt(2); // 0: 2x + 3, 1: x^2 - 4x + 1
            String question = "";
            int result = 0;

            if (type == 0) {
                result = 2 * x + 3;
                question = "2x + 3";
            } else {
                result = x * x - 4 * x + 1;
                question = "x^2 - 4x + 1";
            }

            // Gửi phép toán và kết quả về client
            String challenge = question + ";" + result;
            sendData = challenge.getBytes();
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            serverSocket.send(sendPacket);
            System.out.println("Gửi thử thách: " + challenge);

            // Nhận phản hồi từ client
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String clientReply = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            int clientGuess = Integer.parseInt(clientReply);

            if (clientGuess == x) {
                successCount++;
                sendData = "Correct".getBytes();
            } else {
                successCount = 0; // Reset nếu sai
                sendData = "Wrong".getBytes();
            }

            sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            serverSocket.send(sendPacket);
        }

        // Gửi bài toán thực sự (sau xác minh thành công)
        String challenge = "Challenge Accepted. Welcome!";
        sendData = challenge.getBytes();
        InetAddress clientAddress = InetAddress.getLocalHost(); // Hoặc lấy lại từ trước
        DatagramPacket finalPacket = new DatagramPacket(sendData, sendData.length, clientAddress, 9998);
        serverSocket.send(finalPacket);

        serverSocket.close();
    }
}
