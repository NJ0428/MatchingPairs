package matchingPairs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.SwingConstants;

public class matching {
	public static void main(String[] args) {
		// EDT(Event Dispatch Thread)에서 GUI를 생성하여 즉시 응답성 확보
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new StartMenuFrame();
			}
		});
	}
}

// 게임 상수들을 관리하는 클래스
class GameConstants {
	public static final int TOTAL_CARDS = 16;
	public static final int GRID_ROWS = 4;
	public static final int GRID_COLS = 4;
	public static final int PAIRS_TO_MATCH = 8;
	public static final int FRAME_WIDTH = 1400;
	public static final int FRAME_HEIGHT = 800;
	public static final int TITLE_HEIGHT = 80;
	public static final int BUTTON_WIDTH = 160;
	public static final int BUTTON_HEIGHT = 160;
	public static final int IMAGE_WIDTH = 140;
	public static final int IMAGE_HEIGHT = 200;
	public static final int CARD_FLIP_DELAY = 800;

	public static final String[] CARD_IMAGES = {
			"card01.png", "card02.png", "card03.png", "card04.png",
			"card05.png", "card06.png", "card07.png", "card08.png"
	};

	public static final String CARD_BACK_IMAGE = "cardBack.png";
	public static final String GAME_ICON = "game_icon.png";
	public static final String IMAGE_PATH = "./img/";

	// 메뉴 관련 상수
	public static final int MENU_BUTTON_WIDTH = 280;
	public static final int MENU_BUTTON_HEIGHT = 70;
	public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 56);
	public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 18);
	public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 22);
	public static final Font INSTRUCTION_FONT = new Font("Segoe UI", Font.PLAIN, 16);
	public static final Font STATUS_FONT = new Font("Segoe UI", Font.BOLD, 20);

	// 모던한 색상 팔레트
	public static final Color PRIMARY_DARK = new Color(15, 23, 42); // 진한 네이비
	public static final Color PRIMARY_BLUE = new Color(59, 130, 246); // 밝은 블루
	public static final Color SECONDARY_BLUE = new Color(37, 99, 235); // 중간 블루
	public static final Color ACCENT_PURPLE = new Color(139, 92, 246); // 보라색 액센트
	public static final Color SUCCESS_GREEN = new Color(34, 197, 94); // 성공 녹색
	public static final Color WARNING_ORANGE = new Color(251, 146, 60); // 경고 오렌지
	public static final Color BACKGROUND_LIGHT = new Color(248, 250, 252); // 밝은 배경
	public static final Color CARD_SHADOW = new Color(0, 0, 0, 30); // 카드 그림자
	public static final Color TEXT_PRIMARY = new Color(15, 23, 42); // 주요 텍스트
	public static final Color TEXT_SECONDARY = new Color(100, 116, 139); // 보조 텍스트

	// 추가 상수들
	public static final Color BACKGROUND_COLOR = new Color(25, 25, 112); // 배경색
	public static final Color BUTTON_COLOR = new Color(70, 130, 180); // 버튼색
}

// 시작 메뉴 프레임
class StartMenuFrame extends JFrame {
	private JPanel mainPanel;
	private MatchingGameFrame gameFrame;
	private InstructionFrame instructionFrame;

	public StartMenuFrame() {
		initializeFrame();
		createMainMenu();
		// UI 컴포넌트 생성 완료 후 화면에 표시
		UIUtils.centerFrameOnScreen(this);
		setVisible(true);
	}

	private void initializeFrame() {
		setTitle("Matching Game - 메인 메뉴");
		// 아이콘 로딩을 별도 스레드에서 수행하여 메인 UI 로딩 차단 방지
		try {
			setIconImage(UIUtils.createScaledImageIcon(GameConstants.GAME_ICON).getImage());
		} catch (Exception e) {
			System.err.println("게임 아이콘 로딩 실패: " + e.getMessage());
		}
		setSize(GameConstants.FRAME_WIDTH, GameConstants.FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
	}

	private void createMainMenu() {
		mainPanel = new JPanel();
		// 그라데이션 배경 효과를 위한 커스텀 페인팅
		mainPanel = new JPanel() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
						java.awt.RenderingHints.VALUE_RENDER_QUALITY);

				// 그라데이션 배경
				java.awt.GradientPaint gradient = new java.awt.GradientPaint(
						0, 0, GameConstants.PRIMARY_DARK,
						0, getHeight(), GameConstants.SECONDARY_BLUE);
				g2d.setPaint(gradient);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		mainPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		// 제목과 부제목 패널
		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

		JLabel titleLabel = new JLabel("🎮 Matching Game", SwingConstants.CENTER);
		titleLabel.setFont(GameConstants.TITLE_FONT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel subtitleLabel = new JLabel("카드를 뒤집어 같은 그림을 찾아보세요!", SwingConstants.CENTER);
		subtitleLabel.setFont(GameConstants.SUBTITLE_FONT);
		subtitleLabel.setForeground(new Color(203, 213, 225));
		subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		titlePanel.add(titleLabel);
		titlePanel.add(Box.createVerticalStrut(10));
		titlePanel.add(subtitleLabel);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(60, 0, 80, 0);
		mainPanel.add(titlePanel, gbc);

		// 버튼들을 위한 패널
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		// 게임 시작 버튼
		JButton startButton = createMenuButton("🚀 게임 시작", GameConstants.SUCCESS_GREEN);
		startButton.addActionListener(e -> startGame());
		buttonPanel.add(startButton);
		buttonPanel.add(Box.createVerticalStrut(20));

		// 게임 방법 버튼
		JButton instructionButton = createMenuButton("📖 게임 방법", GameConstants.PRIMARY_BLUE);
		instructionButton.addActionListener(e -> showInstructions());
		buttonPanel.add(instructionButton);
		buttonPanel.add(Box.createVerticalStrut(20));

		// 게임 종료 버튼
		JButton exitButton = createMenuButton("🚪 게임 종료", GameConstants.WARNING_ORANGE);
		exitButton.addActionListener(e -> exitGame());
		buttonPanel.add(exitButton);

		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 60, 0);
		mainPanel.add(buttonPanel, gbc);

		add(mainPanel, BorderLayout.CENTER);
	}

	private JButton createMenuButton(String text, Color baseColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

				// 둥근 모서리 배경
				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				// 텍스트 그리기
				super.paintComponent(g);
			}
		};

		button.setPreferredSize(new Dimension(GameConstants.MENU_BUTTON_WIDTH, GameConstants.MENU_BUTTON_HEIGHT));
		button.setMaximumSize(new Dimension(GameConstants.MENU_BUTTON_WIDTH, GameConstants.MENU_BUTTON_HEIGHT));
		button.setFont(GameConstants.BUTTON_FONT);
		button.setBackground(baseColor);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 그림자 효과를 위한 보더
		button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createEmptyBorder(3, 3, 6, 6),
				javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20)));

		// 향상된 호버 효과
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			private Color originalColor = baseColor;

			public void mouseEntered(java.awt.event.MouseEvent evt) {
				// 색상을 약간 밝게
				button.setBackground(brightenColor(originalColor, 0.2f));
				button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(originalColor);
				button.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			}

			public void mousePressed(java.awt.event.MouseEvent evt) {
				// 클릭 시 색상을 어둡게
				button.setBackground(darkenColor(originalColor, 0.2f));
			}

			public void mouseReleased(java.awt.event.MouseEvent evt) {
				button.setBackground(brightenColor(originalColor, 0.2f));
			}
		});

		return button;
	}

	private Color brightenColor(Color color, float factor) {
		int r = Math.min(255, (int) (color.getRed() * (1 + factor)));
		int g = Math.min(255, (int) (color.getGreen() * (1 + factor)));
		int b = Math.min(255, (int) (color.getBlue() * (1 + factor)));
		return new Color(r, g, b);
	}

	private Color darkenColor(Color color, float factor) {
		int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
		int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
		int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
		return new Color(r, g, b);
	}

	private void startGame() {
		if (gameFrame != null) {
			gameFrame.dispose();
		}
		gameFrame = new MatchingGameFrame(this);
		setVisible(false);
	}

	private void showInstructions() {
		if (instructionFrame != null) {
			instructionFrame.dispose();
		}
		instructionFrame = new InstructionFrame(this);
	}

	private void exitGame() {
		System.exit(0);
	}

	public void showMenu() {
		setVisible(true);
	}
}

// 게임 방법 설명 프레임
class InstructionFrame extends JFrame {
	public InstructionFrame(StartMenuFrame parent) {
		initializeFrame();
		createInstructionPanel();
		// UI 컴포넌트 생성 완료 후 화면에 표시
		UIUtils.centerFrameOnScreen(this);
		setVisible(true);
	}

	private void initializeFrame() {
		setTitle("게임 방법");
		try {
			setIconImage(UIUtils.createScaledImageIcon(GameConstants.GAME_ICON).getImage());
		} catch (Exception e) {
			System.err.println("게임 아이콘 로딩 실패: " + e.getMessage());
		}
		setSize(600, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
	}

	private void createInstructionPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setLayout(new BorderLayout());

		// 제목
		JLabel titleLabel = new JLabel("게임 방법", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Sans-Serif", Font.BOLD, 24));
		titleLabel.setForeground(GameConstants.BACKGROUND_COLOR);
		titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		// 게임 방법 설명
		String instructions = """
				🎯 게임 목표
				- 4x4 격자에 배치된 16장의 카드 중 같은 그림의 카드 8쌍을 모두 찾아 맞추는 게임입니다.

				🎮 게임 방법
				1. 카드를 클릭하면 카드가 뒤집혀 그림이 나타납니다.
				2. 두 장의 카드를 선택할 수 있습니다.
				3. 두 카드의 그림이 같으면 성공! 카드가 그대로 남아있습니다.
				4. 두 카드의 그림이 다르면 실패! 1초 후 카드가 다시 뒤집힙니다.
				5. 모든 카드 쌍을 맞추면 게임이 완료됩니다.

				🏆 게임 팁
				- 카드의 위치를 기억하세요!
				- 처음 몇 장은 여러 카드를 뒤집어 위치를 파악하는 것이 좋습니다.
				- 시도 횟수가 화면 상단에 표시됩니다.

				🔄 다시 시작
				- 게임 완료 후 '다시 시작' 버튼을 클릭하면 새로운 게임을 시작할 수 있습니다.
				- 카드 배치가 무작위로 섞입니다.
				""";

		JTextArea instructionArea = new JTextArea(instructions);
		instructionArea.setFont(GameConstants.INSTRUCTION_FONT);
		instructionArea.setEditable(false);
		instructionArea.setBackground(Color.WHITE);
		instructionArea.setLineWrap(true);
		instructionArea.setWrapStyleWord(true);
		instructionArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JScrollPane scrollPane = new JScrollPane(instructionArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// 닫기 버튼
		JButton closeButton = new JButton("닫기");
		closeButton.setFont(GameConstants.BUTTON_FONT);
		closeButton.setBackground(GameConstants.BUTTON_COLOR);
		closeButton.setForeground(Color.WHITE);
		closeButton.setFocusPainted(false);
		closeButton.setOpaque(true); // 버튼을 불투명하게 설정
		closeButton.setContentAreaFilled(true); // 버튼 내용 영역 채우기 활성화
		closeButton.addActionListener(e -> dispose());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(closeButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel);
	}
}

// 게임 상태를 관리하는 클래스
class GameState {
	private int openCount = 0;
	private int firstCardIndex = -1;
	private int secondCardIndex = -1;
	private int tryCount = 0;
	private int successCount = 0;

	public void reset() {
		openCount = 0;
		firstCardIndex = -1;
		secondCardIndex = -1;
		tryCount = 0;
		successCount = 0;
	}

	public boolean canOpenCard() {
		return openCount < 2;
	}

	public void openCard(int index) {
		if (openCount == 0) {
			firstCardIndex = index;
		} else if (openCount == 1) {
			secondCardIndex = index;
		}
		openCount++;
	}

	public boolean isSameCardClicked(int index) {
		return openCount == 1 && firstCardIndex == index;
	}

	public void incrementTryCount() {
		tryCount++;
	}

	public void incrementSuccessCount() {
		successCount++;
	}

	public void resetOpenCount() {
		openCount = 0;
	}

	// Getters
	public int getOpenCount() {
		return openCount;
	}

	public int getFirstCardIndex() {
		return firstCardIndex;
	}

	public int getSecondCardIndex() {
		return secondCardIndex;
	}

	public int getTryCount() {
		return tryCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public boolean isGameComplete() {
		return successCount == GameConstants.PAIRS_TO_MATCH;
	}
}

// 카드 관리 클래스
class CardManager {
	private String[] cardImages;

	public CardManager() {
		initializeCards();
		shuffleCards();
	}

	private void initializeCards() {
		cardImages = new String[GameConstants.TOTAL_CARDS];
		for (int i = 0; i < GameConstants.CARD_IMAGES.length; i++) {
			cardImages[i] = GameConstants.CARD_IMAGES[i];
			cardImages[i + GameConstants.CARD_IMAGES.length] = GameConstants.CARD_IMAGES[i];
		}
	}

	public void shuffleCards() {
		List<String> cardList = Arrays.asList(cardImages);
		Collections.shuffle(cardList);
		cardImages = cardList.toArray(new String[0]);
	}

	public String getCardImage(int index) {
		return cardImages[index];
	}

	public boolean areCardsMatching(int index1, int index2) {
		if (index1 == index2)
			return false;
		return cardImages[index1].equals(cardImages[index2]);
	}
}

// UI 유틸리티 클래스
class UIUtils {
	public static ImageIcon createScaledImageIcon(String filename) {
		try {
			ImageIcon icon = new ImageIcon(GameConstants.IMAGE_PATH + filename);

			// 이미지가 제대로 로드되었는지 확인
			if (icon.getIconWidth() == -1) {
				System.err.println("이미지를 찾을 수 없습니다: " + GameConstants.IMAGE_PATH + filename);
				// 기본 이미지가 없을 경우 빈 아이콘 반환
				return new ImageIcon();
			}

			Image originalImage = icon.getImage();
			// SCALE_FAST를 사용하여 더 빠른 스케일링 (품질은 약간 떨어지지만 속도 향상)
			Image scaledImage = originalImage.getScaledInstance(
					GameConstants.IMAGE_WIDTH,
					GameConstants.IMAGE_HEIGHT,
					Image.SCALE_FAST);
			return new ImageIcon(scaledImage);
		} catch (Exception e) {
			System.err.println("이미지 로딩 중 오류 발생: " + filename + " - " + e.getMessage());
			return new ImageIcon();
		}
	}

	public static void centerFrameOnScreen(JFrame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		int x = (int) ((screenSize.getWidth() - frameSize.getWidth()) / 2);
		int y = (int) ((screenSize.getHeight() - frameSize.getHeight()) / 2);
		frame.setLocation(x, y);
	}
}

// 메인 게임 프레임
class MatchingGameFrame extends JFrame implements ActionListener {
	private JPanel titlePanel;
	private JPanel cardPanel;
	private JLabel statusLabel;
	private JButton[] cardButtons;
	private Timer flipBackTimer;
	private StartMenuFrame parentFrame;

	private GameState gameState;
	private CardManager cardManager;

	public MatchingGameFrame(StartMenuFrame parent) {
		this.parentFrame = parent;
		gameState = new GameState();
		cardManager = new CardManager();
		initializeFrame();
		createUI();
		// UI 컴포넌트 생성 완료 후 화면에 표시
		UIUtils.centerFrameOnScreen(this);
		setVisible(true);
	}

	private void initializeFrame() {
		setTitle("Matching 게임");
		try {
			setIconImage(UIUtils.createScaledImageIcon(GameConstants.GAME_ICON).getImage());
		} catch (Exception e) {
			System.err.println("게임 아이콘 로딩 실패: " + e.getMessage());
		}
		setLayout(new BorderLayout());
		setSize(GameConstants.FRAME_WIDTH, GameConstants.FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// 창 닫기 이벤트 처리
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				parentFrame.showMenu();
				dispose();
			}
		});
	}

	private void createUI() {
		createTitlePanel();
		createCardPanel();
		pack();
	}

	private void createTitlePanel() {
		titlePanel = new JPanel();
		titlePanel.setPreferredSize(new Dimension(GameConstants.FRAME_WIDTH, GameConstants.TITLE_HEIGHT));
		titlePanel.setBackground(new Color(25, 25, 112));

		statusLabel = new JLabel("Matching Game");
		statusLabel.setPreferredSize(new Dimension(GameConstants.FRAME_WIDTH, 30));
		statusLabel.setForeground(Color.WHITE);
		statusLabel.setFont(new Font("Sans-Serif", Font.BOLD, 25));
		statusLabel.setHorizontalAlignment(JLabel.CENTER);

		// 메뉴로 돌아가기 버튼
		JButton backButton = new JButton("메뉴로");
		backButton.setFont(new Font("Sans-Serif", Font.BOLD, 14));
		backButton.setBackground(GameConstants.BUTTON_COLOR);
		backButton.setForeground(Color.WHITE);
		backButton.setFocusPainted(false);
		backButton.setOpaque(true); // 버튼을 불투명하게 설정
		backButton.setContentAreaFilled(true); // 버튼 내용 영역 채우기 활성화
		backButton.addActionListener(e -> {
			parentFrame.showMenu();
			dispose();
		});

		titlePanel.add(backButton);
		titlePanel.add(statusLabel);
		add(titlePanel, BorderLayout.NORTH);
	}

	private void createCardPanel() {
		cardPanel = new JPanel();
		cardPanel.setLayout(new GridLayout(GameConstants.GRID_ROWS, GameConstants.GRID_COLS));
		cardPanel.setPreferredSize(new Dimension(GameConstants.FRAME_WIDTH, GameConstants.FRAME_HEIGHT));

		cardButtons = new JButton[GameConstants.TOTAL_CARDS];

		for (int i = 0; i < GameConstants.TOTAL_CARDS; i++) {
			cardButtons[i] = createCardButton();
			cardPanel.add(cardButtons[i]);
		}

		add(cardPanel, BorderLayout.CENTER);
	}

	private JButton createCardButton() {
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT));
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setIcon(UIUtils.createScaledImageIcon(GameConstants.CARD_BACK_IMAGE));
		button.addActionListener(this);
		button.setBorder(new javax.swing.border.LineBorder(Color.LIGHT_GRAY, 2, true));
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!gameState.canOpenCard())
			return;

		JButton clickedButton = (JButton) e.getSource();
		int cardIndex = getButtonIndex(clickedButton);

		if (gameState.isSameCardClicked(cardIndex))
			return;

		handleCardClick(clickedButton, cardIndex);
	}

	private void handleCardClick(JButton button, int cardIndex) {
		button.setIcon(UIUtils.createScaledImageIcon(cardManager.getCardImage(cardIndex)));
		gameState.openCard(cardIndex);

		if (gameState.getOpenCount() == 1) {
			// 첫 번째 카드 선택
			return;
		} else if (gameState.getOpenCount() == 2) {
			// 두 번째 카드 선택
			gameState.incrementTryCount();
			updateStatusMessage("카드를 맞추려 시도한 횟수는? " + gameState.getTryCount() + "번!");

			if (cardManager.areCardsMatching(gameState.getFirstCardIndex(), gameState.getSecondCardIndex())) {
				handleMatchSuccess();
			} else {
				handleMatchFailure();
			}
		}
	}

	private void handleMatchSuccess() {
		gameState.incrementSuccessCount();

		// 성공한 카드들 비활성화
		cardButtons[gameState.getFirstCardIndex()].setEnabled(false);
		cardButtons[gameState.getSecondCardIndex()].setEnabled(false);

		gameState.resetOpenCount();

		if (gameState.isGameComplete()) {
			updateStatusMessage("🎉 축하합니다! 모든 카드를 맞췄습니다! (총 " + gameState.getTryCount() + "번 시도)");
			addResetButton();
		}
	}

	private void handleMatchFailure() {
		flipBackTimer = new Timer(GameConstants.CARD_FLIP_DELAY, e -> {
			cardButtons[gameState.getFirstCardIndex()]
					.setIcon(UIUtils.createScaledImageIcon(GameConstants.CARD_BACK_IMAGE));
			cardButtons[gameState.getSecondCardIndex()]
					.setIcon(UIUtils.createScaledImageIcon(GameConstants.CARD_BACK_IMAGE));
			gameState.resetOpenCount();
			flipBackTimer.stop();
		});
		flipBackTimer.start();
	}

	private void updateStatusMessage(String message) {
		statusLabel.setText(message);
		statusLabel.revalidate();
		statusLabel.repaint();
	}

	private void addResetButton() {
		if (isResetButtonExists())
			return;

		JButton resetButton = new JButton("다시 시작");
		resetButton.setFont(new Font("Sans-Serif", Font.BOLD, 14));
		resetButton.setBackground(GameConstants.BUTTON_COLOR);
		resetButton.setForeground(Color.WHITE);
		resetButton.setFocusPainted(false);
		resetButton.setOpaque(true); // 버튼을 불투명하게 설정
		resetButton.setContentAreaFilled(true); // 버튼 내용 영역 채우기 활성화
		resetButton.addActionListener(e -> resetGame());
		titlePanel.add(resetButton);
		titlePanel.revalidate();
		titlePanel.repaint();
	}

	private boolean isResetButtonExists() {
		return Arrays.stream(titlePanel.getComponents())
				.filter(comp -> comp instanceof JButton)
				.map(comp -> (JButton) comp)
				.anyMatch(btn -> "다시 시작".equals(btn.getText()));
	}

	private void resetGame() {
		gameState.reset();
		cardManager.shuffleCards();

		updateStatusMessage("Matching Game");

		// 모든 카드 버튼 초기화
		for (JButton button : cardButtons) {
			button.setEnabled(true);
			button.setIcon(UIUtils.createScaledImageIcon(GameConstants.CARD_BACK_IMAGE));
		}

		// 리셋 버튼 제거
		Component[] components = titlePanel.getComponents();
		for (Component comp : components) {
			if (comp instanceof JButton && "다시 시작".equals(((JButton) comp).getText())) {
				titlePanel.remove(comp);
			}
		}

		titlePanel.revalidate();
		titlePanel.repaint();
	}

	private int getButtonIndex(JButton button) {
		for (int i = 0; i < cardButtons.length; i++) {
			if (cardButtons[i] == button) {
				return i;
			}
		}
		return -1;
	}
}
