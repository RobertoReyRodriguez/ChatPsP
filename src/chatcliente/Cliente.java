package chatcliente;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * Clase para gestionar el cliente.
 */
public class Cliente extends Thread {
    /** Socket para comunicarse con el servidor. */
    private Socket socket;
    /** Stream para el envío de objetos al servidor. */
    private ObjectOutputStream objectOutputStream;
    /** Stream para el envío de objetos al servidor. */
    private ObjectInputStream objectInputStream;
    /** Ventana para la interfaz gráfica del cliente. */
    private final VentanaC ventana;
    /** Identificador único del cliente dentro del chat. */
    private String identificador;
    /** Determina si el cliente escucha o no al servidor */
    private boolean escuchando;
    /** Almacena la IP del host en el que se ejecuta el servidor. */
    private final String host;
    /** Puerto por el cual el servidor escucha las conexiones de los clientes. */
    private final int puerto;

    /**
     * Constructor de la clase
     * 
     * @param ventana
     * @param host
     * @param puerto
     * @param nombre
     */
    public Cliente(VentanaC ventana, String host, Integer puerto, String nombre) {
        this.ventana = ventana;
        this.host = host;
        this.puerto = puerto;
        this.identificador = nombre;
        escuchando = true;
        this.start();
    }

    /**
     * Método run del hilo de comunicación del lado del cliente.
     */
    public void run() {
        try {
            socket = new Socket(host, puerto);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Conexion exitosa!!!!");
            this.enviarSolicitudConexion(identificador);
            this.escuchar();
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(ventana, "Conexión rehusada, servidor desconocido,\n"
                    + "puede que haya ingresado una ip incorrecta\n"
                    + "o que el servidor no este corriendo.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, "Conexión rehusada, error de Entrada/Salida,\n"
                    + "puede que haya ingresado una ip o un puerto\n"
                    + "incorrecto, o que el servidor no este corriendo.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        }

    }

    /**
     * Método que cierra el socket y los streams de comunicación.
     */
    public void desconectar() {
        try {
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
            escuchando = false;
        } catch (Exception e) {
            System.err.println("Error al cerrar los elementos de comunicación del cliente.");
        }
    }

    /**
     * Método que envia un mensaje hacia el servidor.
     * @param cliente_receptor
     * @param mensaje
     */
    public void enviarMensaje(String cliente_receptor, String mensaje) {
        LinkedList<String> lista = new LinkedList<>();
        lista.add("MENSAJE");
        lista.add(identificador);
        lista.add(cliente_receptor);
        lista.add(mensaje);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
        }
    }

    /*
     * Método que escucha constantemente lo que el servidor dice.
     */
    public void escuchar() {
        try {
            while (escuchando) {
                Object aux = objectInputStream.readObject();
                if (aux != null) {
                    if (aux instanceof LinkedList) {
                        ejecutar((LinkedList<String>) aux);
                    } else {
                        System.err.println("Se recibió un Objeto desconocido a través del socket");
                    }
                } else {
                    System.err.println("Se recibió un null a través del socket");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "La comunicación con el servidor se ha\n"
                    + "perdido, este chat tendrá que finalizar.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        }
    }

    /**
     * Método que ejecuta una serie de instruccines dependiendo del mensaje que el
     * cliente reciba del servidor.
     * @param lista
     */
    public void ejecutar(LinkedList<String> lista) {
        String tipo = lista.get(0);
        switch (tipo) {
            case "CONEXION_ACEPTADA":
                identificador = lista.get(1);
                ventana.sesionIniciada(identificador);
                for (int i = 2; i < lista.size(); i++) {
                    ventana.addContacto(lista.get(i));
                }
                break;
            case "NUEVO_USUARIO_CONECTADO":
                ventana.addContacto(lista.get(1));
                break;
            case "USUARIO_DESCONECTADO":
                ventana.eliminarContacto(lista.get(1));
                break;
            case "MENSAJE":
                ventana.addMensaje(lista.get(1), lista.get(3));
                break;
            default:
                break;
        }
    }

    /**
     * Método para solicitar la agregación del cliente a la lista de clientes
     * @param identificador
     */
    private void enviarSolicitudConexion(String identificador) {
        LinkedList<String> lista = new LinkedList<>();
        lista.add("SOLICITUD_CONEXION");
        lista.add(identificador);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
        }
    }

    /**
     * Tramita una notificación de desconexión para eliminar al cliente 
     * de las listas de contactos y de la lista de clientes
     */
    public void confirmarDesconexion() {
        LinkedList<String> lista = new LinkedList<>();
        lista.add("SOLICITUD_DESCONEXION");
        lista.add(identificador);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
        }
    }

    /**
     * Devuelve el identificador único del cliente
     * @return identificador del cliente
     */
    public String getIdentificador() {
        return identificador;
    }
}