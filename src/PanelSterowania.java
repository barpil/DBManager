package src;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelSterowania extends JPanel {
    private static PanelSterowania panelSterowania;
    private WyborTabeliCB wyborTabeliCB;
    private PanelSterowania() {
        this.setPreferredSize(new Dimension(150, 500));
        this.setBackground(Color.BLUE);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Użycie BoxLayout.Y_AXIS dla układu pionowego
        JPanel wyborTabeliPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,10));
        wyborTabeliPanel.setMaximumSize(new Dimension(150,50));
        wyborTabeliCB= new WyborTabeliCB();
        wyborTabeliPanel.add(wyborTabeliCB);
        this.add(wyborTabeliPanel);
        dodajElementy();


    }

    private void dodajElementy() {



        JPanel insertPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
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
    public void zaktualizujWyborTabelCB(){

            wyborTabeliCB.zaktualizujOpcje();


    }

    public static PanelSterowania getPanelSterowania() {
        if(panelSterowania==null){
            panelSterowania= new PanelSterowania();
        }
        return panelSterowania;
    }

    private class WyborTabeliCB extends JComboBox {
        WyborTabeliCB(){
            this.setPreferredSize(new Dimension(120,30));
            this.addActionListener(e -> {
                BazaDanych.getBazaDanych().setNazwaTabeli((String)this.getSelectedItem());
              try {
                  BazaDanych.getBazaDanych().zaktualizujBaze();
                  PanelElementow.zaladujTabele();
                  Menu.getMenu().zaktualizujMenu();
                } catch (SQLException ex) {
                   throw new RuntimeException(ex);
                }

            });
        }

        public void zaktualizujOpcje() {
            this.removeAllItems();
            try {
                List<String> listaTabel = BazaDanych.getBazaDanych().getNazwyTabel();
                for(String nazwa: listaTabel){
                    this.addItem(nazwa);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,"An error occured while loading table names", "Table name error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
