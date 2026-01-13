# FairyTaleLink
本项目旨在开发一款名为"Fairy Tale Link"的连连看小游戏，通过实现经典连连看游戏的核心玩法，掌握Java Swing图形界面开发，实践MVC架构模式，设计和优化路径查找算法，解决两个方块间的最优连接问题。

编程语言：Java
图形界面库：Java Swing
开发工具：IntelliJ IDEA Community

依赖关系:
1.Main 启动 MainMenuFrame
2.MainMenuFrame 创建 GameFrame
3.GameFrame 包含 GamePanel
4.GamePanel 使用 GameController 处理逻辑
5.GameController 调用 PathFinder 查找路径
6.所有UI组件通过 ResourceLoader 加载资源
7.所有类通过 GameConstants 获取配置参数
