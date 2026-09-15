# Timetable App

面向 Android 和小米澎湃 OS 的本地优先课程表应用。主界面和桌面小组件均采用表格布局，支持单双周、起止周、教室与上下课时间；后续会加入电脑编辑文件后导入手机、课程编辑和提醒。

## 技术基线

- Kotlin、Jetpack Compose、Material 3
- 单向数据流与构造器注入，按 `data / domain / presentation / widget` 分层
- Room、DataStore、Coroutines/Flow（实现对应功能时引入）
- Android AppWidget、RemoteViews，并针对小米小部件规范适配
- 最低 Android 8.0（API 26），目标 Android 16（API 36）

## 代码结构

```text
app/src/main/java/com/did2818/timetable/
├── data/                 仓库实现与数据源；当前为示例数据
├── domain/
│   ├── model/            不依赖 Android 的业务模型
│   ├── repository/       数据访问契约
│   └── usecase/          可复用的课程表计算规则
├── presentation/
│   ├── timetable/        页面状态、状态转换、ViewModel 与路由
│   ├── timetable/components/  小而独立的表格组件
│   ├── navigation/       页面导航
│   └── theme/            Compose 主题
├── widget/               桌面组件更新、渲染与样式策略
├── di/                   应用级依赖装配
├── TimetableApplication.kt
└── MainActivity.kt
```

详细说明：

- [架构与模块边界](docs/ARCHITECTURE.md)
- [开发约定与验证方式](docs/DEVELOPMENT.md)
- [功能任务规划](docs/PROJECT_PLAN.md)

## 本地构建

```bash
source /etc/profile.d/android-dev.sh
./gradlew testDebugUnitTest assembleDebug lintDebug
```

## 当前进度

- 纯 Kotlin 领域层已覆盖学期、节次、课程、上课安排、周数规则和冲突检测。
- 主界面采用“星期列 × 节次行”表格，非当前周课程自动灰显。
- 桌面小组件从当天开始展示未来七天，跨周时会按每天所属周次计算单双周状态。
- 页面与小组件共用领域用例，并通过仓库接口取得同一份课程表快照。
