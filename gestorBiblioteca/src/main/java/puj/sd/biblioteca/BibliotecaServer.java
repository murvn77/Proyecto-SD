package puj.sd.biblioteca;

import java.io.Serializable;

public class BibliotecaServer implements Serializable {
    public static void main(String args[]) {
        try {
            new BibliotecaImpl("rmi://localhost:1099" + "/MiBiblioteca");
        } catch (Exception e) {
            System.err.println("System exception" + e);
        }
    }
}