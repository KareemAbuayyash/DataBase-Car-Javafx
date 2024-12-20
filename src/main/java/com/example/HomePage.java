
package com.example;
import javax.swing.*;

public class HomePage extends JFrame {
    public HomePage() {
        setTitle("Home Page");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to the Home Page!", SwingConstants.CENTER);
        add(welcomeLabel);

        setVisible(true);
    }
}