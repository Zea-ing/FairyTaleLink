package com.yush.link;

import com.yush.link.view.MainMenuFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 使用SwingUtilities确保线程安全
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置外观为系统默认
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 创建并显示主菜单
            new MainMenuFrame().setVisible(true);
        });
    }
}