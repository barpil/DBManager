import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OknoEdycjiTabeli extends JDialog {
    TabelaEdycji tabelaPodgladu;
    OknoEdycjiTabeli(Frame frame, String nazwaOkna, boolean modal) {
        super(frame, nazwaOkna, modal);
        this.setSize(new Dimension(400, 400));
        this.setResizable(true);

        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty() {
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!tabelaPodgladu.dodawaneDane.isEmpty() || BazaDanych.getBazaDanych().getSqlThreadQueue().liczbaPozostalychWatkow()!=0) {
                    int potwierdzenieZamkniecia = JOptionPane.showConfirmDialog(
                            OknoEdycjiTabeli.this,
                            "Do you want to discard the changes?",
                            "Unsaved changes",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (potwierdzenieZamkniecia == JOptionPane.YES_OPTION) {
                        BazaDanych.getBazaDanych().getSqlThreadQueue().resetQueue();
                        OknoEdycjiTabeli.this.dispose();
                    }
                }else{
                    OknoEdycjiTabeli.this.dispose();
                }
            }
        });

        JPanel panelGlowny = new JPanel();
        panelGlowny.setLayout(new BoxLayout(panelGlowny, BoxLayout.Y_AXIS));

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setLayout(new BoxLayout(topButtonPanel, BoxLayout.X_AXIS));
        JLabel showNewOnlyLabel = new JLabel("New only");
        JCheckBox showNewOnlyCheckbox = new JCheckBox();
        MyItemListener myItemListener = new MyItemListener();
        showNewOnlyCheckbox.addItemListener(myItemListener);

        topButtonPanel.add(Box.createHorizontalGlue());
        topButtonPanel.add(showNewOnlyLabel);
        topButtonPanel.add(showNewOnlyCheckbox);

        panelGlowny.add(topButtonPanel);


        JScrollPane scrollPane = new JScrollPane();
        tabelaPodgladu = new TabelaEdycji();
        scrollPane.setViewportView(tabelaPodgladu);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelGlowny.add(scrollPane);

        JPanel panelDodawaniaWierszy = new JPanel();
        panelDodawaniaWierszy.setLayout(new BoxLayout(panelDodawaniaWierszy, BoxLayout.X_AXIS));
        panelDodawaniaWierszy.setPreferredSize(new Dimension(400, 50));

        class TabelaDodawaniaWierszy extends TabelaSQL {

            TabelaDodawaniaWierszy() {
                super(true);
            }

            @Override
            protected void updateData() {
                daneTabeli = new Object[1][nazwyKolumnTabeli.length];
                for (int i = 0; i < nazwyKolumnTabeli.length; i++) {
                    daneTabeli[0][i] = null;
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
                for (int i = 0; i < InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn(); i++) {
                    dodawanyWiersz.addPole(InformacjeOTabeli.getInformacjeOTabeli().getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY), this.getModel().getValueAt(0, i));

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

        JPanel panelPrzyciskowOkna = new JPanel();
        panelPrzyciskowOkna.setLayout(new BoxLayout(panelPrzyciskowOkna,BoxLayout.X_AXIS));
        panelPrzyciskowOkna.setPreferredSize(new Dimension(400,30));
        panelPrzyciskowOkna.add(Box.createHorizontalGlue());

        JButton applyButton = new JButton("Apply");
        applyButton.setPreferredSize(new Dimension(60,20));
        applyButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        applyButton.addActionListener(e -> {
            BazaDanych.getBazaDanych().dodajDane(tabelaPodgladu.getData());
            BazaDanych.getBazaDanych().getSqlThreadQueue().rozpocznijWykonywanie();
            while(BazaDanych.getBazaDanych().getSqlThreadQueue().liczbaPozostalychWatkow()!=0){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            this.dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60,20));
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cancelButton.addActionListener(e -> {
            BazaDanych.getBazaDanych().getSqlThreadQueue().resetQueue();
            this.dispose();
        });
        panelPrzyciskowOkna.add(applyButton);
        panelPrzyciskowOkna.add(Box.createRigidArea(new Dimension(10,0)));
        panelPrzyciskowOkna.add(cancelButton);
        panelPrzyciskowOkna.add(Box.createRigidArea(new Dimension(5,0)));

        panelGlowny.add(panelPrzyciskowOkna);

        this.add(panelGlowny);
    }

    class MyItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                tabelaPodgladu.setOnlyNewOption(true);
                System.out.println("Feature is enabled.");
            } else {
                tabelaPodgladu.setOnlyNewOption(false);
                System.out.println("Feature is disabled.");
            }
            tabelaPodgladu.updateModel();
        }
    }
}



class TabelaEdycji extends TabelaSQL {
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    List<Row> dodawaneDane;
    List<Row> daneBazy;
    private boolean onlyNewOption = false;

    TabelaEdycji(){
        super(false);
        daneBazy=new LinkedList<>();
        daneBazy.addAll(BazaDanych.getBazaDanych().getDane());
        System.out.println(daneBazy);
        updateModel();
        PopUpMenuTabeliEdycji popUpMenuTabeli = new PopUpMenuTabeliEdycji(this);
    }

    @Override
    protected void updateData() {
        if(daneBazy==null){
            return;
        }
        if(dodawaneDane==null){
            dodawaneDane=new LinkedList<>();
        }
        Object[][] dane;
        if(onlyNewOption){
            dane = new Object[dodawaneDane.size()][InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn()];
            for(int j=0;j< dodawaneDane.size();j++){
                for(int k=0;k<InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn();k++){
                    dane[j][k]=dodawaneDane.get(j).getPole(k).getWartosc();
                }
            }
        }
        else{
            dane = new Object[dodawaneDane.size()+daneBazy.size()][InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn()];
            for(int j=0;j<daneBazy.size();j++){
                for(int k=0;k<InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn();k++){
                    dane[j][k]=daneBazy.get(j).getPole(k).getWartosc();
                }
            }
            for(int j=0;j< dodawaneDane.size();j++){
                for(int k=0;k<InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn();k++){
                    dane[j+daneBazy.size()][k]=dodawaneDane.get(j).getPole(k).getWartosc();
                }
            }
        }

        super.daneTabeli=dane;
    }

    protected void setOnlyNewOption(boolean b){
        this.onlyNewOption=b;
    }

    public void dodajWiersz(Row wiersz){
        dodawaneDane.add(wiersz);
        super.updateModel();
    }

    public void usunWiersz(int[] numeryWierszy){
        for(int numerWiersza: numeryWierszy){
            if(onlyNewOption){
                dodawaneDane.remove(numerWiersza);
            } else if(numerWiersza>daneBazy.size()){
                dodawaneDane.remove(daneTabeli.length-numerWiersza-1);
            }else{
                daneBazy.remove(numerWiersza);
            }

        }
        super.updateModel();
    }


public List<Row> getData(){
        return dodawaneDane;
}


    class PopUpMenuTabeliEdycji extends JPopupMenu{
        private final PopUpMenuTabeliEdycji thisPopUpMenu = this;
        PopUpMenuTabeliEdycji(TabelaEdycji tabela){

            JMenuItem deleteRows = new JMenuItem("Delete rows");
            deleteRows.addActionListener(e -> {

                int[] selectedRows = tabela.getSelectedRows();

                //Zalozenie zeby to dzialalo to nieistnienie sortowania tej tabeli
                List<Integer> listaDoUsunieciaZBazy = new LinkedList<>();
                List<Integer> listaDoUsunieciaZNowych = new LinkedList<>();
                for(int row: selectedRows){
                    if(row<BazaDanych.getBazaDanych().getDane().size()){
                        listaDoUsunieciaZBazy.add(row);
                    }else{
                        listaDoUsunieciaZNowych.add(row);
                    }
                }
                int[] t = new int[listaDoUsunieciaZNowych.size()];

                for(int i=0;i<listaDoUsunieciaZNowych.size();i++){
                    t[i]=listaDoUsunieciaZNowych.get(i);
                }
                if (t.length!=0) {
                    usunWiersz(t);
                }
                t=new int[listaDoUsunieciaZBazy.size()];
                for(int i=0;i<listaDoUsunieciaZBazy.size();i++){
                    t[i]=listaDoUsunieciaZBazy.get(i);
                }
                if (t.length!=0) {
                    usunWiersz(t);
                    BazaDanych.getBazaDanych().usunDane(t);
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




