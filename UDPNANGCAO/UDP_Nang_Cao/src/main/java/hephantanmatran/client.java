/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hephantanmatran;
import java.net.*;
import java.util.Scanner;
public class client {
     public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Nhập kích thước ma trận n (≥2): ");
        int n = Integer.parseInt(scanner.nextLine());

        StringBuilder sb = new StringBuilder();
        sb.append(n).append(";");
        for (int i = 0; i < n; i++) {
            System.out.print("Dòng " + (i + 1) + ": ");
            String row = scanner.nextLine().trim();
            sb.append(row).append(";");
        }

        byte[] data = sb.toString().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, 12345);
        socket.send(sendPacket);

        byte[] buffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivePacket);

        String result = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("📬 Kết quả từ server: " + result);
        socket.close();
    }
}
