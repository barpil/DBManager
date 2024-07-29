import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelSterowania extends JPanel {
    private static PanelSterowania panelSterowania;
    private WyborTabeliCB wyborTabeliCB;
    private ThreadProgressBar progressBar;

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



        this.add(Box.createVerticalGlue());
        progressBar= new ThreadProgressBar();
        this.add(progressBar);


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
            if(this.getSelectedItem()!=null){
                try {
                    BazaDanych.getBazaDanych().setNazwaTabeli((String) this.getSelectedItem());
                    BazaDanych.getBazaDanych().zaktualizujBaze();
                    PanelElementow.zaladujTabele();
                    Menu.getMenu().zaktualizujMenu();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
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

    public ThreadProgressBar getProgressBar() {
        return progressBar;
    }
}
