import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class PanelSterowania extends JPanel {
    private static PanelSterowania panelSterowania;
    private WyborTabeliCB wyborTabeliCB;
    private JPanel panelProgressBara;
    private ThreadProgressBar progressBar;
    private Button przyciskRozpoczecia;

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
        przyciskRozpoczecia= new Button("Start");
        przyciskRozpoczecia.addActionListener(e -> {
            BazaDanych.getBazaDanych().getSqlThreadQueue().rozpocznijWykonywanie();
            System.out.println("Klik");
        });
        this.add(przyciskRozpoczecia);
        panelProgressBara = new JPanel();
        panelProgressBara.setBackground(Color.MAGENTA);
        this.add(panelProgressBara);
        progressBar = new ThreadProgressBar();
        panelProgressBara.add(progressBar);



    }

    public ThreadProgressBar getProgressBar() {
        return progressBar;
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

    public JPanel getPanelProgressBara() {
        return panelProgressBara;
    }
}
