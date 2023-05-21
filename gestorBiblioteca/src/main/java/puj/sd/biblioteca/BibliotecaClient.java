package puj.sd.biblioteca;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BibliotecaClient implements Serializable {
    static Scanner sn = new Scanner(System.in);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        if (args.length != 1) {
            System.out.println("Faltan parámetros en la invocación.");
            System.out.println(
                    "FORMA: NombrePrograma direccionServer:puerto: BibliotecaClient 10.43.101.172:1099");
        } else {
            System.out.println("========= BIENVENIDO =========");
            menu(args[0]);
        }
    }
            
    public static void menu(String direccion) throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        Biblioteca mibiblioteca = (Biblioteca) Naming.lookup("rmi://" + direccion + "/" + "MiBiblioteca");
        String mensaje = "";
        boolean salir = false;
        char opcion;

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
                        mensaje = mibiblioteca.sendMessage(prestamo);
                        printRequestInformation(prestamo, mensaje);
                        break;
                    case 'D':
                        Actividad devolucion = saveReturnInformation();
                        mensaje = mibiblioteca.sendMessage(devolucion);
                        printRequestInformation(devolucion, mensaje);
                        break;
                    case 'R':
                        Actividad renovacion = saveRenewalInformation();
                        // mensaje = mibiblioteca.renovacion(renovacion);
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

        Actividad newPrestamo = new Actividad(TipoActividad.PRESTAMO, fechaInicio, fechaFin, cedulaCliente,
                nombreCliente, codigoLibro, EstadoPrestamo.PRESTADO);

        return newPrestamo;
    }

    public static Actividad saveReturnInformation() {
        System.out.println("Digite la cedula del cliente: ");
        Long cedulaCliente = sn.nextLong();

        System.out.println("Digite el nombre del cliente: ");
        String nombreCliente = sn.next();

        System.out.println("Digite el codigo del libro: ");
        String codigoLibro = sn.next();

        LocalDate fechaDevolucion = LocalDate.now();

        Actividad newDevolucion = new Actividad(TipoActividad.DEVOLUCION, fechaDevolucion, cedulaCliente, nombreCliente, codigoLibro, false);

        return newDevolucion;
    }

    public static Actividad saveRenewalInformation() {
        Actividad newRenovacion = new Actividad();
        return newRenovacion;
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
