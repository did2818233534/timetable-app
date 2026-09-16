# Timetable App

“咕嘎课程表”是面向 Android 和小米澎湃 OS 的本地优先课程表应用。主界面和桌面小组件均采用表格布局，支持单双周、起止周、不规则周次、自定义节次与上下课时间、文件导入导出和应用内编辑。

## 技术基线

- Kotlin、Jetpack Compose、Material 3
- 单向数据流与构造器注入，按 `data / domain / presentation / widget` 分层
- Room、DataStore、Coroutines/Flow（实现对应功能时引入）
- Android AppWidget、RemoteViews，并针对小米小部件规范适配
- 最低 Android 8.0（API 26），目标 Android 16（API 36）

## 代码结构

```text
app/src/main/java/com/did2818/timetable/
├── data/                 本地仓库、默认空状态、JSON 导入导出与预览示例
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
- [课表 JSON 文件格式](docs/TIMETABLE_JSON_FORMAT.md)
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
- 已支持从应用内选择 JSON 文件，校验后导入并持久化，同时刷新桌面组件。
- Android 文件管理器可将 JSON 直接“用咕嘎课程表打开”；应用也可导出当前课表为 JSON。
- 全新安装默认没有演示课表，可通过加号从学期信息和自定义节次时间开始新建空课表。
- JSON 中的 `periods` 完整决定学校可用节次、上下课时间及各行是否显示，`visibleDays` 决定星期列，不依赖固定作息。
- 导入后可在“设置”中增删节次、修改每节上下课时间、逐项控制星期和节次显示，并选择是否自动隐藏整学期无课的星期；这些配置会随 JSON 导出。
- 主界面和桌面组件都会按显示配置生成动态行列，默认不插入上午、下午、晚上等时段标题。
- 支持跟随手指位移的左右分页切周；点按课程可编辑备注和周数，点按空白格可新增，长按课程与空白格可复制粘贴。
- 同一格可管理多门课程和不规则指定周次；冲突保留并警告，格子右上角显示 `!`。
- 同一格会同时显示优先级最高的两门课程，分别占据上、下半格；超过两门时显示剩余数量，点击仍可管理全部课程。桌面小组件采用相同规则。
- 支持全局“每节课 / 每天第一节课”提前提醒，也可在单节课程中继承、单独开启或关闭；提醒分钟数及弹窗/闹钟方式均随 JSON 导入导出。
