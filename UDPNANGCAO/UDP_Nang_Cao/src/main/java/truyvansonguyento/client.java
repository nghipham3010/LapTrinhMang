/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package truyvansonguyento;
import java.net.*;
import java.util.Scanner;
public class client {
     public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhập khoảng a, b (VD: 10, 50): ");
        String range = sc.nextLine();

        // Gửi yêu cầu khoảng a,b
        byte[] sendData = range.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9876);
        clientSocket.send(sendPacket);

        // Luồng nhận số nguyên tố
        Thread receiver = new Thread(() -> {
            byte[] receiveData = new byte[1024];
            try {
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Số nguyên tố: " + response);
                }
            } catch (Exception e) {
                System.out.println("Luồng nhận dừng.");
            }
        });
        receiver.start();

        // Chờ người dùng nhập "stop" để ngắt
        while (true) {
            String cmd = sc.nextLine();
            if (cmd.equalsIgnoreCase("stop")) {
                byte[] stopMsg = "STOP".getBytes();
                DatagramPacket stopPacket = new DatagramPacket(stopMsg, stopMsg.length, serverAddress, 9876);
                clientSocket.send(stopPacket);
                break;
            }
        }

        clientSocket.close();
    }
}
