# 架构与模块边界

## 依赖方向

```text
Android 入口 ──> di（组合根） ──> data（仓库实现）
                         │
                         ├──> presentation（应用界面） ──┐
                         └──> widget（桌面组件） ────────┤
                                                        v
                         domain（模型、仓库契约、用例）
```

依赖只能指向图中的下游。`domain` 不依赖 Android、Compose、数据库或具体文件格式；`presentation` 和 `widget` 只认识领域模型与 `TimetableRepository`，不直接选择数据实现。`di/AppContainer.kt` 是唯一的生产依赖装配入口。

## 目录职责

| 目录 | 唯一职责 | 不应包含 |
| --- | --- | --- |
| `domain/model` | 业务实体、值对象及自身不变量 | Android 类型、界面文案、存储注解 |
| `domain/usecase` | 可独立测试的课程计算规则 | UI 状态、数据库访问 |
| `domain/repository` | 领域需要的数据访问契约 | 具体数据库或示例数据 |
| `data/sample` | 当前可运行的仓库实现和演示数据 | 页面状态、组件渲染 |
| `data/local` | 预留 Room 数据源与实体映射 | Compose 或小组件代码 |
| `data/importexport` | 预留导入、导出、校验与格式映射 | 页面布局 |
| `presentation/timetable` | 页面状态、状态工厂、ViewModel 和路由 | 数据库实现、RemoteViews |
| `presentation/timetable/components` | 无业务数据访问的 Compose 小组件 | 仓库和跨页面导航 |
| `widget` | AppWidget 生命周期、更新调度和 RemoteViews 渲染 | 重新实现周数业务规则 |
| `di` | 创建并连接长期对象 | 业务逻辑和 UI |

## 主要数据流

应用界面：`TimetableRepository.timetable` → `MainScreenViewModel` → `MainScreenStateFactory` → `MainScreenUiState` → Compose 组件。

桌面组件：`TimetableRepository.timetable.value` → `BuildRollingSchedule` → `RollingWeekRemoteViewsRenderer` → Android Launcher。

`TimetableSnapshot` 是一次一致读取所需的聚合，负责保证节次编号和课程编号唯一，并拒绝引用不存在课程的上课安排。周界面和七日小组件分别使用 `BuildWeekSchedule` 与 `BuildRollingSchedule`，因此单双周、起止周等规则不会散落在渲染层。

## 扩展位置

- 持久化：在 `data/local` 实现 Room，并让仓库输出 `StateFlow<TimetableSnapshot>`；只修改 `AppContainer` 的装配。
- 文件导入：在 `data/importexport` 增加“解析 → 校验 → 预览 → 写入”流水线，不让格式对象进入领域和 UI。
- 课程编辑：在 `presentation/courseeditor` 增加独立 Route、ViewModel、UiState，并通过仓库命令接口保存。
- 新小组件：复用领域用例和文本/颜色策略，每种尺寸拥有独立 Provider、Updater 与 Renderer。
- 测试替身：实现内存版 `TimetableRepository` 后通过构造器传入，无需启动数据库或 Android 环境。
