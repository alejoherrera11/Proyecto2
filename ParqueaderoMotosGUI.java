import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ParqueaderoMotosGUI {

    private static int maxPuestos = 30;
    private static int puestosOcupados = 0;
    private static int precioPorHora = 1000;
    private static String[] placas = new String[maxPuestos];
    private static long[] horasEntrada = new long[maxPuestos];
    private static long[] horasSalida = new long[maxPuestos];
    private static int[] puestosCascos = new int[maxPuestos];
    private static ArrayList<String> registros = new ArrayList<>();

    public static void main(String[] args) {
        cargarRegistros();

        JFrame frame = new JFrame("Parqueadero de Motos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        frame.add(mainPanel);

        JButton ingresarButton = new JButton("Ingresar Moto");
        JButton salirButton = new JButton("Salir Moto");
        JButton verRegistrosButton = new JButton("Ver Registros");

        JTextField placaField = new JTextField(10);
        JTextField salidaPlacaField = new JTextField(10);
        JTextField puestoCascoField = new JTextField(5);
        JTextArea infoTextArea = new JTextArea(10, 30);
        infoTextArea.setEditable(false);

        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String placa = placaField.getText();
                int puestoCasco = Integer.parseInt(puestoCascoField.getText());
                ingresarMoto(placa, puestoCasco);
                placaField.setText("");
                puestoCascoField.setText("");
                actualizarStatus(infoTextArea);
                JOptionPane.showMessageDialog(null,
                        "Moto ingresada:\nPlaca: " + placa + "\nPuesto del casco: " + puestoCasco +
                                "\nHora actual: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        });

        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String placa = salidaPlacaField.getText();
                //
                salirMoto(placa);
                salidaPlacaField.setText("");
                actualizarStatus(infoTextArea);
            }
        });

        verRegistrosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verRegistros();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Añadir márgenes


        mainPanel.add(new JLabel("PARQUEADERO LOS MARAVILLOSOS"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("NIT: 950.648.874-9"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Horario 24 Hrs"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("CALLE 10 A3 # 38-41"), gbc);
        gbc.gridy++;

        mainPanel.add(new JLabel("Placa Entrada: "), gbc);
        gbc.gridx++;
        mainPanel.add(placaField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        mainPanel.add(new JLabel("Puesto del casco: "), gbc);
        gbc.gridx++;
        mainPanel.add(puestoCascoField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        mainPanel.add(ingresarButton, gbc);
        gbc.gridx=0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Placa (Salida): "), gbc);
        gbc.gridx++;
        mainPanel.add(salidaPlacaField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        mainPanel.add(salirButton, gbc);
        gbc.gridy++;
        mainPanel.add(verRegistrosButton, gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Ocupacion Parqueadero: "), gbc);
        gbc.gridy++;
        gbc.gridwidth = 2; // Hacer que el campo de texto ocupe 2 columnas
        mainPanel.add(new JScrollPane(infoTextArea), gbc);

        // Configurar para centrar el panel
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    private static void ingresarMoto(String placa, int puestoCasco) {
        if (puestosOcupados < maxPuestos) {
            placas[puestosOcupados] = placa;
            horasEntrada[puestosOcupados] = System.currentTimeMillis();
            puestosCascos[puestosOcupados] = puestoCasco;
            puestosOcupados++;
            guardarRegistro(placa, "Ingreso", puestoCasco);
        } else {
            JOptionPane.showMessageDialog(null, "El parqueadero está lleno, no se pueden ingresar más motos.");
        }

    }

    private static void salirMoto(String placa) {
        int puesto = -1;
        for (int i = 0; i < puestosOcupados; i++) {
            if (placas[i] != null && placas[i].equals(placa)) {
                puesto = i;
                break;
            }
        }

        if (puesto != -1) {
            horasSalida[puesto] = System.currentTimeMillis();
            long tiempoEstacionado = horasSalida[puesto] - horasEntrada[puesto];
            long horasEstacionado = tiempoEstacionado / (60 * 60 * 1000);
            if (tiempoEstacionado % (60 * 60 * 1000) > 0) {
                horasEstacionado++;
            }
            int precioPagar = (int) (horasEstacionado * precioPorHora);
            JOptionPane.showMessageDialog(null,
                    "Placa: " + placas[puesto] + "\n" +
                            "Hora de ingreso: " + new java.util.Date(horasEntrada[puesto]) + "\n" +
                            "Puesto del casco: " + puestosCascos[puesto] + "\n" +
                            "Hora de salida: " + new java.util.Date(horasSalida[puesto]) + "\n" +
                            "Tiempo estacionado (horas): " + horasEstacionado + "\n" +
                            "Precio a pagar: $" + precioPagar);
            puestosOcupados--;
            placas[puesto] = null;
            puestosCascos[puesto] = 0;
            guardarRegistro(placa, "Salida", puestosCascos[puesto]);
        } else {
            JOptionPane.showMessageDialog(null, "La moto con la placa " + placa + " no se encuentra en el parqueadero.");
        }
    }


    private static void actualizarStatus(JTextArea infoTextArea) {
        infoTextArea.setText("Puestos disponibles: " + (maxPuestos - puestosOcupados) + "\n" +
                "Puestos ocupados: " + puestosOcupados);
    }

    private static void guardarRegistro(String placa, String accion, int puestoCasco) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        String registro = dateFormat.format(fecha) + " - Placa: " + placa + " - " + accion +
                " - Puesto del casco: " + puestoCasco;
        registros.add(registro);
        guardarRegistros();
    }

    private static void guardarRegistros() {
        try {
            PrintWriter writer = new PrintWriter("registros.txt");
            for (String registro : registros) {
                writer.println(registro);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void cargarRegistros() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("registros.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                registros.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void verRegistros() {
        JFrame frame = new JFrame("Registros del Parqueadero");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);

        JTextArea registrosArea = new JTextArea(10, 30);
        registrosArea.setEditable(false);

        for (String registro : registros) {
            registrosArea.append(registro + "\n");
        }

        JScrollPane scrollPane = new JScrollPane(registrosArea);
        frame.add(scrollPane);

        frame.setLocationRelativeTo(null); // Centrar la ventana de registros

        frame.setVisible(true);
    }
}