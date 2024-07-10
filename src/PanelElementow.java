package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelElementow extends JScrollPane{
    private static PanelElementow panelElementow;
    private JTable jTable;
    DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private PanelElementow() {
        this.setPreferredSize(new Dimension(350, 500));
        this.setBackground(Color.GREEN);
        utworzElementy();
    }

    private void utworzElementy() {
        int liczbaKolumn = BazaDanych.getBazaDanych().getInformacjeOTabeli().getLiczbaKolumn();
        String nazwyKolumn[] = new String[liczbaKolumn];
        int i=0;
        for(String nazwaKolumny: BazaDanych.getBazaDanych().getInformacjeOTabeli().getInformacjaOKolumnie(InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY)){
            nazwyKolumn[i]=nazwaKolumny;
            i++;
        }
        model.setDataVector(BazaDanych.getBazaDanych().getData(), nazwyKolumn);
        jTable = new JTable(model);
        this.setViewportView(jTable);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    }

    public void updateModel() {
        int liczbaKolumn = BazaDanych.getBazaDanych().getInformacjeOTabeli().getLiczbaKolumn();
        String nazwyKolumn[] = new String[liczbaKolumn];
        int i=0;
        for(String nazwaKolumny: BazaDanych.getBazaDanych().getInformacjeOTabeli().getInformacjaOKolumnie(InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY)){
            nazwyKolumn[i]=nazwaKolumny;
            i++;
        }
        model.setDataVector(BazaDanych.getBazaDanych().getData(), nazwyKolumn );
        jTable.setModel(model);
    }

    public static PanelElementow getPanelElementow() {
        if(panelElementow==null){
            panelElementow= new PanelElementow();
        }
        return panelElementow;
    }

}
