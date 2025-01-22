package chatcliente;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * Clase que maneja la interfaz gráfica del cliente.
 */
public class VentanaC extends JFrame {
    private JButton btnEnviar;
    private JComboBox cmbContactos;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextArea txtHistorial;
    private JTextField txtMensaje;
    /** Puerto por defecto para la aplicación. */
    private final String DEFAULT_PORT = "10101";
    /** IP por defecto (localhost) para el servidor. */
    private final String DEFAULT_IP = "127.0.0.1";
    /** Almacena el cliente */
    private final Cliente cliente;

    /**
     * Constructor de la ventana.
     */
    public VentanaC() {
        initComponentes();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String ip_puerto_nombre[] = getIPPuertoNombre();
        String ip = ip_puerto_nombre[0];
        String puerto = ip_puerto_nombre[1];
        String nombre = ip_puerto_nombre[2];
        cliente = new Cliente(this, ip, Integer.valueOf(puerto), nombre);
    }

    /**
     * Método para inciar componentes y formulario.
     */
    private void initComponentes() {

        jScrollPane1 = new JScrollPane();
        txtHistorial = new JTextArea();
        txtMensaje = new JTextField();
        cmbContactos = new JComboBox();
        btnEnviar = new JButton();
        jLabel1 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent evt) {
                formWindowClosed(evt);
            }

            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        txtHistorial.setEditable(false);
        txtHistorial.setColumns(20);
        txtHistorial.setRows(5);
        jScrollPane1.setViewportView(txtHistorial);

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        jLabel1.setText("Destinatario:");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtMensaje)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEnviar))
                                        .addComponent(jScrollPane1)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout
                                                .createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cmbContactos, GroupLayout.PREFERRED_SIZE, 198,
                                                        GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cmbContactos, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtMensaje, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEnviar))
                                .addContainerGap()));
        pack();
    }

    /**
     * Al hacer clic en el botón de enviar, pide al cliente que envíe al servidor el mensaje.
     * @param evt
     */
    private void btnEnviarActionPerformed(ActionEvent evt) {
        if (cmbContactos.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe escoger un destinatario válido, si no \n"
                    + "hay uno, espere a que otro usuario se conecte\n"
                    + "para poder chatear con él.");
            return;
        }
        String cliente_receptor = cmbContactos.getSelectedItem().toString();
        String mensaje = txtMensaje.getText();
        cliente.enviarMensaje(cliente_receptor, mensaje);
        txtHistorial.append("## Yo -> " + cliente_receptor + " ## : \n" + mensaje + "\n");
        txtMensaje.setText("");
    }

    private void formWindowClosed(WindowEvent evt) {
    }

    /**
     * Cuando la ventana se este cerrando se notifica al servidor que el cliente
     * se ha desconectado, por lo que los demás clientes del chat no podrán enviarle
     * más mensajes.
     * @param evt
     */
    private void formWindowClosing(WindowEvent evt) {
        cliente.confirmarDesconexion();
    }

    /**
     * Main
     * @param args
     */
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaC().setVisible(true);
            }
        });
    }

    /**
     * Agrega un contacto al JComboBox de contactos.
     * @param contacto
     */
    public void addContacto(String contacto) {
        cmbContactos.addItem(contacto);
    }

    /**
     * Agrega un nuevo mensaje al historial de la conversación.
     * @param emisor
     * @param mensaje
     */
    public void addMensaje(String emisor, String mensaje) {
        txtHistorial.append("##### " + emisor + " ##### : \n" + mensaje + "\n");
    }

    /**
     * Se configura el título de la ventana para una nueva sesión.
     * @param identificador
     */
    public void sesionIniciada(String identificador) {
        this.setTitle(" --- " + identificador + " --- ");
    }

    /**
     * Método que abre una ventana para que el usuario ingrese la IP del host en
     * el que corre el servidor, el puerto con el que escucha y el nombre con el
     * que quiere participar en el chat.
     * @return array con el puerto, la ip y el nombre
     */
    private String[] getIPPuertoNombre() {
        String s[] = new String[3];
        s[0] = DEFAULT_IP;
        s[1] = DEFAULT_PORT;
        JTextField ip = new JTextField(20);
        JTextField puerto = new JTextField(20);
        JTextField usuario = new JTextField(20);
        ip.setText(DEFAULT_IP);
        puerto.setText(DEFAULT_PORT);
        usuario.setText("Usuario");
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(3, 2));
        myPanel.add(new JLabel("IP del Servidor:"));
        myPanel.add(ip);
        myPanel.add(new JLabel("Puerto de la conexión:"));
        myPanel.add(puerto);
        myPanel.add(new JLabel("Escriba su nombre:"));
        myPanel.add(usuario);
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Configuraciones de la comunicación", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            s[0] = ip.getText();
            s[1] = puerto.getText();
            s[2] = usuario.getText();
        } else {
            System.exit(0);
        }
        return s;
    }

    /**
     * Método que elimina un cliente de la lista de contactos, este se llama
     * cuando un usuario cierra sesión.
     * @param identificador
     */
    public void eliminarContacto(String identificador) {
        for (int i = 0; i < cmbContactos.getItemCount(); i++) {
            if (cmbContactos.getItemAt(i).toString().equals(identificador)) {
                cmbContactos.removeItemAt(i);
                return;
            }
        }
    }
}
