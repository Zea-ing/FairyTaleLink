package com.yush.link.view;

import com.yush.link.utils.GameConstants;
import com.yush.link.controller.GameController;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GameController gameController;
    private GamePanel gamePanel;    // 游戏面板
    private JDialog pauseDialog;    // 暂停对话框
    private int rows, cols;
    private int tileTypes;

    /**
     * 游戏主窗口
     */
    public GameFrame(int rows, int cols, int tileTypes) {
        this.rows = rows;
        this.cols = cols;
        this.tileTypes = tileTypes;

        try {
            initUI();
        } catch (Exception e) {
            showErrorDialog("Initialization Failed", e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 初始化用户界面
     */
    private void initUI() {
        setTitle("Fairy Tale Link");
        setSize(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        try {
            // 1. 创建游戏面板
            gamePanel = new GamePanel(rows, cols, this, tileTypes);

            // 2. 添加到主窗口
            setContentPane(gamePanel);

            // 3. 初始化控制器
            gameController = new GameController(rows, cols, tileTypes, gamePanel);
            gamePanel.setGameController(gameController);

            // 4. 创建暂停菜单
            createPauseMenu();

            // 5. 自动启动计时器
            SwingUtilities.invokeLater(() -> {
                if (gamePanel.getTimerPanel() != null) {
                    gamePanel.getTimerPanel().startTimer();
                }
            });

        } catch (Exception e) {
            showErrorDialog("UI Initialization Failed", e.getMessage());
            dispose();
        }
    }

    /**
     * 创建暂停菜单
     */
    private void createPauseMenu() {
        pauseDialog = new JDialog(this, "Game Paused", true);
        pauseDialog.setUndecorated(true);
        pauseDialog.setSize(300, 250);
        pauseDialog.setLocationRelativeTo(this);

        // 创建背景面板
        JPanel pausePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制背景
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 绘制边框
                g2d.setColor(GameConstants.FAIRY_PINK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };

        pausePanel.setLayout(new GridLayout(4, 1, 10, 10));
        pausePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("Game Paused", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setForeground(GameConstants.FAIRY_PINK);

        // 按钮
        JButton resumeButton = createPauseButton("Resume Game",
                new Color(173, 255, 47), Color.BLACK);
        JButton restartButton = createPauseButton("Restart Game",
                new Color(255, 215, 0), Color.BLACK);
        JButton mainMenuButton = createPauseButton("Main Menu",
                new Color(100, 149, 237), Color.WHITE);

        resumeButton.addActionListener(e -> resumeGame());
        restartButton.addActionListener(e -> {
            pauseDialog.dispose();
            restartGame();
        });
        mainMenuButton.addActionListener(e -> {
            pauseDialog.dispose();
            returnToMenu();
        });

        pausePanel.add(titleLabel);
        pausePanel.add(resumeButton);
        pausePanel.add(restartButton);
        pausePanel.add(mainMenuButton);

        pauseDialog.add(pausePanel);
    }

    // 暂停按钮
    private JButton createPauseButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.BOLD, 18));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        // 设置圆角边框
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 鼠标监听事件
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.brighter().darker(), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker(), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
        });

        return button;
    }

    /**
     * 暂停游戏（供GamePanel调用）
     */
    public void pauseGame() {
        if (gamePanel != null && gamePanel.getTimerPanel() != null &&
                gamePanel.getTimerPanel().isTimerRunning()) {
            gamePanel.getTimerPanel().pauseTimer();
            if (pauseDialog != null) {
                pauseDialog.setVisible(true);
            }
        }
    }

    /**
     * 恢复游戏
     */
    private void resumeGame() {
        pauseDialog.dispose();
        if (gamePanel.getTimerPanel() != null) {
            gamePanel.getTimerPanel().resumeTimer();
        }
    }

    /**
     * 返回主界面
     */
    public void returnToMenu() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Return to main menu? Current progress will be lost.",
                "Main Menu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // 用户选择返回主界面
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    new MainMenuFrame().setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Cannot Return to Main Menu", e.getMessage());
                }
            });
        } else {
            // 用户选择不返回
            if (gamePanel != null && gamePanel.getTimerPanel() != null) {
                gamePanel.getTimerPanel().resumeTimer();
            }
        }
    }

    /**
     * 显示错误对话框
     */
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * 游戏结束弹窗（方块全部消除后）
     */
    public void showGameOverDialog(int timeInSeconds) {
        if (gamePanel.getTimerPanel() != null) {
            gamePanel.getTimerPanel().stopTimer();
        }

        String timeStr = String.format("%02d:%02d",
                timeInSeconds / 60, timeInSeconds % 60);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Congratulations! All tiles cleared!\n" +
                        "Time used: " + timeStr + "\n\n" +
                        "Start a new game?",
                "Game Complete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            returnToMenu();
        }
    }

    /**
     * 重新开始游戏
     */
    public void restartGame() {
        // 重置控制器状态
        if (gameController != null) {
            gameController.restartGame();
        }

        // 重置计时器
        if (gamePanel != null && gamePanel.getTimerPanel() != null) {
            gamePanel.getTimerPanel().resetTimer();
            gamePanel.getTimerPanel().startTimer();
        }

        // 刷新游戏面板
        if (gamePanel != null) {
            gamePanel.refreshBoard();
            gamePanel.repaint();
        }
    }

    /**
     * 显示无步可走提示
     */
    public void showNoMovesDialog() {
        if (gamePanel.getTimerPanel() != null) {
            gamePanel.getTimerPanel().pauseTimer();
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "No more moves available!\nDo you want to restart the game?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            returnToMenu();
        }
    }

    /**
     * 清理资源
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}