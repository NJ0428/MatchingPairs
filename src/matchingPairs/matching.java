package matchingPairs;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class matching {
	  static JPanel panelNorth; // 패널
	    static JPanel panelCenter; // 패널
	    static JLabel labelMessage;
	    static JButton[] buttons = new JButton[16]; // 카드 수
	    static String[] images = {
	        "fruit01.png", "fruit02.png", "fruit03.png", "fruit04.png",
	        "fruit05.png", "fruit06.png", "fruit07.png", "fruit08.png",
	        "fruit01.png", "fruit02.png", "fruit03.png", "fruit04.png",
	        "fruit05.png", "fruit06.png", "fruit07.png", "fruit08.png",
	    };
	    static int openCount = 0; // 오픈 카운트
	    static int buttonIndexSaver1 = 0; // 카드 인덱스 1
	    static int buttonIndexSaver2 = 0; // 카드 인덱스 2
	    static Timer timer;
	    static int tryCount = 0; // 시도 회수
	    static int successCount = 0; // 성공한 카드 카운트
	
	    static class MyFrame extends JFrame implements ActionListener {
	        public MyFrame(String title) {
	            super(title);
	            this.setLayout(new BorderLayout());
	            this.setSize(400, 500);
	            this.setVisible(true);
	            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	            initUI(this);
	            mixCard(); // 카드 섞기
	            this.pack();
	        }
	
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (openCount == 2) {
	                return;
	            }
	
	            JButton btn = (JButton) e.getSource();
	            int index = getButtonIndex(btn);
	            btn.setIcon(changeImage(images[index]));
	
	            openCount++;
	            if (openCount == 1) { // 첫 번째 카드
	                buttonIndexSaver1 = index;
	            } else if (openCount == 2) { // 두 번째 카드
	                buttonIndexSaver2 = index;
	                tryCount++;
	                labelMessage.setText("카드 맞추기 - 시도 회수: " + tryCount);
	
	                boolean isBingo = checkCard(buttonIndexSaver1, buttonIndexSaver2);
	                if (isBingo) {
	                    openCount = 0;
	                    successCount++;
	                    if (successCount == 8) {
	                        labelMessage.setText("성공! 카드 맞추기 - 시도 회수: " + tryCount);
	                    }
	                } else {
	                    backToQuestion();
	                }
	            }
	        }
	
	        private void backToQuestion() {
	            timer = new Timer(1000, new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    openCount = 0;
	                    buttons[buttonIndexSaver1].setIcon(changeImage("question.png"));
	                    buttons[buttonIndexSaver2].setIcon(changeImage("question.png"));
	                    timer.stop();
	                }
	            });
	            timer.start();
	        }
	
	        private boolean checkCard(int index1, int index2) {
	            if (index1 == index2) {
	                return false;
	            }
	            return images[index1].equals(images[index2]);
	        }
	
	        private int getButtonIndex(JButton btn) {
	            for (int i = 0; i < 16; i++) {
	                if (buttons[i] == btn) {
	                    return i;
	                }
	            }
	            return -1;
	        }
	    }
	
	    static void mixCard() {
	        Random rand = new Random();
	        for (int i = 0; i < 1000; i++) {
	            int random = rand.nextInt(15) + 1;
	            String temp = images[0];
	            images[0] = images[random];
	            images[random] = temp;
	        }
	    }
	
	    static void initUI(MyFrame myFrame) {
	        panelNorth = new JPanel();
	        panelNorth.setPreferredSize(new Dimension(400, 100));
	        panelNorth.setBackground(Color.gray);
	        labelMessage = new JLabel("카드 맞추기!");
	        labelMessage.setPreferredSize(new Dimension(400, 100));
	        labelMessage.setForeground(Color.red);
	        labelMessage.setFont(new Font("Monaco", Font.BOLD, 20));
	        labelMessage.setHorizontalAlignment(JLabel.CENTER);
	        panelNorth.add(labelMessage);
	        myFrame.add("North", panelNorth);
	
	        panelCenter = new JPanel();
	        panelCenter.setLayout(new GridLayout(4, 4));
	        panelCenter.setPreferredSize(new Dimension(400, 400));
	        for (int i = 0; i < 16; i++) {
	            buttons[i] = new JButton();
	            buttons[i].setPreferredSize(new Dimension(100, 100));
	            buttons[i].setIcon(changeImage("question.png"));
	            buttons[i].addActionListener(myFrame);
	            panelCenter.add(buttons[i]);
	        }
	        myFrame.add("Center", panelCenter);
	    }
	
	    static ImageIcon changeImage(String filename) {
	    	System.out.println("./img/" + filename);
	        ImageIcon icon = new ImageIcon("./img/" + filename);
	        Image originImage = icon.getImage();
	        Image changedImage = originImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
	        return new ImageIcon(changedImage);
	    }
	
	    public static void main(String[] args) {
	        new MyFrame("카드 맞추기 게임");
	    }
}
