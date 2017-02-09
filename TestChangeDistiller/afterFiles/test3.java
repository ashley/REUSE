import javax.swing.*;
import java.awt.*;
import java.awt.event.*; 

public class ClassQuiz extends JFrame implements ActionListener{

   private final int WINDOW_WIDTH = 200;  // Window width
   private final int WINDOW_HEIGHT = 200; // Window height
   private TextArea word; 
   private JButton reverse, clear; 
   private String history = "";
   
   public ClassQuiz(){
      setTitle("Monday quiz");
      setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new BorderLayout());
         
      Panel pTop = new Panel();
      
      word = new TextArea("Enter text here",1,20,TextArea.SCROLLBARS_NONE);
      pTop.add(word); 
      
      /**** when Panel is finished, add to the Frame, then add a label ******/
      add(pTop,BorderLayout.WEST); 
      
      Panel pBot = new Panel();
      pBot.setLayout(new GridLayout(1,2)); 
      
      reverse = new JButton("Reverse me");
      clear = new JButton("Clear me");

	 pBot.add(reverse);
	pBot.add(clear);
      add(pBot,BorderLayout.SOUTH); 
      
      reverse.addActionListener(this); 
      clear.addActionListener(this);
      
      setVisible(true);
   }
   


   public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == reverse) {
           word.setText(result);   
         } 
  }
  else if (src == clear) {
  history = word.getText();
         if(!history.isEmpty()){
            StringBuffer a = new StringBuffer(history);
            String result = "";
             word.setText(result);   
         } 

  }
}




}//class
