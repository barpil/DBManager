import javax.swing.*;
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

        JScrollPane dolnaScrollPane = new JScrollPane();
        TabelaEdycji tabelaEdycji = new TabelaEdycji();
        dolnaScrollPane.setViewportView(tabelaEdycji);
        dolnaScrollPane.setPreferredSize(new Dimension(400, 50));

        panelDodawaniaWierszy.add(dolnaScrollPane);


        JPanel panelPrzyciskow = new JPanel(new BoxLayout(panelDodawaniaWierszy, BoxLayout.Y_AXIS));
        Button dodajWierszBtn = new Button("+");
        dodajWierszBtn.addActionListener(e -> {
            dodajWiersz();
        });
        panelPrzyciskow.add(dodajWierszBtn);

        Button wyczyscWierszBtn = new Button("Clear");
        wyczyscWierszBtn.addActionListener(e -> {
            wyczyscWiersz();
        });


        panelDodawaniaWierszy.add(panelPrzyciskow);

        panelGlowny.add(panelDodawaniaWierszy);

        this.add(panelGlowny);
    }

    private void dodajWiersz() {
        // Implementacja dodawania wiersza
    }

    private void wyczyscWiersz() {

    }


}

