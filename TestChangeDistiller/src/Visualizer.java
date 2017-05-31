import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Visualizer extends JPanel {

    @Override
    public void paintComponent(Graphics g) {
        // Draw Tree Here        
        g.setColor(Color.white);
        g.fillOval(5,5,25,25);
        g.setColor(Color.black);
        g.drawString("12", 10, 22);
        
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.add(new Visualizer());
        jFrame.setSize(500, 500);
        jFrame.setVisible(true);
    }

}