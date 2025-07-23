/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cau8;

/**
 *
 * @author PC
 */
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9010);
        System.out.println("Server đã sẵn sàng");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client đã kết nối đến server");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Nhận số n từ client
        int n = Integer.parseInt(in.readLine());
        System.out.println("Tính giai thừa cho: " + n);

        // Tính giai thừa song song
        BigInteger result = parallelFactorial(n);
        out.println("Giai thừa của " + n + " là:\n" + result.toString());

        clientSocket.close();
        serverSocket.close();
    }

    // Class tính đoạn giai thừa
    static class FactorialThread extends Thread {
        private final int start;
        private final int end;
        private BigInteger result = BigInteger.ONE;

        public FactorialThread(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void run() {
            for (int i = start; i <= end; i++) {
                result = result.multiply(BigInteger.valueOf(i));
            }
        }

        public BigInteger getResult() {
            return result;
        }
    }

    // Hàm chia nhỏ và chạy song song
    public static BigInteger parallelFactorial(int n) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        List<FactorialThread> threads = new ArrayList<>();

        int chunkSize = n / numThreads;
        int remainder = n % numThreads;
        int start = 1;

        for (int i = 0; i < numThreads; i++) {
            int end = start + chunkSize - 1;
            if (i < remainder) end++; // chia đều phần dư

            FactorialThread thread = new FactorialThread(start, end);
            threads.add(thread);
            thread.start();

            start = end + 1;
        }

        // Chờ các thread hoàn thành
        BigInteger result = BigInteger.ONE;
        for (FactorialThread t : threads) {
            try {
                t.join();
                result = result.multiply(t.getResult());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}

