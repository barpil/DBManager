import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SQLConsole extends JDialog {
    SQLConsole(Frame frame, String nazwaOkna, boolean modal) {
        super(frame, nazwaOkna, modal);
        this.setSize(new Dimension(400, 400));
        this.setResizable(true);

        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty() {
        JPanel panelGlowny = new JPanel();
        panelGlowny.setLayout(new BorderLayout()); // Ustawiamy BorderLayout jako główny układ

        // Panel tekstowy z JTextArea
        JTextArea textArea = new JTextArea(new String("Insert SQL command"));
        textArea.addMouseListener(new MouseAdapter() {
            boolean czyJuzKlikniete = false;
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(!czyJuzKlikniete){
                    textArea.setText("");
                    czyJuzKlikniete=true;
                }
            }
        });
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea); // Dodajemy JScrollPane dla JTextArea
        panelGlowny.add(scrollPane, BorderLayout.CENTER); // Umieszczamy JScrollPane na środku

        // Panel z przyciskami
        JPanel panelPrzyciskow = new JPanel();
        panelPrzyciskow.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Ustawiamy przyciski po prawej stronie

        JButton runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(60, 20));
        runButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        runButton.addActionListener(e -> {
            runCommand(textArea.getText());
            BazaDanych.getBazaDanych().getSqlThreadQueue().rozpocznijWykonywanie();
            //Musze jakos wylapywac jesli wpisane polecenie SQL jest niepoprawne
            this.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60, 20));
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cancelButton.addActionListener(e -> this.dispose());

        panelPrzyciskow.add(runButton);
        panelPrzyciskow.add(cancelButton);

        panelGlowny.add(panelPrzyciskow, BorderLayout.SOUTH); // Umieszczamy panel z przyciskami na dole

        this.add(panelGlowny);
    }

    private void runCommand(String textCommand) {
        BazaDanych.getBazaDanych().customSQLCommand(textCommand);
    }

}
