/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package phuongtrinhtuyentinh;
import java.net.*;
import java.util.*;
public class client {
      public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress IP = InetAddress.getByName("localhost");
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhập số ẩn (n): ");
        int n = Integer.parseInt(sc.nextLine());

        StringBuilder sb = new StringBuilder();
        sb.append(n).append("#");

        System.out.println("Nhập ma trận A (" + n + " dòng, mỗi dòng " + n + " số cách nhau dấu phẩy):");
        for (int i = 0; i < n; i++) {
            System.out.print("A[" + i + "]: ");
            String row = sc.nextLine();
            sb.append(row).append("#");
        }

        System.out.print("Nhập vector B (" + n + " số cách nhau dấu phẩy): ");
        String bRow = sc.nextLine();
        sb.append(bRow);

        byte[] sendData = sb.toString().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, 9999);
        socket.send(sendPacket);

        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);

        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Phản hồi từ server: " + response);
    }
}
