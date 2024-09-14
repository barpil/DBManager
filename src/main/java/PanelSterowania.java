import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class PanelSterowania extends JPanel {
    private static PanelSterowania panelSterowania;
    private JPanel panelProgressBara;
    private ThreadProgressBar progressBar;
    private Button przyciskRozpoczecia;

    private PanelSterowania() {
        this.setPreferredSize(new Dimension(500, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // Użycie BoxLayout.Y_AXIS dla układu pionowego
        dodajElementy();


    }

    private void dodajElementy() {

        panelProgressBara = new JPanel();
        panelProgressBara.setLayout(new BoxLayout(panelProgressBara, BoxLayout.X_AXIS));
        panelProgressBara.add(Box.createHorizontalGlue());
        progressBar = new ThreadProgressBar();
        panelProgressBara.add(progressBar);
        this.add(panelProgressBara);




    }

    public ThreadProgressBar getProgressBar() {
        return progressBar;
    }



    public static PanelSterowania getPanelSterowania() {
        if(panelSterowania==null){
            panelSterowania= new PanelSterowania();
        }
        return panelSterowania;
    }



    public JPanel getPanelProgressBara() {
        return panelProgressBara;
    }
}
