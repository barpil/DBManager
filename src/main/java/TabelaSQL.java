import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

abstract public class TabelaSQL extends JTable {
    String[] nazwyKolumnTabeli=null;
    Object[][] daneTabeli=null;


    DefaultTableModel model;

    TabelaSQL(boolean isCellEditable){
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return isCellEditable;
            }
        };
        updateModel();


    }



    protected void updateColumnNames(){
        int liczbaKolumn = BazaDanych.getBazaDanych().getInformacjeOTabeli().getLiczbaKolumn();
        String nazwyKolumn[] = new String[liczbaKolumn];
        int i=0;
        for(String nazwaKolumny: BazaDanych.getBazaDanych().getInformacjeOTabeli().getInformacjaOKolumnie(InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY)){
            nazwyKolumn[i]=nazwaKolumny;
            i++;
        }
        nazwyKolumnTabeli = nazwyKolumn;
    }



    protected abstract void updateData();

    public void updateModel(){
        updateColumnNames();
        updateData();


        model.setDataVector(daneTabeli, nazwyKolumnTabeli);

        this.setModel(model);
    }


}