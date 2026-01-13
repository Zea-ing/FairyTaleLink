package com.yush.link.view;

import com.yush.link.utils.GameConstants;
import com.yush.link.controller.GameController;
import com.yush.link.model.Position;
import com.yush.link.utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel {
    private GameController gameController;
    private int rows;
    private int cols;
    private int tileTypes;
    private GameFrame parentFrame;
    private Image backgroundImage;

    // 界面按钮
    private JButton pauseButton;
    private JButton hintButton;

    // 计时器面板
    private TimerPanel timerPanel;

    // 棋盘位置和尺寸
    private int boardStartX;
    private int boardStartY;
    private int actualRows;
    private int actualCols;
    private int boardWidth;
    private int boardHeight;

    // 布局常量
    private static final int TITLE_MARGIN_TOP = 40;
    private static final int TITLE_MARGIN_LEFT = 30;
    private static final int SUBTITLE_MARGIN_TOP = 100;
    private static final int BUTTON_AREA_WIDTH = 180;

    // 按钮位置
    private int buttonX;
    private int buttonStartY;

    public GamePanel(int rows, int cols, GameFrame parentFrame, int tileTypes) {
        this.rows = rows;
        this.cols = cols;
        this.tileTypes = tileTypes;
        this.parentFrame = parentFrame;
        this.gameController = null;

        // 计算棋盘参数
        actualRows = rows + 2;
        actualCols = cols + 2;
        boardWidth = actualCols * GameConstants.TILE_SIZE;
        boardHeight = actualRows * GameConstants.TILE_SIZE;

        // 计算按钮位置
        buttonX = GameConstants.WINDOW_WIDTH - BUTTON_AREA_WIDTH + 20;
        buttonStartY = 180;

        initPanel();
    }

    private void initPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT));
        setOpaque(false);

        // 创建计时器面板
        timerPanel = new TimerPanel();
        timerPanel.setBounds(GameConstants.WINDOW_WIDTH - 170, 40, 140, 50);
        add(timerPanel);

        // 创建界面按钮
        createInterfaceButtons();

        // 加载背景图片
        try {
            backgroundImage = ResourceLoader.loadImage(GameConstants.GAME_BG);
            if (backgroundImage != null) {
                backgroundImage = backgroundImage.getScaledInstance(
                        GameConstants.WINDOW_WIDTH,
                        GameConstants.WINDOW_HEIGHT,
                        Image.SCALE_SMOOTH
                );
            }
        } catch (Exception e) {
            System.err.println("Cannot load game background image, using default color");
            backgroundImage = null;
        }
    }

    /**
     * 创建界面按钮
     */
    private void createInterfaceButtons() {
        // 按钮配置
        String[] buttonConfigs = {
                "Pause", "pause",
                "Hint", "hint"
        };

        Color[] buttonColors = {
                GameConstants.FAIRY_PINK,    // 暂停
                GameConstants.FAIRY_GREEN    // 提示
        };

        int buttonY = buttonStartY;
        int colorIndex = 0;

        for (int i = 0; i < buttonConfigs.length; i += 2) {
            String text = buttonConfigs[i];
            String command = buttonConfigs[i + 1];
            Color buttonColor = buttonColors[colorIndex % buttonColors.length];

            JButton button = createFloatingButton(text, buttonColor);
            button.setActionCommand(command);
            button.addActionListener(e -> handleButtonAction(command));

            button.setBounds(buttonX, buttonY,
                    GameConstants.BUTTON_WIDTH,
                    GameConstants.BUTTON_HEIGHT);
            buttonY += GameConstants.BUTTON_HEIGHT + 15;
            colorIndex++;

            switch(command) {
                case "pause": pauseButton = button; break;
                case "hint": hintButton = button; break;
            }

            add(button);
        }
    }

    private JButton createFloatingButton(String text, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Garamond", Font.BOLD, 20));
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        // 设置边框
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(textColor, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.setBackground(textColor);
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setForeground(textColor);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(textColor.darker());
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.getModel().isRollover()) {
                    button.setBackground(textColor);
                    button.setForeground(Color.WHITE);
                }
            }
        });

        return button;
    }

    private void handleButtonAction(String command) {
        try {
            switch (command) {
                case "pause":
                    if (parentFrame != null) {
                        parentFrame.pauseGame();
                    }
                    break;
                case "hint":
                    if (gameController != null) {
                        gameController.showHint();
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
        createTileButtons();
    }

    private void calculateBoardPosition() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (panelWidth == 0) panelWidth = GameConstants.WINDOW_WIDTH;
        if (panelHeight == 0) panelHeight = GameConstants.WINDOW_HEIGHT;

        int availableWidth = panelWidth - BUTTON_AREA_WIDTH - 40;
        int availableHeight = panelHeight - 120;

        boardStartX = (availableWidth - boardWidth) / 2 + 20;
        boardStartY = (availableHeight - boardHeight) / 2 + 120;

        if (boardStartY + boardHeight > panelHeight - 50) {
            boardStartY = panelHeight - boardHeight - 50;
        }
    }

    private void createTileButtons() {
        // 先移除现有的方块按钮
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof TileButton) {
                remove(comp);
            }
        }
        calculateBoardPosition();

        if (gameController != null && gameController.getGameBoard() != null) {
            for (int row = 0; row < actualRows; row++) {
                for (int col = 0; col < actualCols; col++) {
                    boolean isBorder = (row == 0 || row == actualRows - 1 ||
                            col == 0 || col == actualCols - 1);

                    // 创建方块按钮
                    TileButton tileButton = new TileButton(row, col);
                    // 计算按钮位置
                    int x = boardStartX + col * GameConstants.TILE_SIZE;
                    int y = boardStartY + row * GameConstants.TILE_SIZE;
                    tileButton.setBounds(x, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);

                    if (isBorder) {
                        // 边框按钮：不可见且不可点击
                        tileButton.setVisible(false);
                        tileButton.setEnabled(false);
                    } else {
                        // 游戏区域按钮：设置类型和事件监听
                        if (gameController.getGameBoard().getTile(row, col) != null) {
                            tileButton.setTileType(gameController.getGameBoard().getTile(row, col).getType());
                        } else {
                            tileButton.setTileType(0);
                        }

                        // 添加点击事件监听器
                        tileButton.addActionListener(e -> {
                            if (gameController != null && tileButton.isVisible() && tileButton.isEnabled()) {
                                gameController.onTileClicked(tileButton);
                            }
                        });
                    }

                    // 添加按钮到面板
                    add(tileButton);
                }
            }
        }
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制背景图
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            GradientPaint gradient = new GradientPaint(
                    0, 0, GameConstants.FAIRY_LIGHT_BLUE,
                    getWidth(), getHeight(), GameConstants.FAIRY_PINK
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // 绘制游戏主标题
        g2d.setFont(new Font("Dialog", Font.BOLD, 48));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Fairy Tale Link", TITLE_MARGIN_LEFT, TITLE_MARGIN_TOP + 30);

        // 绘制游戏副标题
        g2d.setFont(new Font("Dialog", Font.PLAIN, 26));
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.drawString(String.format("%d×%d Mode - %d Tile Types", rows, cols, tileTypes),
                TITLE_MARGIN_LEFT, SUBTITLE_MARGIN_TOP);

        // 绘制棋盘背景
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(boardStartX - 10, boardStartY - 10,
                boardWidth + 20, boardHeight + 20, 20, 20);

        // 绘制棋盘边框
        g2d.setColor(GameConstants.FAIRY_BLUE1);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(boardStartX - 10, boardStartY - 10,
                boardWidth + 20, boardHeight + 20, 20, 20);

        // 绘制棋盘网格
        g2d.setColor(new Color(GameConstants.FAIRY_BLUE1.getRed(),
                GameConstants.FAIRY_BLUE1.getGreen(),
                GameConstants.FAIRY_BLUE1.getBlue(), 120));
        g2d.setStroke(new BasicStroke(1.5f));

        // 绘制垂直线
        for (int i = 1; i <= actualCols; i++) {
            int x = boardStartX + i * GameConstants.TILE_SIZE;
            g2d.drawLine(x, boardStartY + GameConstants.TILE_SIZE,
                    x, boardStartY + (actualRows - 1) * GameConstants.TILE_SIZE);
        }

        // 绘制水平线
        for (int i = 1; i <= actualRows; i++) {
            int y = boardStartY + i * GameConstants.TILE_SIZE;
            g2d.drawLine(boardStartX + GameConstants.TILE_SIZE, y,
                    boardStartY + (actualCols - 1) * GameConstants.TILE_SIZE, y);
        }

        // 绘制连接路径
        if (gameController != null) {
            List<Position> path = gameController.getCurrentPath();
            if (path != null && path.size() > 1) {
                drawConnectionPath(g2d, path);
            }
        }

        // 绘制按钮区域背景
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(buttonX - 10, buttonStartY - 10,
                GameConstants.BUTTON_WIDTH + 20,
                (GameConstants.BUTTON_HEIGHT + 15) * 2 + 5, 10, 10);
    }

    /**
     * 当两个方块成功连接时，绘制连接路径
     */
    private void drawConnectionPath(Graphics2D g2d, List<Position> path) {
        Stroke originalStroke = g2d.getStroke();
        Color originalColor = g2d.getColor();

        // 绘制路径线
        for (int i = 0; i < path.size() - 1; i++) {
            Position p1 = path.get(i);
            Position p2 = path.get(i + 1);

            int x1 = boardStartX + p1.getCol() * GameConstants.TILE_SIZE + GameConstants.TILE_SIZE / 2;
            int y1 = boardStartY + p1.getRow() * GameConstants.TILE_SIZE + GameConstants.TILE_SIZE / 2;
            int x2 = boardStartX + p2.getCol() * GameConstants.TILE_SIZE + GameConstants.TILE_SIZE / 2;
            int y2 = boardStartY + p2.getRow() * GameConstants.TILE_SIZE + GameConstants.TILE_SIZE / 2;

            // 绘制主路径
            g2d.setColor(GameConstants.FAIRY_GREEN);
            g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    /**
     * 更新方块显示
     */
    public void updateTile(int row, int col, int tileType, boolean visible) {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof TileButton) {
                TileButton button = (TileButton) comp;
                if (button.getRow() == row && button.getCol() == col) {
                    button.setTileType(tileType);
                    button.setVisible(visible);
                    button.setEnabled(visible);
                    break;
                }
            }
        }
        repaint();
    }

    /**
     * 刷新游戏板（重新创建所有方块按钮）
     */
    public void refreshBoard() {
        createTileButtons();
        repaint();
    }

    /**
     * 显示游戏结束对话框
     */
    public void showGameOver() {
        if (parentFrame != null) {
            int timeInSeconds = timerPanel.getElapsedSeconds();
            parentFrame.showGameOverDialog(timeInSeconds);
        }
    }

    /**
     * 显示无步可走提示
     */
    public void showNoMoves() {
        if (parentFrame != null) {
            parentFrame.showNoMovesDialog();
        }
    }

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    @Override
    public void doLayout() {
        super.doLayout();

        calculateBoardPosition();

        if (timerPanel != null) {
            timerPanel.setBounds(getWidth() - 170, 40, 140, 50);
        }

        if (pauseButton != null) {
            buttonX = getWidth() - BUTTON_AREA_WIDTH + 20;
            int buttonY = buttonStartY;

            pauseButton.setBounds(buttonX, buttonY,
                    GameConstants.BUTTON_WIDTH,
                    GameConstants.BUTTON_HEIGHT);
            buttonY += GameConstants.BUTTON_HEIGHT + 15;

            hintButton.setBounds(buttonX, buttonY,
                    GameConstants.BUTTON_WIDTH,
                    GameConstants.BUTTON_HEIGHT);
        }

        if (gameController != null) {
            createTileButtons();
        }
    }
}