package com.yush.link.controller;

import com.yush.link.model.GameBoard;
import com.yush.link.model.Position;

import java.util.*;

/**
 * 路径查找，负责查找两个方块之间的连接路径
 */
public class PathFinder {
    // 路径缓存：key = "row1,col1:row2,col2"，value = 路径列表
    private final Map<String, List<Position>> pathCache = new HashMap<>();

    /**
     * 查找两个方块之间的连接路径
     */
    public List<Position> findPath(GameBoard board, Position start, Position end) {
        // 起点-终点相同
        if (start.equals(end)) {
            return null;
        }

        // 检查缓存
        String cacheKey = getCacheKey(start, end);
        List<Position> cachedPath = pathCache.get(cacheKey);
        if (cachedPath != null) {
            return cachedPath;
        }

        // 获取两个位置的方块类型
        Integer startType = getTileType(board, start);
        Integer endType = getTileType(board, end);

        // 类型不同直接返回null
        if (startType == null || endType == null || !startType.equals(endType)) {
            return null;
        }

        // 按拐角数从少到多尝试查找
        List<Position> path = null;
        if ((path = findStraightPath(board, start, end)) != null ||
                (path = findOneCornerPath(board, start, end)) != null ||
                (path = findTwoCornerPath(board, start, end)) != null) {

            // 缓存结果
            pathCache.put(cacheKey, path);
            return path;
        }

        return null;
    }

    /**
     * 获取方块类型
     */
    private Integer getTileType(GameBoard board, Position pos) {
        var tile = board.getTile(pos.getRow(), pos.getCol());
        return (tile != null && tile.isActive()) ? tile.getType() : null;
    }

    /**
     * 生成缓存键
     */
    private String getCacheKey(Position start, Position end) {
        // 确保相同的起点终点对生成相同的键
        int hash1 = start.hashCode();
        int hash2 = end.hashCode();
        return hash1 <= hash2 ?
                hash1 + ":" + hash2 : hash2 + ":" + hash1;
    }

    /**
     * 查找直线路径
     */
    private List<Position> findStraightPath(GameBoard board, Position start, Position end) {
        // 水平方向
        if (start.getRow() == end.getRow()) {
            if (checkHorizontalPath(board, start, end)) {
                return buildStraightPath(start, end, true);
            }
        }

        // 垂直方向
        if (start.getCol() == end.getCol()) {
            if (checkVerticalPath(board, start, end)) {
                return buildStraightPath(start, end, false);
            }
        }

        return null;
    }

    /**
     * 检查水平路径是否畅通
     */
    private boolean checkHorizontalPath(GameBoard board, Position start, Position end) {
        int row = start.getRow();
        int minCol = Math.min(start.getCol(), end.getCol());
        int maxCol = Math.max(start.getCol(), end.getCol());

        for (int col = minCol + 1; col < maxCol; col++) {
            if (!board.isEmpty(row, col)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查垂直路径是否畅通
     */
    private boolean checkVerticalPath(GameBoard board, Position start, Position end) {
        int col = start.getCol();
        int minRow = Math.min(start.getRow(), end.getRow());
        int maxRow = Math.max(start.getRow(), end.getRow());

        for (int row = minRow + 1; row < maxRow; row++) {
            if (!board.isEmpty(row, col)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 构建直线路径
     */
    private List<Position> buildStraightPath(Position start, Position end, boolean isHorizontal) {
        List<Position> path = new ArrayList<>();

        if (isHorizontal) {
            int row = start.getRow();
            int minCol = Math.min(start.getCol(), end.getCol());
            int maxCol = Math.max(start.getCol(), end.getCol());

            for (int col = minCol; col <= maxCol; col++) {
                path.add(new Position(row, col));
            }
        } else {
            int col = start.getCol();
            int minRow = Math.min(start.getRow(), end.getRow());
            int maxRow = Math.max(start.getRow(), end.getRow());

            for (int row = minRow; row <= maxRow; row++) {
                path.add(new Position(row, col));
            }
        }

        return path;
    }

    /**
     * 查找一个拐角的路径
     */
    private List<Position> findOneCornerPath(GameBoard board, Position start, Position end) {
        // 两个可能的拐点方向
        Position corner1 = new Position(start.getRow(), end.getCol());
        Position corner2 = new Position(end.getRow(), start.getCol());

        List<Position> path = checkCornerPath(board, start, end, corner1);
        if (path != null) return path;

        return checkCornerPath(board, start, end, corner2);
    }

    /**
     * 查找两个拐角的路径
     */
    private List<Position> findTwoCornerPath(GameBoard board, Position start, Position end) {
        int boardSize = board.getActualSize();

        // 只检查start和end周围的空白格
        List<Position> emptyPositions = new ArrayList<>();

        // 检查start周围的空白格
        for (int row = Math.max(0, start.getRow() - 2); row <= Math.min(boardSize - 1, start.getRow() + 2); row++) {
            for (int col = Math.max(0, start.getCol() - 2); col <= Math.min(boardSize - 1, start.getCol() + 2); col++) {
                if (board.isEmpty(row, col)) {
                    emptyPositions.add(new Position(row, col));
                }
            }
        }

        // 检查end周围的空白格
        for (int row = Math.max(0, end.getRow() - 2); row <= Math.min(boardSize - 1, end.getRow() + 2); row++) {
            for (int col = Math.max(0, end.getCol() - 2); col <= Math.min(boardSize - 1, end.getCol() + 2); col++) {
                if (board.isEmpty(row, col)) {
                    Position pos = new Position(row, col);
                    // 避免重复添加
                    if (!emptyPositions.contains(pos)) {
                        emptyPositions.add(pos);
                    }
                }
            }
        }

        // 尝试所有可能的两个中间点组合
        for (Position corner1 : emptyPositions) {
            List<Position> path1 = findStraightPath(board, start, corner1);
            if (path1 == null) continue;

            for (Position corner2 : emptyPositions) {
                if (corner1.equals(corner2)) continue;

                List<Position> path2 = findStraightPath(board, corner1, corner2);
                if (path2 == null) continue;

                List<Position> path3 = findStraightPath(board, corner2, end);
                if (path3 == null) continue;

                // 合并路径
                return mergePaths(path1, path2, path3);
            }
        }

        return null;
    }


    /**
     * 检查拐点路径
     */
    private List<Position> checkCornerPath(GameBoard board, Position start, Position end, Position corner) {
        if (!board.isEmpty(corner.getRow(), corner.getCol())) {
            return null;
        }

        List<Position> path1 = findStraightPath(board, start, corner);
        List<Position> path2 = findStraightPath(board, corner, end);

        if (path1 != null && path2 != null) {
            return mergePaths(path1, path2);
        }

        return null;
    }

    /**
     * 合并两个路径
     */
    private List<Position> mergePaths(List<Position> path1, List<Position> path2) {
        List<Position> merged = new ArrayList<>(path1);
        merged.addAll(path2.subList(1, path2.size()));
        return merged;
    }

    /**
     * 合并三个路径
     */
    private List<Position> mergePaths(List<Position> path1, List<Position> path2, List<Position> path3) {
        List<Position> merged = new ArrayList<>(path1);
        merged.addAll(path2.subList(1, path2.size()));
        merged.addAll(path3.subList(1, path3.size()));
        return merged;
    }
}