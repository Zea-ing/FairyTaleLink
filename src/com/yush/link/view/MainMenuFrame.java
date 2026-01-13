package com.yush.link.view;

import com.yush.link.utils.GameConstants;
import com.yush.link.utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {
    private JLabel backgroundLabel;

    public MainMenuFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Fairy Tale Link");
        setSize(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 设置背景图片
        Image bgImage = ResourceLoader.loadImage(GameConstants.MENU_BG);
        if (bgImage != null) {
            backgroundLabel = new JLabel(new ImageIcon(bgImage.getScaledInstance(
                    GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT, Image.SCALE_SMOOTH)));
            backgroundLabel.setLayout(new GridBagLayout());
            setContentPane(backgroundLabel);
        } else {
            setContentPane(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(
                            0, 0, GameConstants.FAIRY_LIGHT_BLUE,
                            getWidth(), getHeight(), GameConstants.FAIRY_PINK
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            });
        }

        // 创建菜单面板
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(GameConstants.GAME_MODES.length + 1, 1, 0, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));

        // 游戏模式按钮
        Color[] buttonColors = {
                GameConstants.FAIRY_BLUE,  // 6×6 Easy
                GameConstants.FAIRY_GREEN,  // 8×8 Normal
                GameConstants.FAIRY_YELLOW,  // 10×10 Hard
                GameConstants.FAIRY_PINK    // 退出按钮
        };

        // 边框颜色
        Color[] borderColors = {
                GameConstants.FAIRY_BLUE,
                GameConstants.FAIRY_GREEN,
                GameConstants.FAIRY_YELLOW,
                GameConstants.FAIRY_PINK
        };

        // 添加游戏模式按钮
        for (int i = 0; i < GameConstants.GAME_MODES.length; i++) {
            GameConstants.GameMode mode = GameConstants.GAME_MODES[i];
            Color buttonColor = buttonColors[i];
            Color borderColor = borderColors[i];
            JButton gameButton = createMenuButton(mode.getName(), buttonColor, borderColor);
            gameButton.addActionListener(e -> startGame(mode.getRows(), mode.getCols(), mode.getTileTypes()));
            buttonPanel.add(gameButton);
        }

        // 退出按钮
        JButton exitButton = createMenuButton("Exit Game", buttonColors[3], borderColors[3]);
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        // 添加组件到菜单面板
        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(buttonPanel);

        // 添加菜单面板
        if (backgroundLabel != null) {
            backgroundLabel.add(menuPanel);
        } else {
            add(menuPanel, BorderLayout.CENTER);
        }
    }

    private JButton createMenuButton(String text, Color bgColor, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Garamond", Font.BOLD, 28)); // 加大字体
        button.setForeground(borderColor);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 3),
                BorderFactory.createEmptyBorder(15, 40, 15, 40)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.getModel().isRollover()) {
                    button.setBackground(bgColor.brighter());
                } else {
                    button.setBackground(bgColor);
                }
            }
        });

        return button;
    }

    // 开始游戏
    private void startGame(int rows, int cols, int tileTypes) {
        dispose();  // 关闭主菜单
        SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame(rows, cols, tileTypes); // 创建游戏主窗口
            gameFrame.setVisible(true);
        });
    }
}