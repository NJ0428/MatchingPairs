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
	static JPanel titleTop; // 제목
	static JPanel cardImg; // 카드
	static JLabel labelInfo;
    static final int TOTAL_CARDS = 16;
    static final int GRID_ROWS = 4;
    static final int GRID_COLS = 4;
    static int openCount = 0; // 오픈 카운트
    static int buttonIndexSaver1 = 0; // 카드 인덱스 1
    static int buttonIndexSaver2 = 0; // 카드 인덱스 2
    static int tryCount = 0; // 시도 회수
    static int successCount = 0; // 성공한 카드 카운트
    static Timer timer;
    
	static JButton[] buttons = new JButton[TOTAL_CARDS]; // 카드 수
    // 카드 이미지
    static String[] images2 = {
        "card01.png", "card02.png", "card03.png", "card04.png",
        "card05.png", "card06.png", "card07.png", "card08.png"
    };
    
    static String[] images = new String[TOTAL_CARDS];
    static {
        for (int i = 0; i < images2.length; i++) {
        	images[i] = images2[i];
        	images[i + images2.length] = images2[i];
        }
    }

	static class MyFrame extends JFrame implements ActionListener {
		public MyFrame() {
			super("Matching 게임");
			ImageIcon icon = new ImageIcon("./img/game_icon.png"); // 아이콘 이미지 로드
			this.setIconImage(icon.getImage()); // 창의 아이콘 설정
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
			
			titleUI(this);
			infoUI(this);
			mixCard();
			this.pack();
		}
		static void titleUI(MyFrame myFrame) {
			titleTop = new JPanel();
			titleTop.setPreferredSize(new Dimension(1280, 40));
			titleTop.setBackground(new Color(25, 25, 112));

			labelInfo = new JLabel("Matching Game");
			labelInfo.setPreferredSize(new Dimension(1280, 30));
			labelInfo.setForeground(Color.white);
			labelInfo.setFont(new Font("Sans-Serif", Font.BOLD, 25));
			labelInfo.setHorizontalAlignment(JLabel.CENTER);

			titleTop.add(labelInfo);
			myFrame.add("North", titleTop);
		}
		static void infoUI(MyFrame myFrame) {
			cardImg = new JPanel();
			cardImg.setLayout(new GridLayout(GRID_ROWS, GRID_COLS));
			cardImg.setPreferredSize(new Dimension(1280, 720));
			for (int i = 0; i < 16; i++) {
				buttons[i] = new JButton();
				buttons[i].setPreferredSize(new Dimension(150, 150)); // 버튼 크기 설정

				// 배경색 설정 (필요에 따라 수정)
				labelInfo.setForeground(new Color(255, 211, 105)); // 밝은 금색 

				// 테두리 제거
				buttons[i].setBorderPainted(false); // 테두리 그리지 않음
				buttons[i].setFocusPainted(false); // 클릭 후 포커스 테두리 제거
				buttons[i].setContentAreaFilled(false); // 버튼 내용 영역 채우기 제거 (이미지가 더 깔끔해 보이도록)

				// 카드 뒷면 이미지 설정
				buttons[i].setIcon(changeImage("cardBack.png"));

				// 버튼 클릭 이벤트 추가
				buttons[i].addActionListener(myFrame);

				// 패널에 버튼 추가
				cardImg.add(buttons[i]);

				// 둥근 테두리 추가
				buttons[i].setBorder(new javax.swing.border.LineBorder(Color.LIGHT_GRAY, 2, true)); 

			}
			myFrame.add("Center", cardImg);

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
			Image changedImage = originImage.getScaledInstance(168, 238, Image.SCALE_SMOOTH);
			return new ImageIcon(changedImage);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// 한 번에 두 장 이상의 카드를 열지 않도록 방지
			if (openCount == 2)
				return;

			JButton btn = (JButton) e.getSource();
			int index = getButtonIndex(btn);

			if (openCount == 1 && buttonIndexSaver1 == index) {
				return; // 같은 카드 두 번 클릭 방지
			}
			btn.setIcon(changeImage(images[index]));

			openCount++;
			handleCardClick(index);
		}
		private void handleCardClick(int index) {
		    if (openCount == 1) {
		        buttonIndexSaver1 = index;  // 첫 번째 카드 저장
		    } else if (openCount == 2) {
		        buttonIndexSaver2 = index;  // 두 번째 카드 저장
		        tryCount++;  // 시도 횟수 증가
		        updateStatusMessage("카드를 맞추려 시도한 횟수는? " + tryCount + "번!");

		        // 카드가 일치하는지 체크
		        if (checkCard(buttonIndexSaver1, buttonIndexSaver2)) {
		            handleMatchSuccess();  // 카드 매칭 성공 처리
		        } else {
		            backToCard();  // 카드 뒤집기 처리
		        }
		    }
		}

		private void handleMatchSuccess() {
		    successCount++;
		    tryCount--;  // 성공했으므로 시도 횟수 감소
		    updateStatusMessage("카드를 맞추려 시도한 횟수는? " + tryCount + "번!");

		    // 성공한 카드 비활성화
		    buttons[buttonIndexSaver1].setEnabled(false);
		    buttons[buttonIndexSaver2].setEnabled(false);

		    // 모든 카드가 맞춰졌을 경우
		    if (successCount == 8) {
		        updateStatusMessage("성공! 모든 카드를 맞췄습니다.");
		        addResetButtonIfNeeded();
		    }

		    
		    openCount = 0;  // 오픈된 카드 카운트 초기화
		}

		// 상태 메시지 업데이트 메서드
		private void updateStatusMessage(String message) {
		    labelInfo.setText(message);
		    labelInfo.revalidate();
		    labelInfo.repaint();
		}

		// 리셋 버튼이 없을 경우 추가
		private void addResetButtonIfNeeded() {
		    if (!isResetButtonAdded()) {
		        JButton resetButton = new JButton("다시 시작");
		        resetButton.addActionListener(e -> resetGame());
		        titleTop.add(resetButton);
		        titleTop.revalidate();
		        titleTop.repaint();
		    }
		}


		// 리셋 버튼이 이미 추가되었는지 확인하는 메서드
		private boolean isResetButtonAdded() {
			for (Component comp : titleTop.getComponents()) {
				if (comp instanceof JButton && ((JButton) comp).getText().equals("다시 시작")) {
					return true; // 리셋 버튼이 이미 추가됨
				}
			}
			return false;
		}

		private void resetGame() {
			// 게임 상태 초기화
			openCount = 0;
			tryCount = 0;
			successCount = 0;

			// 메시지 레이블 초기화
			labelInfo.setText("Matching Game");

			// 카드 섞기
			mixCard();

			// 모든 버튼을 다시 'cardBack.png' 이미지로 설정
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setEnabled(true);  // 모든 버튼 다시 활성화
				buttons[i].setIcon(changeImage("cardBack.png"));
			}

			// 리셋 버튼 제거 (다시 시작할 때는 필요 없음)
			for (Component comp : titleTop.getComponents()) {
				if (comp instanceof JButton) {
					titleTop.remove(comp);
				}
			}

			// 패널을 다시 그려서 UI가 업데이트되도록 함
			titleTop.revalidate();
			titleTop.repaint();
		}

		private boolean checkCard(int index1, int index2) {
			if (index1 == index2)  return false;
			
			return images[index1].equals(images[index2]);
		}

		private void backToCard() {
			timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttons[buttonIndexSaver1].setIcon(changeImage("cardBack.png"));
					buttons[buttonIndexSaver2].setIcon(changeImage("cardBack.png"));
					openCount = 0; 
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
