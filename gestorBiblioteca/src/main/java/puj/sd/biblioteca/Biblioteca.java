package puj.sd.biblioteca;

import java.io.Serializable;

public interface Biblioteca extends java.rmi.Remote, Serializable {
    /**
     * Firma de la función devolucion que presta un libro a un cliente
     * @param newDevolucion: un objeto del tipo Actividad
     * @return un string como confirmación de que la devolución se ha realizado
     */
    public String sendMessage(Actividad newPrestamo) throws java.rmi.RemoteException, InterruptedException;
}