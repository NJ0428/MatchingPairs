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
import java.awt.event.KeyEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
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

// 게임 난이도 열거형
enum GameDifficulty {
	EASY(8, 2, 4, 4, "이지 모드"),
	NORMAL(16, 4, 4, 8, "노멀 모드"),
	HARD(24, 4, 6, 12, "하드 모드");

	public final int totalCards;
	public final int gridRows;
	public final int gridCols;
	public final int pairsToMatch;
	public final String displayName;

	GameDifficulty(int totalCards, int gridRows, int gridCols, int pairsToMatch, String displayName) {
		this.totalCards = totalCards;
		this.gridRows = gridRows;
		this.gridCols = gridCols;
		this.pairsToMatch = pairsToMatch;
		this.displayName = displayName;
	}
}

// 게임 상수들을 관리하는 클래스
class GameConstants {
	// 기본값 (노멀 모드)
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
	public static final Font TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 56);
	public static final Font SUBTITLE_FONT = new Font("맑은 고딕", Font.PLAIN, 18);
	public static final Font BUTTON_FONT = new Font("맑은 고딕", Font.BOLD, 22);
	public static final Font INSTRUCTION_FONT = new Font("맑은 고딕", Font.PLAIN, 16);
	public static final Font STATUS_FONT = new Font("맑은 고딕", Font.BOLD, 20);

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

		JLabel titleLabel = new JLabel("Matching Game", SwingConstants.CENTER);
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
		JButton startButton = createMenuButton("게임 시작", GameConstants.SUCCESS_GREEN);
		startButton.addActionListener(e -> startGame());
		buttonPanel.add(startButton);
		buttonPanel.add(Box.createVerticalStrut(20));

		// 게임 방법 버튼
		JButton instructionButton = createMenuButton("게임 방법", GameConstants.PRIMARY_BLUE);
		instructionButton.addActionListener(e -> showInstructions());
		buttonPanel.add(instructionButton);
		buttonPanel.add(Box.createVerticalStrut(20));

		// 게임 종료 버튼
		JButton exitButton = createMenuButton("게임 종료", GameConstants.WARNING_ORANGE);
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
		showDifficultySelection();
	}

	private void showDifficultySelection() {
		DifficultySelectionDialog difficultyDialog = new DifficultySelectionDialog(this);
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

	public void startGameWithDifficulty(GameDifficulty difficulty) {
		if (gameFrame != null) {
			gameFrame.dispose();
		}
		gameFrame = new MatchingGameFrame(this, difficulty);
		setVisible(false);
	}
}

// 난이도 선택 다이얼로그
class DifficultySelectionDialog extends JDialog {
	private StartMenuFrame parentFrame;

	public DifficultySelectionDialog(StartMenuFrame parent) {
		super(parent, "난이도 선택", true);
		this.parentFrame = parent;
		initializeDialog();
		createDifficultyPanel();
		UIUtils.centerFrameOnScreen(this);
		setVisible(true);
	}

	private void initializeDialog() {
		setSize(500, 400);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);
	}

	private void createDifficultyPanel() {
		JPanel mainPanel = new JPanel() {
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

		// 제목
		JLabel titleLabel = new JLabel("난이도를 선택하세요", SwingConstants.CENTER);
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
		titleLabel.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(30, 0, 40, 0);
		mainPanel.add(titleLabel, gbc);

		// 버튼 패널
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		// 이지 모드 버튼
		JButton easyButton = createDifficultyButton("이지 모드", "2x4 격자 (8장)", GameConstants.SUCCESS_GREEN);
		easyButton.addActionListener(e -> selectDifficulty(GameDifficulty.EASY));
		buttonPanel.add(easyButton);
		buttonPanel.add(Box.createVerticalStrut(20));

		// 노멀 모드 버튼
		JButton normalButton = createDifficultyButton("노멀 모드", "4x4 격자 (16장)", GameConstants.PRIMARY_BLUE);
		normalButton.addActionListener(e -> selectDifficulty(GameDifficulty.NORMAL));
		buttonPanel.add(normalButton);
		buttonPanel.add(Box.createVerticalStrut(20));

		// 하드 모드 버튼
		JButton hardButton = createDifficultyButton("하드 모드", "4x6 격자 (24장)", GameConstants.WARNING_ORANGE);
		hardButton.addActionListener(e -> selectDifficulty(GameDifficulty.HARD));
		buttonPanel.add(hardButton);

		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 30, 0);
		mainPanel.add(buttonPanel, gbc);

		add(mainPanel, BorderLayout.CENTER);
	}

	private JButton createDifficultyButton(String title, String description, Color baseColor) {
		JPanel buttonContent = new JPanel();
		buttonContent.setLayout(new BoxLayout(buttonContent, BoxLayout.Y_AXIS));
		buttonContent.setOpaque(false);

		JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
		descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		descLabel.setForeground(new Color(203, 213, 225));
		descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		buttonContent.add(titleLabel);
		buttonContent.add(Box.createVerticalStrut(5));
		buttonContent.add(descLabel);

		JButton button = new JButton() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

				// 둥근 모서리 배경
				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
			}
		};

		button.setLayout(new BorderLayout());
		button.add(buttonContent, BorderLayout.CENTER);
		button.setPreferredSize(new Dimension(350, 80));
		button.setMaximumSize(new Dimension(350, 80));
		button.setBackground(baseColor);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 호버 효과
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			private Color originalColor = baseColor;

			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(brightenColor(originalColor, 0.2f));
				button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(originalColor);
				button.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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

	private void selectDifficulty(GameDifficulty difficulty) {
		parentFrame.startGameWithDifficulty(difficulty);
		dispose();
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
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
		titleLabel.setForeground(GameConstants.BACKGROUND_COLOR);
		titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		// 게임 방법 설명
		String instructions = """
				■ 게임 목표
				- 격자에 배치된 카드 중 같은 그림의 카드 쌍을 모두 찾아 맞추는 게임입니다.

				■ 난이도별 설명
				- 이지 모드: 2x4 격자 (8장, 4쌍)
				- 노멀 모드: 4x4 격자 (16장, 8쌍)
				- 하드 모드: 4x6 격자 (24장, 12쌍)

				■ 게임 방법
				1. 카드를 클릭하면 카드가 뒤집혀 그림이 나타납니다.
				2. 두 장의 카드를 선택할 수 있습니다.
				3. 두 카드의 그림이 같으면 성공! 카드가 그대로 남아있습니다.
				4. 두 카드의 그림이 다르면 실패! 1초 후 카드가 다시 뒤집힙니다.
				5. 모든 카드 쌍을 맞추면 게임이 완료됩니다.

				■ 게임 조작법
				- ESC 키: 게임 중 메뉴 열기 (일시정지, 다시시작, 메인메뉴, 종료)
				- 마우스 클릭: 카드 선택

				■ 게임 팁
				- 카드의 위치를 기억하세요!
				- 처음 몇 장은 여러 카드를 뒤집어 위치를 파악하는 것이 좋습니다.
				- 시도 횟수가 화면 상단에 표시됩니다.
				- 이지 모드부터 시작해서 점차 어려운 난이도에 도전해보세요!

				■ 다시 시작
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
	private GameDifficulty difficulty;

	public GameState(GameDifficulty difficulty) {
		this.difficulty = difficulty;
	}

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
		return successCount == difficulty.pairsToMatch;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}
}

// 카드 관리 클래스
class CardManager {
	private String[] cardImages;
	private GameDifficulty difficulty;

	public CardManager(GameDifficulty difficulty) {
		this.difficulty = difficulty;
		initializeCards();
		shuffleCards();
	}

	private void initializeCards() {
		cardImages = new String[difficulty.totalCards];
		int pairsNeeded = difficulty.pairsToMatch;

		// 필요한 만큼의 카드 이미지 쌍 생성
		for (int i = 0; i < pairsNeeded; i++) {
			String cardImage = GameConstants.CARD_IMAGES[i % GameConstants.CARD_IMAGES.length];
			cardImages[i * 2] = cardImage;
			cardImages[i * 2 + 1] = cardImage;
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

// 게임 중 메뉴 다이얼로그 클래스
class GameMenuDialog extends JDialog {
	private MatchingGameFrame gameFrame;
	private boolean resumeGame = true;

	public GameMenuDialog(MatchingGameFrame parent) {
		super(parent, "게임 메뉴", true);
		this.gameFrame = parent;
		initializeDialog();
		createMenuPanel();
		UIUtils.centerFrameOnScreen(this);
		setVisible(true);
	}

	private void initializeDialog() {
		setSize(400, 300);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);

		// ESC 키로 다이얼로그 닫기
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				resumeGame();
			}
		});
	}

	private void createMenuPanel() {
		JPanel mainPanel = new JPanel() {
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

		// 제목
		JLabel titleLabel = new JLabel("게임 일시정지", SwingConstants.CENTER);
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
		titleLabel.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 0, 30, 0);
		mainPanel.add(titleLabel, gbc);

		// 버튼 패널
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		// 게임 계속하기 버튼
		JButton resumeButton = createMenuButton("게임 계속하기", GameConstants.SUCCESS_GREEN);
		resumeButton.addActionListener(e -> resumeGame());
		buttonPanel.add(resumeButton);
		buttonPanel.add(Box.createVerticalStrut(15));

		// 게임 다시 시작 버튼
		JButton restartButton = createMenuButton("게임 다시 시작", GameConstants.PRIMARY_BLUE);
		restartButton.addActionListener(e -> restartGame());
		buttonPanel.add(restartButton);
		buttonPanel.add(Box.createVerticalStrut(15));

		// 메인 메뉴로 버튼
		JButton mainMenuButton = createMenuButton("메인 메뉴로", GameConstants.WARNING_ORANGE);
		mainMenuButton.addActionListener(e -> goToMainMenu());
		buttonPanel.add(mainMenuButton);
		buttonPanel.add(Box.createVerticalStrut(15));

		// 게임 종료 버튼
		JButton exitButton = createMenuButton("게임 종료", new Color(220, 38, 127));
		exitButton.addActionListener(e -> exitGame());
		buttonPanel.add(exitButton);

		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 20, 0);
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
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

				// 텍스트 그리기
				super.paintComponent(g);
			}
		};

		button.setPreferredSize(new Dimension(250, 50));
		button.setMaximumSize(new Dimension(250, 50));
		button.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		button.setBackground(baseColor);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 호버 효과
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			private Color originalColor = baseColor;

			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(brightenColor(originalColor, 0.2f));
				button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(originalColor);
				button.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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

	private void resumeGame() {
		resumeGame = true;
		dispose();
	}

	private void restartGame() {
		int choice = JOptionPane.showConfirmDialog(
				this,
				"현재 게임을 다시 시작하시겠습니까?\n진행 상황이 모두 사라집니다.",
				"게임 다시 시작",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (choice == JOptionPane.YES_OPTION) {
			resumeGame = false;
			gameFrame.resetGame();
			dispose();
		}
	}

	private void goToMainMenu() {
		int choice = JOptionPane.showConfirmDialog(
				this,
				"메인 메뉴로 돌아가시겠습니까?\n현재 게임 진행 상황이 사라집니다.",
				"메인 메뉴로",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (choice == JOptionPane.YES_OPTION) {
			resumeGame = false;
			gameFrame.goToMainMenu();
			dispose();
		}
	}

	private void exitGame() {
		int choice = JOptionPane.showConfirmDialog(
				this,
				"게임을 종료하시겠습니까?",
				"게임 종료",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (choice == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	public boolean shouldResumeGame() {
		return resumeGame;
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

	public static void centerFrameOnScreen(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = dialog.getSize();
		int x = (int) ((screenSize.getWidth() - dialogSize.getWidth()) / 2);
		int y = (int) ((screenSize.getHeight() - dialogSize.getHeight()) / 2);
		dialog.setLocation(x, y);
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
	private GameDifficulty difficulty;

	private GameState gameState;
	private CardManager cardManager;

	public MatchingGameFrame(StartMenuFrame parent, GameDifficulty difficulty) {
		this.parentFrame = parent;
		this.difficulty = difficulty;
		gameState = new GameState(difficulty);
		cardManager = new CardManager(difficulty);
		initializeFrame();
		createUI();
		// UI 컴포넌트 생성 완료 후 화면에 표시
		UIUtils.centerFrameOnScreen(this);
		setVisible(true);
	}

	private void initializeFrame() {
		setTitle("Matching 게임 - " + difficulty.displayName);
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
				showGameMenu();
			}
		});

		// ESC 키 바인딩 설정
		setupKeyBindings();
	}

	private void setupKeyBindings() {
		// ESC 키 액션 설정
		InputMap inputMap = getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getRootPane().getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "showMenu");
		actionMap.put("showMenu", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showGameMenu();
			}
		});

		// 포커스 설정으로 키 이벤트 수신 보장
		setFocusable(true);
		requestFocus();
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

		statusLabel = new JLabel("Matching Game - " + difficulty.displayName);
		statusLabel.setPreferredSize(new Dimension(GameConstants.FRAME_WIDTH, 30));
		statusLabel.setForeground(Color.WHITE);
		statusLabel.setFont(GameConstants.STATUS_FONT);
		statusLabel.setHorizontalAlignment(JLabel.CENTER);

		// 메뉴로 돌아가기 버튼
		JButton backButton = new JButton("메뉴로");
		backButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
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
		cardPanel.setLayout(new GridLayout(difficulty.gridRows, difficulty.gridCols));
		cardPanel.setPreferredSize(new Dimension(GameConstants.FRAME_WIDTH, GameConstants.FRAME_HEIGHT));

		cardButtons = new JButton[difficulty.totalCards];

		for (int i = 0; i < difficulty.totalCards; i++) {
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
			updateStatusMessage("★ 축하합니다! 모든 카드를 맞췄습니다! (총 " + gameState.getTryCount() + "번 시도)");
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
		resetButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
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

	private void showGameMenu() {
		// 게임이 진행 중일 때만 메뉴 표시
		if (flipBackTimer != null && flipBackTimer.isRunning()) {
			flipBackTimer.stop();
		}

		GameMenuDialog menuDialog = new GameMenuDialog(this);

		// 다이얼로그가 닫힌 후 포커스 복원
		SwingUtilities.invokeLater(() -> {
			requestFocus();
			if (flipBackTimer != null && !menuDialog.shouldResumeGame()) {
				// 게임이 재시작되거나 메뉴로 이동한 경우 타이머 정리
				flipBackTimer = null;
			} else if (flipBackTimer != null) {
				// 게임을 계속하는 경우 타이머 재시작
				flipBackTimer.start();
			}
		});
	}

	public void resetGame() {
		gameState.reset();
		cardManager.shuffleCards();

		updateStatusMessage("Matching Game - " + difficulty.displayName);

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

		// 포커스 복원
		requestFocus();
	}

	public void goToMainMenu() {
		parentFrame.showMenu();
		dispose();
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
