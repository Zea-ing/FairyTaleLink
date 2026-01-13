package com.yush.link.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TimerPanel extends JPanel {
    private JLabel timeLabel;
    private Timer timer;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private boolean isPaused;

    public TimerPanel() {
        initComponents();
        initTimer();
    }

    private void initComponents() {
        // 设置完全透明
        setOpaque(false);
        setLayout(new BorderLayout());

        // 时间显示标签
        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setOpaque(false);

        add(timeLabel, BorderLayout.CENTER);

        // 设置尺寸
        setPreferredSize(new Dimension(120, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // 背景
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, 10, 10
        );

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fill(roundedRect);

        // 边框
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.draw(roundedRect);

        g2d.dispose();
    }

    private void initTimer() {
        elapsedTime = 0;
        isRunning = false;
        isPaused = false;

        // 创建计时器，每100毫秒更新一次
        timer = new Timer(100, e -> {
            if (isRunning && !isPaused) {
                long currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - startTime;
                updateTimeDisplay();
            }
        });
    }

    /**
     * 开始计时
     */
    public void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            isRunning = true;
            isPaused = false;
            timer.start();
        }
    }

    /**
     * 暂停计时
     */
    public void pauseTimer() {
        if (isRunning && !isPaused) {
            isPaused = true;
            timer.stop();
        }
    }

    /**
     * 恢复计时
     */
    public void resumeTimer() {
        if (isRunning && isPaused) {
            isPaused = false;
            startTime = System.currentTimeMillis() - elapsedTime;
            timer.start();
        }
    }

    /**
     * 重置计时
     */
    public void resetTimer() {
        isRunning = false;
        isPaused = false;
        timer.stop();
        elapsedTime = 0;
        updateTimeDisplay();
    }

    /**
     * 停止计时（完成游戏时调用）
     */
    public void stopTimer() {
        isRunning = false;
        isPaused = false;
        timer.stop();
    }

    /**
     * 获取当前经过的时间（秒）
     */
    public int getElapsedSeconds() {
        return (int) (elapsedTime / 1000);
    }

    /**
     * 获取时间字符串 (MM:SS)
     */
    public String getSimpleFormattedTime() {
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 更新时间显示
     */
    private void updateTimeDisplay() {
        timeLabel.setText(getSimpleFormattedTime());

        long minutes = (elapsedTime / 1000) / 60;
        if (minutes > 10) {
            timeLabel.setForeground(new Color(255, 100, 100));
        } else if (minutes > 5) {
            timeLabel.setForeground(new Color(255, 200, 100));
        } else {
            timeLabel.setForeground(Color.WHITE);
        }

        repaint();
    }

    /**
     * 检查计时器是否正在运行
     */
    public boolean isTimerRunning() {
        return isRunning && !isPaused;
    }
}