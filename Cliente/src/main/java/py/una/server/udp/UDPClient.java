package py.una.server.udp;

import java.io.*;
import java.net.*;

class UDPClient {

    private static final int puertoServidor = 9876;
    private static String userName;
    private static volatile boolean exit = false;
    
    public static void main(String a[]) throws Exception {
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();

            System.out.print("Ingrese su nombre de usuario: ");
            userName = inFromUser.readLine();

            InetAddress IPAddress = InetAddress.getByName("localhost");
            System.out.println("Intentando conectar a = " + IPAddress + ":" + puertoServidor +  " via UDP...");

            byte[] sendData;
            
            // Enviar el nombre de usuario al servidor
            String nuevoUsuario = "NUEVO_USUARIO:" +userName;
            sendData = nuevoUsuario.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, puertoServidor);
            clientSocket.send(sendPacket);

            Thread receiveThread = new Thread(() -> {
                try {
                    while (!exit) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);

                        // Simula un delay en el procesamiento de los mensajes recibidos
                        Thread.sleep(2000);  // Simula 2 segundos de procesamiento

                        String mensaje = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                        System.out.println(mensaje);
                        receiveData = new byte[1024]; // Limpiar buffer
                    }
                } catch (IOException | InterruptedException e) {
                    if(!exit){
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();

            while (true) {
                System.out.print("Mensaje: ");
                String mensaje = inFromUser.readLine();
                if (mensaje.equalsIgnoreCase("exit")) {   
                    exit = true;
                    receiveThread.interrupt();
                    clientSocket.close();
                    break;
                }
                String mensajeConUsuario = userName + ":" + mensaje;
                sendData = mensajeConUsuario.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, puertoServidor);
                clientSocket.send(sendPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

