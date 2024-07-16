import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Tabela extends JTable {
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    Tabela(){
       updateModel();
       PopUpMenuTabeli popUpMenuTabeli = new PopUpMenuTabeli(this);
    }

    public void updateModel(){
        int liczbaKolumn = BazaDanych.getBazaDanych().getInformacjeOTabeli().getLiczbaKolumn();
        String nazwyKolumn[] = new String[liczbaKolumn];
        int i=0;
        for(String nazwaKolumny: BazaDanych.getBazaDanych().getInformacjeOTabeli().getInformacjaOKolumnie(InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY)){
            nazwyKolumn[i]=nazwaKolumny;
            i++;
        }
        model.setDataVector(BazaDanych.getBazaDanych().getData(), nazwyKolumn);
        this.setModel(model);

    }

    class PopUpMenuTabeli extends JPopupMenu{
        private final PopUpMenuTabeli thisPopUpMenu = this;
        PopUpMenuTabeli(Tabela tabela){

            JMenuItem usunWiersz = new JMenuItem("Usuń wiersz");
            usunWiersz.addActionListener(e -> {
                try {
                    BazaDanych.getBazaDanych().usunDane(tabela.getSelectedRow());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            this.add(usunWiersz);

            tabela.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.isPopupTrigger() && tabela.getSelectedRows().length>0){
                        thisPopUpMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }


            });
        }
    }
}
