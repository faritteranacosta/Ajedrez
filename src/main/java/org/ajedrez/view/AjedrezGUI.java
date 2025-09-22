package org.ajedrez.view;

import org.ajedrez.entity.pieza.Pieza;

import javax.swing.*;
import java.awt.*;

public class AjedrezGUI extends JFrame {
    public AjedrezGUI() {
        setTitle("Juego de Ajedrez");
        setSize(860, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout en dos columnas
        setLayout(new BorderLayout());

        // Crear tablero
        TableroGUI tableroPanel = new TableroGUI();

        // Crear panel de información
        InfoPartidaPanel infoPanel = new InfoPartidaPanel();

        // Conectar tablero con panel de info (para actualizar jugadas)
        tableroPanel.setInfoPanel(infoPanel);

        add(tableroPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AjedrezGUI().setVisible(true);
        });
    }
}
