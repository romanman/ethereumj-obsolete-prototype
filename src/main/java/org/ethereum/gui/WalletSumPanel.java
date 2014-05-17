package org.ethereum.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * www.ethereumJ.com
 * User: Roman Mandeleil
 * Created on: 17/05/14 12:32
 */
public class WalletSumPanel extends JPanel{

    public WalletSumPanel() {

        this.setBackground(Color.WHITE);
        double width = this.getSize().getWidth();
        this.setPreferredSize(new Dimension(500, 50));
        Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        Border empty = new EmptyBorder(5, 8, 5, 8);
        CompoundBorder border = new CompoundBorder(line, empty);

        JLabel addressField = new JLabel();
        addressField.setPreferredSize(new Dimension(276, 50));
        this.add(addressField);

        JTextField amount = new JTextField();
        amount.setBorder(border);
        amount.setEnabled(true);
        amount.setEditable(false);
        amount.setText("234 * 10^9");
        amount.setForeground(new Color(143, 170, 220));
        amount.setBackground(Color.WHITE);
        this.add(amount);

        URL payoutIconURL = ClassLoader.getSystemResource("buttons/wallet-pay.png");
        ImageIcon payOutIcon = new ImageIcon(payoutIconURL);
        JLabel payOutLabel = new JLabel(payOutIcon);
        payOutLabel.setToolTipText("Payout for all address list");
        payOutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payOutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("boom");
            }
        });

        this.add(payOutLabel);
    }
}
