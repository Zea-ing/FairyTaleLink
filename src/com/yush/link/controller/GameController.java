package com.yush.link.controller;

import com.yush.link.model.GameBoard;
import com.yush.link.model.Position;
import com.yush.link.model.Tile;
import com.yush.link.view.GamePanel;
import com.yush.link.view.TileButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private GameBoard gameBoard;
    private GamePanel gamePanel;
    private PathFinder pathFinder;

    private TileButton firstSelected = null;
    private TileButton secondSelected = null;
    private List<Position> currentPath = null;

    // 防止重复提示的标记
    private boolean isShowingHint = false;

    public GameController(int rows, int cols, int tileTypes, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.pathFinder = new PathFinder();
        initializeGame(rows, cols, tileTypes);
    }

    private void initializeGame(int rows, int cols, int tileTypes) {
        gameBoard = new GameBoard(rows, cols, tileTypes);
        gameBoard.initializeBoard();
        updateGamePanel();

        // 检查初始状态是否有可走的步
        if (!hasAvailableMoves()) {
            reshuffleBoard();
        }
    }

    public void onTileClicked(TileButton tileButton) {
        // 如果点击的是已消除的方块，忽略
        if (!tileButton.isVisible() || tileButton.getTileType() == 0) {
            return;
        }

        // 如果当前方块已经被选中，则取消选中
        if (firstSelected == tileButton) {
            firstSelected.setSelected(false);
            firstSelected = null;
            return;
        }

        // 如果没有选中任何方块，选中第一个
        if (firstSelected == null) {
            firstSelected = tileButton;
            firstSelected.setSelected(true);
            return;
        }

        // 选中第二个方块
        secondSelected = tileButton;

        // 尝试匹配两个方块
        attemptMatch();
    }

    private void attemptMatch() {
        if (firstSelected == null || secondSelected == null) {
            return;
        }

        // 获取两个方块的位置
        Position pos1 = new Position(firstSelected.getRow(), firstSelected.getCol());
        Position pos2 = new Position(secondSelected.getRow(), secondSelected.getCol());

        // 检查方块类型是否相同
        if (firstSelected.getTileType() != secondSelected.getTileType()) {
            resetSelection();
            return;
        }

        // 检查是否可以连接
        currentPath = pathFinder.findPath(gameBoard, pos1, pos2);

        if (currentPath != null && currentPath.size() > 0) {
            // 消除方块
            gameBoard.removeTile(pos1.getRow(), pos1.getCol());
            gameBoard.removeTile(pos2.getRow(), pos2.getCol());

            // 更新界面
            gamePanel.updateTile(pos1.getRow(), pos1.getCol(), 0, false);
            gamePanel.updateTile(pos2.getRow(), pos2.getCol(), 0, false);

            // 显示连接路径
            gamePanel.repaint();

            // 延迟清除路径并检查游戏状态
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    currentPath = null;
                    gamePanel.repaint();

                    // 检查游戏是否结束
                    SwingUtilities.invokeLater(() -> {
                        if (gameBoard.isGameComplete()) {
                            gamePanel.showGameOver();
                        } else {
                            // 检查是否还有可走的步
                            if (!hasAvailableMoves()) {
                                gamePanel.showNoMoves();
                            }
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            resetSelection();
        }

        // 重置选中状态
        resetSelection();
    }

    /**
     * 检查是否还有可走的步
     */
    private boolean hasAvailableMoves() {
        return gameBoard.hasAvailableMoves();
    }

    /**
     * 重新洗牌
     */
    private void reshuffleBoard() {
        gameBoard.shuffleBoard();
        updateGamePanel();

        if (!hasAvailableMoves()) {
            gameBoard.initializeBoard();
            updateGamePanel();
        }
    }

    private void resetSelection() {
        if (firstSelected != null) {
            firstSelected.setSelected(false);
            firstSelected = null;
        }
        if (secondSelected != null) {
            secondSelected.setSelected(false);
            secondSelected = null;
        }
    }

    private void updateGamePanel() {
        gamePanel.refreshBoard();

        // 更新所有方块
        for (int row = 0; row < gameBoard.getActualSize(); row++) {
            for (int col = 0; col < gameBoard.getActualSize(); col++) {
                Tile tile = gameBoard.getTile(row, col);
                if (tile != null) {
                    gamePanel.updateTile(row, col, tile.getType(), tile.isActive());
                }
            }
        }
    }

    public void restartGame() {
        gameBoard.initializeBoard();
        resetSelection();
        currentPath = null;
        // 清除正在显示的提示
        isShowingHint = false;
        updateGamePanel();

        // 检查初始状态是否有可走的步
        if (!hasAvailableMoves()) {
            reshuffleBoard();
        }
    }

    public List<Position> getCurrentPath() {
        return currentPath;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * 显示提示 - 高亮任意一组可消除的方块对
     */
    public void showHint() {
        if (gameBoard == null) return;

        // 防止重复点击提示按钮
        if (isShowingHint) {
            return;
        }

        // 清除当前可能存在的选中状态
        resetSelection();

        // 清除现有所有高亮状态
        clearExistingHighlights();

        // 收集所有活跃方块位置
        List<Position> activePositions = new ArrayList<>();
        for (int row = 1; row <= gameBoard.getRows(); row++) {
            for (int col = 1; col <= gameBoard.getCols(); col++) {
                Tile tile = gameBoard.getTile(row, col);
                if (tile != null && tile.isActive()) {
                    activePositions.add(new Position(row, col));
                }
            }
        }

        // 尝试找到任意一组可消除的方块
        for (int i = 0; i < activePositions.size(); i++) {
            for (int j = i + 1; j < activePositions.size(); j++) {
                Position pos1 = activePositions.get(i);
                Position pos2 = activePositions.get(j);

                Tile tile1 = gameBoard.getTile(pos1.getRow(), pos1.getCol());
                Tile tile2 = gameBoard.getTile(pos2.getRow(), pos2.getCol());

                // 检查类型是否相同
                if (tile1 == null || tile2 == null || tile1.getType() != tile2.getType()) {
                    continue;
                }

                // 检查是否可以连接
                List<Position> path = pathFinder.findPath(gameBoard, pos1, pos2);
                if (path != null && !path.isEmpty()) {
                    // 找到可连接的方块对
                    isShowingHint = true;

                    // 高亮显示提示的方块
                    highlightTileForHint(pos1.getRow(), pos1.getCol(), true);
                    highlightTileForHint(pos2.getRow(), pos2.getCol(), true);

                    // 2秒后取消高亮
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            // 取消高亮
                            highlightTileForHint(pos1.getRow(), pos1.getCol(), false);
                            highlightTileForHint(pos2.getRow(), pos2.getCol(), false);
                            // 重置标记
                            isShowingHint = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            isShowingHint = false;
                        }
                    }).start();

                    return;
                }
            }
        }

    }

    /**
     * 清除现有所有高亮状态
     */
    private void clearExistingHighlights() {
        if (gamePanel == null) return;

        Component[] components = gamePanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof TileButton) {
                TileButton button = (TileButton) comp;
                if (button.isHighlighted()) {
                    button.setHighlighted(false);
                }
            }
        }
    }

    /**
     * 高亮或取消高亮方块
     * @param row 行
     * @param col 列
     * @param highlight true为高亮，false为取消高亮
     */
    private void highlightTileForHint(int row, int col, boolean highlight) {
        Component[] components = gamePanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof TileButton) {
                TileButton button = (TileButton) comp;
                if (button.getRow() == row && button.getCol() == col) {
                    button.setHighlighted(highlight);
                    button.repaint();
                    break;
                }
            }
        }
    }
}