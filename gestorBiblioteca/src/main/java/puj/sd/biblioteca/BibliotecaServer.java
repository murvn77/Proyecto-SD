package puj.sd.biblioteca;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class BibliotecaServer implements Serializable {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket pubPrestamo = context.createSocket(SocketType.PUB);
            pubPrestamo.bind("tcp://*:5556");

            ZMQ.Socket pubDevolver = context.createSocket(SocketType.PUB);
            pubDevolver.bind("tcp://*:5557");

            ZMQ.Socket pubRenovar = context.createSocket(SocketType.PUB);
            pubRenovar.bind("tcp://*:5558");

            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                socket.send(reply, 0);

                Actividad actividad = (Actividad) deserializeObject(reply);

                if (actividad != null) {
                    if (actividad.getTipoActividad().equals(TipoActividad.PRESTAMO)) {
                        pubPrestamo.send(reply, 0);
                    }

                    if (actividad.getTipoActividad().equals(TipoActividad.DEVOLUCION)) {
                        pubDevolver.send(reply, 0);
                    }

                    if (actividad.getTipoActividad().equals(TipoActividad.RENOVACION)) {
                        pubRenovar.send(reply, 0);
                    }
                }

                Thread.sleep(1000);
            }

            pubPrestamo.close();
            pubDevolver.close();
            pubRenovar.close();
        }
    }

    public static Object deserializeObject(byte[] data) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object object = ois.readObject();
            ois.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}