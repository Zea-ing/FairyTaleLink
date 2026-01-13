package com.yush.link.model;

import java.util.*;

public class GameBoard {
    private int rows;    // 游戏区域行数（高度）
    private int cols;    // 游戏区域列数（宽度）
    private int tileTypes; // 图片种类数
    private int actualRows; // 包含外圈的行数
    private int actualCols; // 包含外圈的列数
    private Tile[][] board;

    public GameBoard(int rows, int cols, int tileTypes) {
        this.rows = rows;
        this.cols = cols;
        this.tileTypes = tileTypes;
        this.actualRows = rows + 2;
        this.actualCols = cols + 2;
        this.board = new Tile[actualRows][actualCols];
    }

    /**
     * 初始化游戏棋盘
     */
    public void initializeBoard() {
        // 清空棋盘
        for (int i = 0; i < actualRows; i++) {
            for (int j = 0; j < actualCols; j++) {
                board[i][j] = null;
            }
        }

        // 计算需要的方块总数（偶数）
        int totalTiles = rows * cols;
        if (totalTiles % 2 != 0) {
            totalTiles--;
        }

        // 创建方块类型列表
        List<Integer> tileTypesList = new ArrayList<>();

        // 确定需要多少种不同类型的方块
        int neededPairs = totalTiles / 2;
        int availableTypes = Math.min(this.tileTypes, neededPairs);

        // 如果需要的方块对数超过可用类型数，需要重复使用类型
        if (neededPairs > availableTypes) {
            int baseCount = neededPairs / availableTypes;
            int remainder = neededPairs % availableTypes;

            for (int type = 1; type <= availableTypes; type++) {
                int count = baseCount;
                if (type <= remainder) {
                    count++;
                }
                for (int i = 0; i < count * 2; i++) {
                    tileTypesList.add(type);
                }
            }
        } else {
            for (int type = 1; type <= neededPairs; type++) {
                tileTypesList.add(type);
                tileTypesList.add(type);
            }
        }

        // 随机打乱顺序
        Collections.shuffle(tileTypesList);

        // 放置方块到游戏区域
        int index = 0;
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                if (index < tileTypesList.size()) {
                    int type = tileTypesList.get(index);
                    board[i][j] = new Tile(type, i, j);
                    index++;
                }
            }
        }

        // 设置外圈空白格
        for (int i = 0; i < actualRows; i++) {
            for (int j = 0; j < actualCols; j++) {
                if (i == 0 || i == actualRows - 1 || j == 0 || j == actualCols - 1) {
                    board[i][j] = null;
                }
            }
        }
    }

    /**
     * 检查是否还有可走的步
     */
    public boolean hasAvailableMoves() {
        List<Position> activePositions = new ArrayList<>();

        // 收集所有活跃方块位置
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                Tile tile = board[i][j];
                if (tile != null && tile.isActive()) {
                    activePositions.add(new Position(i, j));
                }
            }
        }

        // 检查每一对方块是否可以连接
        for (int i = 0; i < activePositions.size(); i++) {
            for (int j = i + 1; j < activePositions.size(); j++) {
                Position pos1 = activePositions.get(i);
                Position pos2 = activePositions.get(j);

                Tile tile1 = getTile(pos1.getRow(), pos1.getCol());
                Tile tile2 = getTile(pos2.getRow(), pos2.getCol());

                if (tile1 != null && tile2 != null &&
                        tile1.getType() == tile2.getType() &&
                        canConnect(pos1, pos2)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检查两个方块是否可以连接
     */
    public boolean canConnect(Position pos1, Position pos2) {
        if (pos1.equals(pos2)) {
            return false;
        }

        Tile tile1 = getTile(pos1.getRow(), pos1.getCol());
        Tile tile2 = getTile(pos2.getRow(), pos2.getCol());

        if (tile1 == null || tile2 == null ||
                !tile1.isActive() || !tile2.isActive()) {
            return false;
        }

        return tile1.getType() == tile2.getType();
    }

    /**
     * 获取指定位置的方块
     */
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= actualRows || col < 0 || col >= actualCols) {
            return null;
        }
        return board[row][col];
    }

    /**
     * 移除指定位置的方块
     */
    public void removeTile(int row, int col) {
        if (row >= 0 && row < actualRows && col >= 0 && col < actualCols) {
            board[row][col] = null;
        }
    }

    /**
     * 检查游戏是否完成
     */
    public boolean isGameComplete() {
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                Tile tile = board[i][j];
                if (tile != null && tile.isActive()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查指定位置是否为空白（是否可通行）
     */
    public boolean isEmpty(int row, int col) {
        if (row < 0 || row >= actualRows || col < 0 || col >= actualCols) {
            return false;
        }

        if (row == 0 || row == actualRows - 1 || col == 0 || col == actualCols - 1) {
            return true;
        }

        Tile tile = board[row][col];
        return tile == null || !tile.isActive();
    }

    /**
     * 洗牌方法
     */
    public void shuffleBoard() {
        List<Tile> activeTiles = new ArrayList<>();
        List<Position> positions = new ArrayList<>();

        // 收集所有活跃方块
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                Tile tile = board[i][j];
                if (tile != null && tile.isActive()) {
                    activeTiles.add(tile);
                    positions.add(new Position(i, j));
                }
            }
        }

        // 随机打乱方块类型
        Collections.shuffle(activeTiles);

        // 重新分配类型到位置
        for (int i = 0; i < positions.size(); i++) {
            Position pos = positions.get(i);
            Tile tile = activeTiles.get(i);
            board[pos.getRow()][pos.getCol()] = new Tile(
                    tile.getType(),
                    pos.getRow(),
                    pos.getCol()
            );
        }
    }


    /**
     * 查找一对可消除的方块（提示功能）
     */
    public Position[] findHint() {
        // 收集所有活跃方块位置
        List<Position> activePositions = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                Tile tile = board[i][j];
                if (tile != null && tile.isActive()) {
                    activePositions.add(new Position(i, j));
                }
            }
        }

        // 尝试找到任意一组可消除的方块
        for (int i = 0; i < activePositions.size(); i++) {
            for (int j = i + 1; j < activePositions.size(); j++) {
                Position pos1 = activePositions.get(i);
                Position pos2 = activePositions.get(j);

                Tile tile1 = getTile(pos1.getRow(), pos1.getCol());
                Tile tile2 = getTile(pos2.getRow(), pos2.getCol());

                if (tile1 != null && tile2 != null && tile1.getType() == tile2.getType()) {
                    // 返回这对方块的位置，让GameController检查是否可以连接
                    return new Position[]{pos1, pos2};
                }
            }
        }

        return null;
    }

    // Getter 方法
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTileTypes() { return tileTypes; }
    public int getActualRows() { return actualRows; }
    public int getActualCols() { return actualCols; }
    public int getActualSize() { return Math.max(actualRows, actualCols); }
    public Tile[][] getBoard() { return board.clone(); }
}