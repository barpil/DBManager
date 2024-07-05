package src;

import javax.swing.*;
import java.awt.*;

public class PanelSterowania extends JPanel {
    PanelSterowania() {
        this.setPreferredSize(new Dimension(150, 500));
        this.setBackground(Color.BLUE);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Użycie BoxLayout.Y_AXIS dla układu pionowego
        dodajElementy();
    }

    private void dodajElementy() {
        JPanel insertPanel = new JPanel();
        insertPanel.setMinimumSize(new Dimension(150,50));
        insertPanel.setMaximumSize(new Dimension(150,50));
        this.add(insertPanel);
        InsertOkno insertOkno = new InsertOkno();
        insertPanel.add(insertOkno);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        btnPanel.setMinimumSize(new Dimension(150,50));
        btnPanel.setMaximumSize(new Dimension(150,50));
        this.add(btnPanel);
        JButton addBtn = new JButton("+");
        addBtn.setPreferredSize(new Dimension(50,50));
        btnPanel.add(addBtn);
        JButton delIdBtn = new JButton("-id");
        delIdBtn.setPreferredSize(new Dimension(50,50));
        btnPanel.add(delIdBtn);
        JButton delNazwaBtn = new JButton("-n");
        delNazwaBtn.setPreferredSize(new Dimension(50,50));
        btnPanel.add(delNazwaBtn);

        this.add(Box.createVerticalGlue());

        addBtn.addActionListener(e -> {
            insertOkno.setLastAction("addAction");
        });
        delIdBtn.addActionListener(e -> {
            insertOkno.setLastAction("delActionId");
        });
        delNazwaBtn.addActionListener(e -> {
            insertOkno.setLastAction("delActionNazwa");
        });
    }
}
