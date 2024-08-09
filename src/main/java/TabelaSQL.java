import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

abstract public class TabelaSQL extends JTable {
    String[] nazwyKolumnTabeli=null;
    Object[][] daneTabeli=null;


    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    TabelaSQL(){
        updateColumnNames();
        updateData();
        model.setDataVector(daneTabeli, nazwyKolumnTabeli);
        this.setModel(model);

        PopUpMenuTabeli popUpMenuTabeli = new PopUpMenuTabeli(this);
    }

    private void updateColumnNames(){
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

    class PopUpMenuTabeli extends JPopupMenu{
        private final PopUpMenuTabeli thisPopUpMenu = this;
        PopUpMenuTabeli(TabelaSQL tabela){

            JMenuItem deleteRows = new JMenuItem("Delete rows");
            deleteRows.addActionListener(e -> {
                try {
                    BazaDanych.getBazaDanych().usunDane(tabela.getSelectedRows());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });


            this.add(deleteRows);

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
