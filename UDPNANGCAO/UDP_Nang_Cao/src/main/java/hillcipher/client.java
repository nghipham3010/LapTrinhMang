/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hillcipher;
import java.net.*;
import java.util.Scanner;
public class client {
     public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress IP = InetAddress.getByName("localhost");
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhập chế độ (ENCODE/DECODE): ");
        String mode = sc.nextLine();

        System.out.print("Nhập ma trận khóa (VD: 3,3;2,5): ");
        String matrix = sc.nextLine();

        System.out.print("Nhập văn bản: ");
        String text = sc.nextLine();

        String message = mode + "#" + matrix + "#" + text;
        byte[] sendData = message.getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, IP, 9999);
        socket.send(packet);

        byte[] receiveData = new byte[4096];
        DatagramPacket response = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(response);

        String result = new String(response.getData(), 0, response.getLength());
        System.out.println("Kết quả: " + result);
    }
}
