package puj.sd.biblioteca;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.zeromq.SocketType;

public class Suscriptor {
    public static void main(String[] args) throws InterruptedException {
        try (ZContext context = new ZContext()) {
            // Crear el socket SUB
            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect("tcp://localhost:5555"); // Conectar a la dirección y puerto del publicador
            socket.subscribe(new byte[0]); // Suscribirse a todos los mensajes
            System.out.println("llegas?");
            // Recibir objetos Mensaje
            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = socket.recv(0);
                System.out.println("llegas?");

                Actividad actividad = deserializeObject(data); // Deserializar los bytes a un objeto
                System.out.println("Mensaje recibido: " + actividad.getId() + " - " + actividad.getUsuarioCedula());
                Thread.sleep(5000);

            }
        }
    }

    private static Actividad deserializeObject(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object obj = ois.readObject();
            if (obj instanceof Actividad) {
                return (Actividad) obj;
            } else {
                throw new IllegalArgumentException("Los bytes recibidos no se pueden deserializar como un objeto Mensaje.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
