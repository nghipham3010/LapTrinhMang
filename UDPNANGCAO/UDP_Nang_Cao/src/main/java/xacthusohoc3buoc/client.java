/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xacthusohoc3buoc;
import java.net.*;
import java.util.Scanner;
public class client {
    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhập số bí mật x: ");
        int x = sc.nextInt();

        // Gửi x đến server
        sendData = String.valueOf(x).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
        clientSocket.send(sendPacket);

        // Nhận phép toán từ server
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String challenge = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

        String[] parts = challenge.split(";");
        String expr = parts[0];
        int result = Integer.parseInt(parts[1]);

        System.out.println("Nhận phép toán: " + expr + " = " + result);
        System.out.print("Nhập x bạn tính được từ kết quả: ");
        int yourX = sc.nextInt();

        // Gửi kết quả đoán x ngược về server
        sendData = String.valueOf(yourX).getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
        clientSocket.send(sendPacket);

        // Nhận kết quả phản hồi
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String serverReply = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
        System.out.println("Server trả lời: " + serverReply);

        // Nhận thử thách cuối cùng (nếu đã xác minh đủ 2 lần)
        if (serverReply.equals("Correct")) {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String finalChallenge = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println(finalChallenge);
        }

        clientSocket.close();
    }
}
