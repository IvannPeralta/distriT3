package py.una.server.udp;

import java.io.*;
import java.net.*;

class UDPClient {

    private static final int puertoServidor = 9876;
    private static String userName;
    
    public static void main(String a[]) throws Exception {
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();

            System.out.print("Ingrese su nombre de usuario: ");
            userName = inFromUser.readLine();

            InetAddress IPAddress = InetAddress.getByName("localhost");
            System.out.println("Intentando conectar a = " + IPAddress + ":" + puertoServidor +  " via UDP...");

            byte[] sendData;
            byte[] receiveData = new byte[1024];

            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);
                        String mensaje = new String(receivePacket.getData()).trim();
                        System.out.println(mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            while (true) {
                System.out.print("Mensaje: ");
                String mensaje = inFromUser.readLine();
                if (mensaje.equalsIgnoreCase("exit")) {
                    clientSocket.close();
                    break;
                }
                String mensajeConUsuario = userName + ":" + mensaje;
                sendData = mensajeConUsuario.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, puertoServidor);
                clientSocket.send(sendPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

