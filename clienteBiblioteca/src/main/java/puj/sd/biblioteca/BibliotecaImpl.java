package puj.sd.biblioteca;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class BibliotecaImpl extends UnicastRemoteObject implements Biblioteca {
    String dataComplete = "";
    private ZMQ.Socket pubPrestamo, pubRenovacion, pubDevolucion;

    public BibliotecaImpl(String name) throws RemoteException {
        super();
        try {
            System.out.println("Rebind Object " + name);
            LocateRegistry.createRegistry(1099);
            Naming.rebind(name, this);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String sendMessage(Actividad newActividad) throws InterruptedException {
        // Lógica para procesar y enviar el mensaje
        byte[] data = serializeObject(newActividad);


        try (ZContext context = new ZContext()) {
            pubPrestamo = context.createSocket(SocketType.PUB);
            pubPrestamo.bind("tcp://localhost:5555");

            pubRenovacion = context.createSocket(SocketType.PUB);
            pubRenovacion.bind("tcp://localhost:5556");

            pubDevolucion = context.createSocket(SocketType.PUB);
            pubDevolucion.bind("tcp://localhost:5557");

            while(!Thread.currentThread().isInterrupted()){
                if (newActividad.getTipoActividad().equals(TipoActividad.PRESTAMO)) {
                    pubPrestamo.send(data, 0);
                    System.out.println(data.getClass().getName());
                    dataComplete = "Prestamo enviado";
                }

                if (newActividad.getTipoActividad().equals(TipoActividad.RENOVACION)) {
                    dataComplete = "Renovacion enviado";
                }

                if (newActividad.getTipoActividad().equals(TipoActividad.DEVOLUCION)) {
                    pubDevolucion.send(data, 0);
                    dataComplete = "Devolucion enviado";
                }

                newActividad.setTipoActividad(TipoActividad.NINGUNO);
            }
        }

        return dataComplete;
    }

    private static byte[] serializeObject(Actividad actividad) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(actividad);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
