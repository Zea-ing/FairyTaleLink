package com.yush.link.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* 资源加载器 */
public class ResourceLoader {
    // 使用HashMap作为图片缓存，key为图片路径，value为加载后的Image对象
    private static final Map<String, Image> imageCache = new HashMap<>();

    // 加载图片资源
    public static Image loadImage(String path) {
        // 1.检查缓存中是否已有该图片
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            // 2.缓存未命中，从文件系统加载
            Image image = ImageIO.read(new File(path));
            if (image != null) {
                imageCache.put(path, image);
            }
            return image;
        } catch (IOException e) {
            System.err.println("无法加载图片: " + path);
            if (GameConstants.DEBUG_MODE) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 加载方块图片
    public static Image loadTileImage(int tileType) {
        String path = String.format("%s%d.png", GameConstants.TILE_PATH, tileType);
        return loadImage(path);
    }
}