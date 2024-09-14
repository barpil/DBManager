import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SQLConsole extends JDialog {
    private TabelaWyswietlaniaWynikow tabelaWyswietlaniaWynikow;
    SQLConsole(Frame frame, String nazwaOkna, boolean modal) {
        super(frame, nazwaOkna, modal);
        this.setSize(new Dimension(400, 400));
        this.setResizable(true);

        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty() {
        JPanel panelOkna = new JPanel();
        panelOkna.setLayout(new BoxLayout(panelOkna, BoxLayout.Y_AXIS));
        JPanel panelSterowania = new JPanel();
        panelSterowania.setLayout(new BoxLayout(panelSterowania, BoxLayout.X_AXIS));

        JPanel panelElementow = new JPanel();
        panelElementow.setLayout(new BoxLayout(panelElementow, BoxLayout.X_AXIS));

        JPanel panelWyswietlaniaWynikow = new JPanel();
        panelWyswietlaniaWynikow.setPreferredSize(new Dimension(0, 0));

        JScrollPane scrollPaneTabeli = new JScrollPane();

        // Konfiguracja tabeli
        tabelaWyswietlaniaWynikow = new TabelaWyswietlaniaWynikow(false);
        tabelaWyswietlaniaWynikow.setVisible(false);
        tabelaWyswietlaniaWynikow.setPreferredScrollableViewportSize(new Dimension(400, 400));
        tabelaWyswietlaniaWynikow.setFillsViewportHeight(true);  // Wypełnia przestrzeń
        tabelaWyswietlaniaWynikow.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);  // Dopasowuje kolumny

        scrollPaneTabeli.setViewportView(tabelaWyswietlaniaWynikow);
        scrollPaneTabeli.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);  // Przewijanie pionowe
        scrollPaneTabeli.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  // Przewijanie poziome

        panelWyswietlaniaWynikow.add(scrollPaneTabeli);

        JLabel showQueryResultLabel = new JLabel("Show result");
        JCheckBox showQueryResultCB = new JCheckBox();

        // Obsługa checkboxa
        ItemListener myItemListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                panelWyswietlaniaWynikow.setPreferredSize(new Dimension(400, 400));
                tabelaWyswietlaniaWynikow.setVisible(true);
                tabelaWyswietlaniaWynikow.updateModel();
                tabelaWyswietlaniaWynikow.revalidate();
                tabelaWyswietlaniaWynikow.repaint();
                this.pack();  // Dostosowanie rozmiaru okna
            } else {
                panelWyswietlaniaWynikow.setPreferredSize(new Dimension(0, 0));
                tabelaWyswietlaniaWynikow.setVisible(false);
                this.revalidate();
                tabelaWyswietlaniaWynikow.repaint();
                this.pack();  // Dostosowanie okna po ukryciu tabeli
            }
        };
        showQueryResultCB.addItemListener(myItemListener);

        panelSterowania.add(Box.createHorizontalGlue());
        panelSterowania.add(showQueryResultLabel);
        panelSterowania.add(showQueryResultCB);

        JPanel panelPolecen = new JPanel();
        panelPolecen.setLayout(new BorderLayout());
        panelPolecen.setPreferredSize(new Dimension(400, 350));

        // Panel tekstowy z JTextArea
        JTextArea textArea = new JTextArea("Insert SQL command");
        textArea.addMouseListener(new MouseAdapter() {
            boolean czyJuzKlikniete = false;

            @Override
            public void mousePressed(MouseEvent e) {
                if (!czyJuzKlikniete) {
                    textArea.setText("");
                    czyJuzKlikniete = true;
                }
            }
        });
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panelPolecen.add(scrollPane, BorderLayout.CENTER);

        JPanel panelPrzyciskow = new JPanel();
        panelPrzyciskow.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(60, 20));
        runButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        runButton.addActionListener(e -> {
            runCommand(textArea.getText());
            BazaDanych.getBazaDanych().getSqlThreadQueue().rozpocznijWykonywanie();
        });

        InputMap inputMap = runButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = runButton.getActionMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control R");
        inputMap.put(keyStroke, "runButton");

        actionMap.put("runButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runButton.doClick();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60, 20));
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cancelButton.addActionListener(e -> this.dispose());

        panelPrzyciskow.add(runButton);
        panelPrzyciskow.add(cancelButton);

        panelPolecen.add(panelPrzyciskow, BorderLayout.SOUTH);

        panelElementow.add(panelPolecen);

        panelWyswietlaniaWynikow.setBackground(Color.BLACK);
        panelElementow.add(panelWyswietlaniaWynikow);

        panelOkna.add(panelSterowania);
        panelOkna.add(panelElementow);

        this.add(panelOkna);
    }

    private void runCommand(String textCommand) {
        BazaDanych.getBazaDanych().customSQLCommand(this, textCommand);
    }

    public static void poinformujOBledzie(SQLException e){
        String wiadomoscKomunikatu = "A SQL error occured.\nPlease check the syntax and correctness of the command <br> Error message:<br><font color='red'>"+e.getMessage()+"</font>";
        JLabel komunikat = new JLabel("<html><body style='width: 300px'>" + wiadomoscKomunikatu + "</body></html>");
        JOptionPane.showMessageDialog(OknoGlowne.getOknoGlowne(), komunikat, "SQL error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isQuery(){
        return tabelaWyswietlaniaWynikow.isVisible();
    }

    public void passResult(ResultSet resultSet){
        tabelaWyswietlaniaWynikow.resultSet=resultSet;

        tabelaWyswietlaniaWynikow.updateModel();
    }

}

class TabelaWyswietlaniaWynikow extends TabelaSQL{
    DefaultTableModel model;
    ResultSet resultSet=null;
    TabelaWyswietlaniaWynikow(boolean isCellEditable) {
        super(isCellEditable);
    }


    @Override
    protected void updateColumnNames(){
        if (resultSet!=null&& isVisible()) {
            int liczbaKolumn = 0;
            try {
                liczbaKolumn = resultSet.getMetaData().getColumnCount();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String nazwyKolumn[] = new String[liczbaKolumn];

            try {
                for(int i=0; i<resultSet.getMetaData().getColumnCount();i++){
                    nazwyKolumn[i]= resultSet.getMetaData().getColumnName(i+1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            nazwyKolumnTabeli = nazwyKolumn;
        }
    }

    @Override
    protected void updateData() {
        if(isVisible()&&resultSet!=null){
            daneTabeli= przygotujDaneTabeli();
        }
    }

    private Object[][] przygotujDaneTabeli() {
        Object[][] wynik=new Object[0][0];
        try {
            List<Row> listaDanych = new LinkedList<>();
            Row dodawanyRzad;
            int liczbaKolumn = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                dodawanyRzad = new Row(liczbaKolumn);

//Musze naprawic scrollPane bo nie pokazuje calej tabeli
                for (int i = 0; i < liczbaKolumn; i++) {
                    String nazwaKolumny = resultSet.getMetaData().getColumnName(i+1);
                    switch (resultSet.getMetaData().getColumnTypeName(i+1).toLowerCase()) {
                        case "int":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getInt(nazwaKolumny));
                            break;
                        case "varchar":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getString(nazwaKolumny));
                            break;
                        default:
                            dodawanyRzad.addPole(nazwaKolumny + " (type error)", null);
                            System.out.println(resultSet.getMetaData().getColumnTypeName(i+1));
                            break; //Mozna dodac informacje o bledzie
                    }

                }
                listaDanych.add(dodawanyRzad);


            }

            wynik= new Object[listaDanych.size()][resultSet.getMetaData().getColumnCount()];
            for(int j=0;j< listaDanych.size();j++){
                for(int k=0;k<liczbaKolumn;k++){
                    wynik[j][k] = listaDanych.get(j).getPole(k).getWartosc();
                    System.out.print(listaDanych.get(j).getPole(k).getWartosc()+" ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resultSet=null;
        return wynik;
    }

}
