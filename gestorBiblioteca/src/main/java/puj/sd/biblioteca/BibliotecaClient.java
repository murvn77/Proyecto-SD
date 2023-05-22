package puj.sd.biblioteca;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class BibliotecaClient implements Serializable {
    static Scanner sn = new Scanner(System.in);

    public static void main(String[] args)
            throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        String mensaje = "";
        boolean salir = false;
        char opcion;
        ZMQ.Socket socket;

        try (ZContext context = new ZContext()) {
            System.out.println("Conectando al servidor");
            socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");

            while (!salir) {
                System.out.println("P. Prestamo");
                System.out.println("D. Devolución");
                System.out.println("R. Renovación");
                System.out.println("S. Salir");
                try {
                    System.out.println("Escribe una de las opciones");
                    opcion = sn.next().charAt(0);

                    switch (opcion) {
                        case 'P':
                            Actividad prestamo = saveLoanInformation();
                            System.out.println("Conectando al servidor");
                            byte[] prestamoSe = serializeObject(prestamo);
                            socket.send(prestamoSe, 0);
                            socket.recv(0);

                            printRequestInformation(prestamo, mensaje);
                            break;
                        case 'D':
                            Actividad devolucion = saveReturnInformation();
                            System.out.println("Conectando al servidor");
                            byte[] devolucionSe = serializeObject(devolucion);
                            socket.send(devolucionSe, 0);
                            socket.recv(0);

                            printRequestInformation(devolucion, mensaje);
                            break;
                        case 'R':
                            Actividad renovacion = saveRenewalInformation();
                            printRequestInformation(renovacion, mensaje);
                            break;
                        case 'S':
                            System.out.println("Gracias por visitar nuestro sistema. Te esperamos pronto.");
                            break;
                        default:
                            System.out.println("Sólo son permitidas las letras: P, D, R");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Debes insertar un número");
                    sn.next();
                }
            }
        }
    }

    public static byte[] serializeObject(Actividad newActividad) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(newActividad);
            oos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static Actividad saveLoanInformation() throws RemoteException {
        LocalDate fechaInicio = LocalDate.now();

        System.out.println("Digite la fecha límite: ");
        String fechaFinUser = sn.next();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate fechaFin = LocalDate.parse(fechaFinUser, formato);

        System.out.println("Digite la cedula del cliente: ");
        Long cedulaCliente = sn.nextLong();

        System.out.println("Digite el nombre del cliente: ");
        String nombreCliente = sn.next();

        System.out.println("Digite el codigo del libro: ");
        String codigoLibro = sn.next();

        return new Actividad(TipoActividad.PRESTAMO, fechaInicio, fechaFin, cedulaCliente,
                nombreCliente, codigoLibro, EstadoPrestamo.PRESTADO);
    }

    public static Actividad saveReturnInformation() {
        System.out.println("Digite la cedula del cliente: ");
        Long cedulaCliente = sn.nextLong();

        System.out.println("Digite el nombre del cliente: ");
        String nombreCliente = sn.next();

        System.out.println("Digite el codigo del libro: ");
        String codigoLibro = sn.next();

        LocalDate fechaDevolucion = LocalDate.now();

        return new Actividad(TipoActividad.DEVOLUCION, fechaDevolucion, cedulaCliente, nombreCliente, codigoLibro,
                false);

    }

    public static Actividad saveRenewalInformation() {
        return new Actividad();
    }

    public static void printRequestInformation(Actividad actividad, String mensaje) {
        if (actividad.getTipoActividad().equals(TipoActividad.PRESTAMO)) {
            System.out.println("NUEVA PETICIÓN DE PRESTAMO ENVIADA");
            System.out.println("Tipo de actividad: " + actividad.getTipoActividad());
            System.out.println("Nombre del cliente: " + actividad.getUsuarioNombre());
            System.out.println("Cédula del cliente: " + actividad.getUsuarioCedula());
            System.out.println("Código del libro: " + actividad.getCodigoLibro());
            System.out.println("Prestamo hecho el: " + actividad.getFechaInicioPrestamo());
            System.out.println("Prestamo válido hasta: " + actividad.getFechaFinPrestamo());
            System.out.println(mensaje);
            System.out.println("====================================================================");
        }

        if (actividad.getTipoActividad().equals(TipoActividad.DEVOLUCION)) {
            System.out.println("NUEVA PETICIÓN DE DEVOLUCIÓN ENVIADA");
            System.out.println("Tipo de actividad: " + actividad.getTipoActividad());
            System.out.println("Nombre del cliente: " + actividad.getUsuarioNombre());
            System.out.println("Cédula del cliente: " + actividad.getUsuarioCedula());
            System.out.println("Código del libro: " + actividad.getCodigoLibro());
            System.out.println("Devolución hecha el: " + actividad.getFechaFinPrestamo());
            System.out.println(mensaje);
            System.out.println("====================================================================");
        }

        if (actividad.getTipoActividad().equals(TipoActividad.RENOVACION)) {
            System.out.println("NUEVA PETICIÓN DE RENOVACIÓN ENVIADA");
            System.out.println("Tipo de actividad: " + actividad.getTipoActividad());
            System.out.println("Nombre del cliente: " + actividad.getUsuarioNombre());
            System.out.println("Cédula del cliente: " + actividad.getUsuarioCedula());
            System.out.println("Código del libro: " + actividad.getCodigoLibro());
            System.out.println("Prestamo hecho el: " + actividad.getFechaInicioPrestamo());
            System.out.println("Renovación válida hasta: " + actividad.getFechaFinPrestamo());
            System.out.println(mensaje);
            System.out.println("====================================================================");
        }
    }
}
