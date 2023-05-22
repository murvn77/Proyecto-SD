package puj.sd.biblioteca;

import org.zeromq.ZMQ;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

import org.zeromq.ZContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.zeromq.SocketType;

public class SuscriptorDevolucion {
    static MongoClient client = MongoClients.create("mongodb://localhost:27017");
    static MongoDatabase db = client.getDatabase("BibliotecaSD");
    static MongoCollection<Document> collectionActividad = db.getCollection("Actividad");
    MongoCollection<Document> collectionLibro = db.getCollection("Libro");
    static String dataComplete = "";
    
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {

            ZMQ.Socket suscriber = context.createSocket(SocketType.SUB);
            suscriber.connect("tcp://*:5557"); // Conecta el socket al puerto 5555
            suscriber.subscribe("".getBytes());

            while (!Thread.currentThread().isInterrupted()) {
                byte[] mensajeBytes = suscriber.recv(0);
                String mensaje = new String(mensajeBytes);

                System.out.println("Suscriptor recibió el mensaje: " + mensaje);
                Thread.sleep(1000);
            }
        }
    }

    public String devolucion(Actividad newDevolucion) {
        try {
            Bson filtroLibro = Filters.and(Filters.eq("codigo", newDevolucion.getCodigoLibro()));
            Document libro = collectionLibro.find(filtroLibro).first();

            if (libro != null) {
                Bson filtroActividad = Filters.and(Filters.eq("codigoLibro", newDevolucion.getCodigoLibro()),
                        Filters.eq("usuarioCedula", newDevolucion.getUsuarioCedula().toString()),
                        Filters.eq("usuarioNombre", newDevolucion.getUsuarioNombre()),
                        Filters.or(Filters.eq("estado", EstadoPrestamo.PRESTADO.toString()),
                                Filters.eq("estado", EstadoPrestamo.RENOVADO.toString())));
                Document prestamo = collectionActividad.find(filtroActividad).first();

                System.out.println(filtroActividad.toString());

                if (prestamo != null) {
                    System.out.println(prestamo);

                    String fechaFin = prestamo.get("fechaFinPrestamo").toString();
                    LocalDate fechaFinFormat = LocalDate.parse(fechaFin);

                    Long cantidadDias = ChronoUnit.DAYS.between(fechaFinFormat, newDevolucion.getFechaFinPrestamo());

                    if (cantidadDias > 7 && prestamo.get("estado").equals(EstadoPrestamo.PRESTADO.toString())) {
                        newDevolucion.setUsuarioPenalizado(true);
                        dataComplete += "¡El sistema identificó que entregó el libro después del plazo máximo! Actualmente se encuentra penalizado \n";
                    }

                    UpdateOptions opciones = new UpdateOptions().upsert(false);
                    Document cambios = new Document("$set", new Document("estado", EstadoPrestamo.DEVUELTO));

                    collectionActividad.updateOne(filtroActividad, cambios, opciones);

                    Document newDevolucionBD = new Document("tipoActividad",
                            newDevolucion.getTipoActividad().toString())
                            .append("fechaInicioPrestamo", prestamo.get("fechaInicioPrestamo").toString())
                            .append("fechaDevolucion", newDevolucion.getFechaFinPrestamo().toString())
                            .append("usuarioCedula", newDevolucion.getUsuarioCedula().toString())
                            .append("usuarioNombre", newDevolucion.getUsuarioNombre())
                            .append("codigoLibro", newDevolucion.getCodigoLibro())
                            .append("usuarioPenalizado", newDevolucion.getUsuarioPenalizado().toString());

                    collectionActividad.insertOne(newDevolucionBD);
                } else {
                    System.err.println(
                            " System exception en el método devolución: el préstamo de este libro a nombre del cliente no se encuentra en la BD");
                }
            } else {
                System.err.println(
                        " System exception en el método devolución: el libro que intenta devolver no se encuentra en la BD");
            }
        } catch (Exception e) {
            System.err.println(" System exception en el método devolución: " + e);
        }

        MongoCursor<Document> cursor = collectionActividad.find().iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            dataComplete += ((Document) doc).toJson() + "\n";
        }

        return "Ahora los registros de la BD son: \n" + dataComplete;
    }

}