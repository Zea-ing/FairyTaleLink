package com.yush.link.model;

public class Tile {
    private int type; // 方块类型（1-30）
    private int row;  // 行位置
    private int col;  // 列位置
    private boolean active; // 是否活跃（未被消除）

    public Tile(int type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.active = true;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // 比较两个方块是否相同（类型和位置都相同）
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Tile tile = (Tile) obj;
        return type == tile.type &&
                row == tile.row &&
                col == tile.col &&
                active == tile.active;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + row;
        result = 31 * result + col;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Tile{type=%d, row=%d, col=%d, active=%s}",
                type, row, col, active);
    }
}