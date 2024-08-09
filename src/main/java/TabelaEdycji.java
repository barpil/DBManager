import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class TabelaEdycji extends TabelaSQL {
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    TabelaEdycji(){
        updateModel();
        PopUpMenuTabeliEdycji popUpMenuTabeli = new PopUpMenuTabeliEdycji(this);
    }

    @Override
    protected void updateData() {
        
    }

    public void updateModel(){
        int liczbaKolumn = BazaDanych.getBazaDanych().getInformacjeOTabeli().getLiczbaKolumn();
        String nazwyKolumn[] = new String[liczbaKolumn];
        int i=0;
        for(String nazwaKolumny: BazaDanych.getBazaDanych().getInformacjeOTabeli().getInformacjaOKolumnie(InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY)){
            nazwyKolumn[i]=nazwaKolumny;
            i++;
        }
        model.setDataVector(null, nazwyKolumn);
        this.setModel(model);

    }

    class PopUpMenuTabeliEdycji extends JPopupMenu{
        private final PopUpMenuTabeliEdycji thisPopUpMenu = this;
        PopUpMenuTabeliEdycji(JTable tabela){

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
