package matchingPairs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class matching {
	static JPanel panelNorth; // 제목
	static JPanel panelCenter; // 카드
	static JLabel labelMessage;
	static JButton[] buttons = new JButton[16]; // 카드 수
	static String[] images = {
			"card01.png", "card02.png", "card03.png", "card04.png",
			"card05.png", "card06.png", "card07.png", "card08.png",
			"card01.png", "card02.png", "card03.png", "card04.png",
			"card05.png", "card06.png", "card07.png", "card08.png",
	};
	
	static int openCount = 0; // 오픈 카운트
	static int buttonIndexSaver1 = 0; // 카드 인덱스 1
	static int buttonIndexSaver2 = 0; // 카드 인덱스 2
	static Timer timer;
	static int tryCount = 0; // 시도 회수
	static int successCount = 0; // 성공한 카드 카운트
	
	static class MyFrame extends JFrame implements ActionListener {
		public MyFrame() {
			 super("Matching 게임");
			 this.setLayout(new BorderLayout());
		     this.setSize(1280, 720);
		     // 화면의 크기 얻기
	         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	        
	         // 창의 크기 얻기
	         Dimension frameSize = this.getSize();

	         // 창의 중앙이 화면 중앙에 오도록 좌표 계산
	         int x = (int) ((screenSize.getWidth() - frameSize.getWidth()) / 2);
	         int y = (int) ((screenSize.getHeight() - frameSize.getHeight()) / 2);
	        
	         // 위치 설정
	         this.setLocation(x, y);
			 this.setVisible(true);
			 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			 
			 infoUI(this);
			 mixCard();
			 this.pack();
		}
		static void infoUI(MyFrame myFrame) {
			panelNorth = new JPanel();
			panelNorth.setPreferredSize(new Dimension(1280, 100));
			panelNorth.setBackground(new Color(25, 25, 112));
			
			labelMessage = new JLabel("Matching Game");
			labelMessage.setPreferredSize(new Dimension(1280, 90));
			labelMessage.setForeground(Color.white);
			labelMessage.setFont(new Font("DialogInput", Font.BOLD, 25));
			labelMessage.setHorizontalAlignment(JLabel.CENTER);
			
			panelNorth.add(labelMessage);
			myFrame.add("North", panelNorth);
			
			panelCenter = new JPanel();
			panelCenter.setLayout(new GridLayout(4, 4));
			panelCenter.setPreferredSize(new Dimension(1280, 400));
			for (int i = 0; i < 16; i++) {
				buttons[i] = new JButton();
				buttons[i].setPreferredSize(new Dimension(150, 150));
				buttons[i].setIcon(changeImage("cardBack.png"));
				buttons[i].addActionListener(myFrame);
				panelCenter.add(buttons[i]);
			}
			myFrame.add("Center", panelCenter);
			
		}
		static void mixCard() {
		    Random rand = new Random();
		    for (int i = images.length - 1; i > 0; i--) {
		        int random = rand.nextInt(i + 1); // 0부터 i까지 무작위 인덱스 선택
		        // 이미지 교환
		        String temp = images[i];
		        images[i] = images[random];
		        images[random] = temp;
		    }
		}
		static ImageIcon changeImage(String filename) {
			ImageIcon icon = new ImageIcon("./img/" + filename);
			Image originImage = icon.getImage();
			Image changedImage = originImage.getScaledInstance(210, 297, Image.SCALE_SMOOTH);
			return new ImageIcon(changedImage);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//한 번에 두 장 이상의 카드를 열지 않도록 방지
		    if (openCount == 2) return;

		    JButton btn = (JButton) e.getSource();
		    int index = getButtonIndex(btn);
		    
		    if (openCount == 1 && buttonIndexSaver1 == index) {
		        return;  // 같은 카드 두 번 클릭 방지
		    }
		    btn.setIcon(changeImage(images[index]));

		    openCount++;
		    handleCardClick(index);
		}
		private void handleCardClick(int index) {
		    if (openCount == 1) {
		        buttonIndexSaver1 = index;
		    } else if (openCount == 2) {
		        buttonIndexSaver2 = index;
		        tryCount++;
		        labelMessage.setText("카드를 맞추려 시도한 횟수는? " + tryCount + "번!");

		        if (checkCard(buttonIndexSaver1, buttonIndexSaver2)) {
		            successCount++;
		            if (successCount == 8) {
		            	// 리셋 버튼 생성
		                JButton resetButton = new JButton("다시 시작");
		                resetButton.addActionListener(new ActionListener() {
		                    @Override
		                    public void actionPerformed(ActionEvent e) {
		                        resetGame();  // 리셋 버튼을 클릭하면 게임을 초기화
		                    }
		                });
		                
		                // 패널에 리셋 버튼 추가
		                panelNorth.add(resetButton);
		                panelNorth.revalidate();  // 패널을 다시 그려 UI 업데이트
		            }
		            openCount = 0;
		        } else {
		            backToCard();
		        }
		    }
		}
		private void resetGame() {
		    // 게임 상태 초기화
		    openCount = 0;
		    tryCount = 0;
		    successCount = 0;

		    // 메시지 레이블 초기화
		    labelMessage.setText("Matching Game");

		    // 카드 섞기
		    mixCard();

		    // 모든 버튼을 다시 'cardBack.png' 이미지로 설정
		    for (int i = 0; i < buttons.length; i++) {
		        buttons[i].setIcon(changeImage("cardBack.png"));
		    }

		    // 리셋 버튼 제거 (다시 시작할 때는 필요 없음)
		    for (Component comp : panelNorth.getComponents()) {
		        if (comp instanceof JButton) {
		            panelNorth.remove(comp);
		        }
		    }

		    // 패널을 다시 그려서 UI가 업데이트되도록 함
		    panelNorth.revalidate();
		    panelNorth.repaint();
		}
		private boolean checkCard(int index1, int index2) {
			if (index1 == index2) {
				return false;
			}
			return images[index1].equals(images[index2]);
		}
		private void backToCard() {
			timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					openCount = 0;
					buttons[buttonIndexSaver1].setIcon(changeImage("cardBack.png"));
					buttons[buttonIndexSaver2].setIcon(changeImage("cardBack.png"));
					timer.stop();
				}
			});
			timer.start();
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
	public static void main(String[] args) {
		new MyFrame();
	}
	
}
