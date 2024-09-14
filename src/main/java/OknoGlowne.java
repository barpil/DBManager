import javax.swing.*;

public class OknoGlowne extends JFrame {
    private static OknoGlowne oknoGlowne;
    OknoGlowne(){
        this.setSize(500,500);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        stworzComponenty();
        this.revalidate();

    }

    private void stworzComponenty(){
        PanelElementow panelElementow = PanelElementow.getPanelElementow();
        this.add(panelElementow);
        PanelSterowania panelSterowania = PanelSterowania.getPanelSterowania();
        this.add(panelSterowania);

        Menu menu = Menu.getMenu();
        this.setJMenuBar(menu);
    }

    public static OknoGlowne getOknoGlowne(){
        if(oknoGlowne==null){
            oknoGlowne= new OknoGlowne();
        }
        return  oknoGlowne;
    }
}
