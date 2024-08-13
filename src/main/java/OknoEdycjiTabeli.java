import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OknoEdycjiTabeli extends JDialog {

    OknoEdycjiTabeli(Frame frame, String nazwaOkna, boolean modal){
        super(frame, nazwaOkna, modal);
        this.setSize(new Dimension(400, 400));
        this.setResizable(true);

        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty(){
        JPanel panelGlowny = new JPanel();
        panelGlowny.setLayout(new BoxLayout(panelGlowny, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane();
        TabelaEdycji tabelaPodgladu = new TabelaEdycji();
        scrollPane.setViewportView(tabelaPodgladu);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelGlowny.add(scrollPane);

        JPanel panelDodawaniaWierszy = new JPanel();
        panelDodawaniaWierszy.setLayout(new BoxLayout(panelDodawaniaWierszy, BoxLayout.X_AXIS));
        panelDodawaniaWierszy.setPreferredSize(new Dimension(400, 50));

        class TabelaDodawaniaWierszy extends TabelaSQL{

            TabelaDodawaniaWierszy(){
                super(true);
            }

            @Override
            protected void updateData() {
                daneTabeli= new Object[1][nazwyKolumnTabeli.length];
                for(int i=0; i<nazwyKolumnTabeli.length;i++){
                    daneTabeli[0][i]=null;
                }
            }



            private void wyczyscWiersz() {
                updateModel();
            }

            private void dodajWiersz() {
                Row dodawanyWiersz = new Row(InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn());
                if (this.isEditing()) {
                    this.getCellEditor().stopCellEditing();
                }
                for(int i=0; i<InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn();i++){
                    dodawanyWiersz.addPole(InformacjeOTabeli.getInformacjeOTabeli().getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY), this.getModel().getValueAt(0,i));

                }

                tabelaPodgladu.dodajWiersz(dodawanyWiersz);
            }
        }





        JScrollPane dolnyScrollPane = new JScrollPane();
        TabelaDodawaniaWierszy tabelaDodawaniaWierszy = new TabelaDodawaniaWierszy();
        dolnyScrollPane.setViewportView(tabelaDodawaniaWierszy);
        dolnyScrollPane.setPreferredSize(new Dimension(400, 50));

        panelDodawaniaWierszy.add(dolnyScrollPane);


        JPanel panelPrzyciskow = new JPanel();
        panelPrzyciskow.setLayout(new BoxLayout(panelPrzyciskow, BoxLayout.Y_AXIS));
        Button dodajWierszBtn = new Button("+");

        dodajWierszBtn.addActionListener(e -> {
            tabelaDodawaniaWierszy.dodajWiersz();
            tabelaDodawaniaWierszy.wyczyscWiersz();
        });
        panelPrzyciskow.add(dodajWierszBtn);

        Button wyczyscWierszBtn = new Button("Clear");
        wyczyscWierszBtn.addActionListener(e -> {
            tabelaDodawaniaWierszy.wyczyscWiersz();
        });
        panelPrzyciskow.add(wyczyscWierszBtn);


        panelDodawaniaWierszy.add(panelPrzyciskow);

        panelGlowny.add(panelDodawaniaWierszy);

        this.add(panelGlowny);
    }











}

