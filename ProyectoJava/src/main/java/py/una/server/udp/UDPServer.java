package py.una.server.udp;

import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class UDPServer {
    
    // Variables
    private static final int puertoServidor = 9876;
    private static ConcurrentHashMap<String, InetSocketAddress> clients = new ConcurrentHashMap<>();

    public static void main(String[] a){
        
        try {
            DatagramSocket serverSocket = new DatagramSocket(puertoServidor);			
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];

			System.out.println("Servidor de Chat UDP en funcionamiento...");
			
            //3) Servidor siempre esperando
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                // Datos recibidos e Identificamos quien nos envio
                String mensajeRecibido = new String(receivePacket.getData()).trim();
                InetAddress clientIPAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("\nMensaje recibido: " + mensajeRecibido);
                String[] mensajePartes = mensajeRecibido.split(":", 2); // Separar usuario y mensaje

                if (mensajePartes.length == 2) {
                    String usuario = mensajePartes[0];
                    String mensaje = mensajePartes[1];

                    System.out.println("De : " + clientIPAddress + ":" + clientPort);

                    // Registrar cliente
                    String clientKey = usuario + "@" + clientIPAddress.getHostAddress() + ":" + clientPort;
                    clients.put(clientKey, new InetSocketAddress(clientIPAddress, clientPort));

                    // Mostrar usuarios registrados
                    System.out.println("Clientes conectados: " + clients.keySet());

                    // Reenviar mensaje a todos los clientes
                    for (InetSocketAddress clientAddress : clients.values()) {
                        if (!clientAddress.equals(new InetSocketAddress(clientIPAddress, clientPort))) { // No enviar a quien envió
                            sendData = (usuario + ": " + mensaje).getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress.getAddress(), clientAddress.getPort());
                            serverSocket.send(sendPacket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}


