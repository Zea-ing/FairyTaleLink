package com.yush.link.utils;

import java.awt.*;

public class GameConstants {
    // 窗口尺寸
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;

    // 按钮尺寸
    public static final int BUTTON_WIDTH = 140;
    public static final int BUTTON_HEIGHT = 45;

    // 方块大小
    public static final int TILE_SIZE = 55;

    // 资源路径
    public static final String RESOURCES_PATH = "resources/";
    public static final String IMAGES_PATH = RESOURCES_PATH + "images/";
    public static final String TILE_PATH = IMAGES_PATH + "tiles/tile_";

    // 图片文件
    public static final String MENU_BG = IMAGES_PATH + "menu_bg.png";
    public static final String GAME_BG = IMAGES_PATH + "game_bg.png";

    // 颜色
    public static final Color FAIRY_PINK = new Color(255, 180, 200);
    public static final Color FAIRY_BLUE = new Color(0, 255, 255);
    public static final Color FAIRY_GREEN = new Color(204, 255, 0);
    public static final Color FAIRY_YELLOW = new Color(255, 255, 0);
    public static final Color FAIRY_BLUE1 = new Color(135, 206, 250);
    public static final Color FAIRY_LIGHT_BLUE = new Color(173, 216, 230);

    // 游戏模式配置
    public static final GameMode[] GAME_MODES = {
            new GameMode(6, 6, "6×6 Easy", 12),
            new GameMode(8, 8, "8×8 Normal", 18),
            new GameMode(10, 10, "10×10 Hard", 24)
    };

    // 调试模式
    public static final boolean DEBUG_MODE = false;

    // 游戏模式内部类
    public static class GameMode {
        private final int rows;
        private final int cols;
        private final String name;
        private final int tileTypes;

        public GameMode(int rows, int cols, String name, int tileTypes) {
            this.rows = rows;
            this.cols = cols;
            this.name = name;
            this.tileTypes = tileTypes;
        }

        public int getRows() { return rows; }
        public int getCols() { return cols; }
        public String getName() { return name; }
        public int getTileTypes() { return tileTypes; }

        @Override
        public String toString() { return name; }
    }
}