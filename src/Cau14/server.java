/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau14;
import java.io.*;
import java.net.*;
/**
 *
 * @author PC
 */
public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9016);
        System.out.println("Server sẵn sàng mã hóa Hill Cipher...");

        Socket clientSocket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Nhận chế độ: ENCRYPT hoặc DECRYPT
        String mode = in.readLine();

        // Nhận văn bản và ma trận khóa
        String text = in.readLine();
        int size = Integer.parseInt(in.readLine());
        int[][] key = new int[size][size];
        for (int i = 0; i < size; i++) {
            String[] parts = in.readLine().split(",");
            for (int j = 0; j < size; j++) {
                key[i][j] = Integer.parseInt(parts[j]);
            }
        }

        if (HillCipherUtils.determinant(key) == 0 || HillCipherUtils.modInverse(HillCipherUtils.determinant(key), 26) == -1) {
            out.println("Khóa không khả nghịch mod 26!");
            return;
        }

        int[] inputNums = HillCipherUtils.textToNumbers(text);
        while (inputNums.length % size != 0) {
            inputNums = java.util.Arrays.copyOf(inputNums, inputNums.length + 1); // padding
        }

        int[] resultNums;
        if (mode.equals("ENCRYPT")) {
            resultNums = HillCipherUtils.encrypt(key, inputNums, size);
        } else {
            int[][] inverse = HillCipherUtils.inverseMatrix(key);
            resultNums = HillCipherUtils.encrypt(inverse, inputNums, size); // giải mã
        }

        out.println(HillCipherUtils.numbersToText(resultNums));
        clientSocket.close();
        serverSocket.close();
    }
}
