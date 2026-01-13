package com.yush.link.view;

import com.yush.link.utils.GameConstants;
import com.yush.link.utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TileButton extends JButton {
    private int row;
    private int col;
    private int tileType;
    private boolean highlighted;    // 是否高亮
    private boolean selected;       // 是否选中
    private Image tileImage;

    public TileButton(int row, int col) {
        this.row = row;
        this.col = col;
        this.tileType = 0;
        this.highlighted = false;
        this.selected = false;

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        updateTileImage();
    }

    // 图片加载和缓存
    private void updateTileImage() {
        if (tileType > 0) {
            tileImage = ResourceLoader.loadTileImage(tileType);
            if (tileImage != null) {
                tileImage = tileImage.getScaledInstance(
                        GameConstants.TILE_SIZE - 12,
                        GameConstants.TILE_SIZE - 12,
                        Image.SCALE_SMOOTH
                );
            }
        } else {
            tileImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int width = getWidth();
        int height = getHeight();

        // 绘制背景
        if (isEnabled() && tileType > 0) {
            // 1. 创建圆角矩形形状
            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    4, 4, width - 8, height - 8, 15, 15
            );

            // 2. 根据状态确定背景颜色
            Color bgColor;
            if (selected) {
                // 选中状态
                bgColor = new Color(255, 215, 0);
            } else if (highlighted) {
                // 提示高亮状态
                bgColor = new Color(200, 150, 255);
            } else {
                // 正常状态
                int colorIndex = tileType % 4;
                switch (colorIndex) {
                    case 0:
                        bgColor = new Color(220, 240, 255);
                        break;
                    case 1:
                        bgColor = new Color(255, 240, 220);
                        break;
                    case 2:
                        bgColor = new Color(220, 255, 220);
                        break;
                    case 3:
                        bgColor = new Color(255, 220, 255);
                        break;
                    default:
                        bgColor = new Color(173, 216, 230);
                }
            }

            // 3. 填充圆角矩形背景
            g2d.setColor(bgColor);
            g2d.fill(roundedRectangle);

            // 4.绘制边框
            Color borderColor;
            if (selected) {
                borderColor = new Color(200, 100, 0);
                g2d.setStroke(new BasicStroke(3));
            } else if (highlighted) {
                borderColor = new Color(100, 0, 200);
                g2d.setStroke(new BasicStroke(2));
            } else {
                if (bgColor.getRed() > 200 && bgColor.getGreen() > 200 && bgColor.getBlue() > 200) {
                    borderColor = new Color(100, 100, 100);
                } else {
                    borderColor = bgColor.darker().darker();
                }
                g2d.setStroke(new BasicStroke(1));
            }

            g2d.setColor(borderColor);
            g2d.draw(roundedRectangle);

            // 5.绘制方块图片
            if (tileImage != null) {
                int x = (width - tileImage.getWidth(null)) / 2;
                int y = (height - tileImage.getHeight(null)) / 2;
                g2d.drawImage(tileImage, x, y, null);
            }

        } else {
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(0, 0, width, height);
        }

        // 6.添加鼠标悬停效果
        if (getModel().isRollover() && isEnabled() && tileType > 0) {
            g2d.setColor(new Color(255, 255, 255, 60));
            RoundRectangle2D hoverRect = new RoundRectangle2D.Float(
                    2, 2, width - 4, height - 4, 15, 15
            );
            g2d.fill(hoverRect);
        }

        g2d.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    // Getters and Setters
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public int getTileType() { return tileType; }

    public void setTileType(int tileType) {
        this.tileType = tileType;
        updateTileImage();
        repaint();
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        this.selected = false;  // 高亮和选中状态互斥
        repaint();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.highlighted = false;   // 选中和高亮状态互斥
        repaint();
    }

    public boolean isSelected() { return selected; }
    public boolean isHighlighted() { return highlighted; }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        repaint();
    }
}