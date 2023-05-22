// package puj.sd.biblioteca;

// import java.rmi.*;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.server.UnicastRemoteObject;
// import java.time.LocalDate;
// import java.time.temporal.ChronoUnit;

// import org.bson.Document;
// import org.bson.conversions.Bson;

// import com.mongodb.client.MongoClient;
// import com.mongodb.client.MongoClients;
// import com.mongodb.client.MongoCollection;
// import com.mongodb.client.MongoCursor;
// import com.mongodb.client.MongoDatabase;
// import com.mongodb.client.model.Filters;
// import com.mongodb.client.model.UpdateOptions;

// public class BibliotecaImpl extends UnicastRemoteObject implements Biblioteca {
//     MongoClient client = MongoClients.create("mongodb://localhost:27017");
//     MongoDatabase db = client.getDatabase("BibliotecaSD");
//     MongoCollection<Document> collectionActividad = db.getCollection("Actividad");
//     MongoCollection<Document> collectionLibro = db.getCollection("Libro");
//     String dataComplete = "";

//     public BibliotecaImpl(String name) throws RemoteException {
//         super();
//         try {
//             System.out.println("Rebind Object " + name);
//             LocateRegistry.createRegistry(1099);
//             Naming.rebind(name, this);
//         } catch (Exception e) {
//             System.out.println("Exception: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     public String prestamo(Actividad newPrestamo) throws RemoteException {
//         Document sampleDoc = new Document("tipoActividad", newPrestamo.getTipoActividad().toString())
//                 .append("fechaInicioPrestamo", newPrestamo.getFechaInicioPrestamo().toString())
//                 .append("fechaFinPrestamo", newPrestamo.getFechaFinPrestamo().toString())
//                 .append("usuarioCedula", newPrestamo.getUsuarioCedula().toString())
//                 .append("usuarioNombre", newPrestamo.getUsuarioNombre().toString())
//                 .append("codigoLibro", newPrestamo.getCodigoLibro().toString())
//                 .append("estado", newPrestamo.getEstado());

//         collectionActividad.insertOne(sampleDoc);

//         /*
//          * MongoCursor<Document> cursor = collectionActividad.find().iterator();
//          * while (cursor.hasNext()) {
//          * Document doc = cursor.next();
//          * dataComplete = ((Document) doc).toJson() + "\n";
//          * }
//          */
//         return dataComplete;
//     }

//     public String devolucion(Actividad newDevolucion) throws RemoteException {
//         try {
//             Bson filtroLibro = Filters.and(Filters.eq("codigo", newDevolucion.getCodigoLibro()));
//             Document libro = collectionLibro.find(filtroLibro).first();

//             if (libro != null) {
//                 Bson filtroActividad = Filters.and(Filters.eq("codigoLibro", newDevolucion.getCodigoLibro().toString()),
//                         Filters.eq("usuarioCedula", newDevolucion.getUsuarioCedula().toString()),
//                         Filters.eq("usuarioNombre", newDevolucion.getUsuarioNombre().toString()),
//                         Filters.or(Filters.eq("estado", EstadoPrestamo.PRESTADO.toString()),
//                                 Filters.eq("estado", EstadoPrestamo.RENOVADO.toString())));
//                 Document prestamo = collectionActividad.find(filtroActividad).first();

//                 System.out.println(filtroActividad.toString());

//                 if (prestamo != null) {
//                     System.out.println(prestamo);

//                     String fechaFin = prestamo.get("fechaFinPrestamo").toString();
//                     LocalDate fechaFinFormat = LocalDate.parse(fechaFin);

//                     Long cantidadDias = ChronoUnit.DAYS.between(fechaFinFormat, newDevolucion.getFechaFinPrestamo());

//                     if (cantidadDias > 7 && prestamo.get("estado").equals(EstadoPrestamo.PRESTADO.toString())) {
//                         newDevolucion.setUsuarioPenalizado(true);
//                         dataComplete += "¡El sistema identificó que entregó el libro después del plazo máximo! Actualmente se encuentra penalizado \n";
//                     }

//                     UpdateOptions opciones = new UpdateOptions().upsert(false);
//                     Document cambios = new Document("$set", new Document("estado", EstadoPrestamo.DEVUELTO));

//                     collectionActividad.updateOne(filtroActividad, cambios, opciones);

//                     Document newDevolucionBD = new Document("tipoActividad",
//                             newDevolucion.getTipoActividad().toString())
//                             .append("fechaInicioPrestamo", prestamo.get("fechaInicioPrestamo").toString())
//                             .append("fechaDevolucion", newDevolucion.getFechaFinPrestamo().toString())
//                             .append("usuarioCedula", newDevolucion.getUsuarioCedula().toString())
//                             .append("usuarioNombre", newDevolucion.getUsuarioNombre().toString())
//                             .append("codigoLibro", newDevolucion.getCodigoLibro().toString())
//                             .append("usuarioPenalizado", newDevolucion.getUsuarioPenalizado().toString());

//                     collectionActividad.insertOne(newDevolucionBD);
//                 } else {
//                     System.err.println(
//                             " System exception en el método devolución: el préstamo de este libro a nombre del cliente no se encuentra en la BD");
//                 }
//             } else {
//                 System.err.println(
//                         " System exception en el método devolución: el libro que intenta devolver no se encuentra en la BD");
//             }
//         } catch (Exception e) {
//             System.err.println(" System exception en el método devolución: " + e);
//         }

//         MongoCursor<Document> cursor = collectionActividad.find().iterator();
//         while (cursor.hasNext()) {
//             Document doc = cursor.next();
//             dataComplete += ((Document) doc).toJson() + "\n";
//         }

//         return "Ahora los registros de la BD son: \n" + dataComplete;
//     }

//     /*
//      * public String renovacion(Actividad newRenovacion) throws RemoteException {
//      * try {
//      * Bson filtroLibro = Filters.and(Filters.eq("codigo",
//      * newDevolucion.getCodigoLibro()));
//      * Document libro = collectionLibro.find(filtroLibro).first();
//      * 
//      * if (libro != null) {
//      * Bson filtroActividad = Filters.and(Filters.eq("codigoLibro",
//      * newDevolucion.getCodigoLibro().toString()), Filters.eq("usuarioCedula",
//      * newDevolucion.getUsuarioCedula().toString()), Filters.eq("usuarioNombre",
//      * newDevolucion.getUsuarioNombre().toString()), Filters.or(Filters.eq("estado",
//      * EstadoPrestamo.PRESTADO.toString()), Filters.eq("estado",
//      * EstadoPrestamo.RENOVADO.toString())));
//      * Document prestamo = collectionActividad.find(filtroActividad).first();
//      * 
//      * System.out.println(filtroActividad.toString());
//      * 
//      * if (prestamo != null) {
//      * System.out.println(prestamo);
//      * 
//      * String fechaInit = prestamo.get("fechaInicioPrestamo").toString();
//      * LocalDate fechaInitFormat = LocalDate.parse(fechaInit);
//      * 
//      * Long cantidadDias = ChronoUnit.DAYS.between(fechaInitFormat,
//      * newDevolucion.getFechaFinPrestamo());
//      * 
//      * if (cantidadDias > 7 &&
//      * prestamo.get("estado").equals(EstadoPrestamo.PRESTADO.toString())) {
//      * newDevolucion.setUsuarioPenalizado(true);
//      * dataComplete +=
//      * "¡El sistema identificó que entregó el libro después del plazo máximo! Actualmente se encuentra penalizado \n"
//      * ;
//      * }
//      * 
//      * UpdateOptions opciones = new UpdateOptions().upsert(false);
//      * Document cambios = new Document("$set", new Document("estadoLibro",
//      * EstadoPrestamo.DEVUELTO));
//      * 
//      * collectionActividad.updateOne(filtroActividad, cambios, opciones);
//      * 
//      * Document newDevolucionBD = new Document("tipoActividad",
//      * newDevolucion.getTipoActividad().toString()).append("fechaInicioPrestamo",
//      * prestamo.get("fechaInicioPrestamo").toString()).append("fechaFinPrestamo",
//      * newDevolucion.getFechaFinPrestamo().toString()).append("usuarioCedula",
//      * newDevolucion.getUsuarioCedula().toString()).append("usuarioNombre",
//      * newDevolucion.getUsuarioNombre().toString()).append("codigoLibro",
//      * newDevolucion.getCodigoLibro().toString()).append("usuarioPenalizado",
//      * newDevolucion.getUsuarioPenalizado().toString());
//      * 
//      * collectionActividad.insertOne(newDevolucionBD);
//      * }
//      * else {
//      * System.err.
//      * println(" System exception en el método devolución: el préstamo de este libro a nombre del cliente no se encuentra en la BD"
//      * );
//      * }
//      * } else {
//      * System.err.
//      * println(" System exception en el método devolución: el libro que intenta devolver no se encuentra en la BD"
//      * );
//      * }
//      * } catch (Exception e) {
//      * System.err.println(" System exception en el método devolución: " + e);
//      * }
//      * 
//      * MongoCursor<Document> cursor = collectionActividad.find().iterator();
//      * while (cursor.hasNext()) {
//      * Document doc = cursor.next();
//      * dataComplete += ((Document) doc).toJson() + "\n";
//      * }
//      * 
//      * return "Ahora los registros de la BD son: \n" + dataComplete;
//      * }
//      */
// }
