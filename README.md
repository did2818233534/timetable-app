# Timetable App

面向 Android 和小米澎湃 OS 的本地课程表应用，计划支持桌面/负一屏小部件、单双周与自定义周数、课程时间和教室设置，以及电脑编辑文件后导入手机。

## 技术基线

- Kotlin、Jetpack Compose、Material 3
- MVVM，按 `data / domain / presentation` 分层
- Room、DataStore、Coroutines/Flow（实现对应功能时引入）
- Android AppWidget、RemoteViews，并针对小米小部件规范适配
- 最低 Android 8.0（API 26），目标 Android 16（API 36）

## 目录结构

```text
app/src/main/java/com/did2818/timetable/
├── core/                 通用时间计算和基础工具
├── data/                 数据库、导入导出、仓库实现
├── domain/               课程模型、仓库接口和业务用例
├── presentation/         Compose界面、导航和界面状态
├── widget/               桌面及负一屏小部件
├── notification/         上课提醒与重建调度
├── di/                   依赖注入
└── MainActivity.kt
```

项目规划见 [docs/PROJECT_PLAN.md](docs/PROJECT_PLAN.md)。

## 本地构建

```bash
source /etc/profile.d/android-dev.sh
./gradlew testDebugUnitTest assembleDebug
```
