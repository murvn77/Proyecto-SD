package puj.sd.biblioteca;

import org.zeromq.ZMQ;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.zeromq.ZContext;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.bson.Document;
import org.zeromq.SocketType;

public class SuscriptorPrestamo {
    static MongoClient client = MongoClients.create("mongodb://localhost:27017");
    static MongoDatabase db = client.getDatabase("BibliotecaSD");
    static MongoCollection<Document> collectionActividad = db.getCollection("Actividad");
    MongoCollection<Document> collectionLibro = db.getCollection("Libro");
    static String dataComplete = "";
    
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {

            ZMQ.Socket suscriber = context.createSocket(SocketType.SUB);
            suscriber.connect("tcp://*:5556"); // Conecta el socket al puerto 5555
            suscriber.subscribe("".getBytes());

            while (!Thread.currentThread().isInterrupted()) {
                byte[] mensajeBytes = suscriber.recv(0);

                Actividad actividad = (Actividad) deserializeObject(mensajeBytes);
                prestamo(actividad);

                System.out.println("Suscriptor recibió el mensaje: " + actividad.toString());
                Thread.sleep(1000);
            }
        }
    }


    public static String prestamo(Actividad newPrestamo) throws Exception {
        Document sampleDoc = new Document("tipoActividad", newPrestamo.getTipoActividad().toString())
                .append("fechaInicioPrestamo", newPrestamo.getFechaInicioPrestamo().toString())
                .append("fechaFinPrestamo", newPrestamo.getFechaFinPrestamo().toString())
                .append("usuarioCedula", newPrestamo.getUsuarioCedula().toString())
                .append("usuarioNombre", newPrestamo.getUsuarioNombre())
                .append("codigoLibro", newPrestamo.getCodigoLibro())
                .append("estado", newPrestamo.getEstado());

        collectionActividad.insertOne(sampleDoc);
        return dataComplete;
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