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

依赖只能指向图中的下游。`domain` 不依赖 Android、Compose、数据库或具体文件格式；`presentation` 和 `widget` 只认识领域模型与 `TimetableRepository`、`TimetableImporter` 等领域契约，不直接选择数据实现。`di/AppContainer.kt` 是唯一的生产依赖装配入口。

## 目录职责

| 目录 | 唯一职责 | 不应包含 |
| --- | --- | --- |
| `domain/model` | 业务实体、值对象及自身不变量 | Android 类型、界面文案、存储注解 |
| `domain/usecase` | 可独立测试的课程计算规则 | UI 状态、数据库访问 |
| `domain/repository` | 领域需要的数据访问契约 | 具体数据库或示例数据 |
| `data/defaults` | 全新安装且没有持久化文件时的未配置空状态 | 演示课程、页面状态 |
| `data/sample` | 预览和测试使用的示例数据 | 生产环境默认数据 |
| `data/importexport` | JSON 文档、领域映射、校验与导入协调 | 页面布局 |
| `data/local` | 应用私有目录中的课表持久化 | Compose 或小组件代码 |
| `presentation/timetable` | 页面状态、状态工厂、ViewModel 和路由 | 数据库实现、RemoteViews |
| `presentation/timetable/components` | 无业务数据访问的 Compose 小组件 | 仓库和跨页面导航 |
| `widget` | AppWidget 生命周期、更新调度和 RemoteViews 渲染 | 重新实现周数业务规则 |
| `di` | 创建并连接长期对象 | 业务逻辑和 UI |

## 主要数据流

应用界面：`TimetableRepository.timetable` → `MainScreenViewModel` → `MainScreenStateFactory` → `MainScreenUiState` → Compose 组件。

应用内编辑：表格手势 → 编辑对话框或复制缓冲区 → `EditTimetable` 纯领域用例 → 冲突检测 → `TimetableRepository.replace` → 小组件刷新。

同格显示：候选课程 → `SelectSlotDisplay` → 本周有效课程 / 最近的未来课程 / 最后的历史课程。冲突数据不会丢弃，由 `FindScheduleConflicts` 生成警告，界面负责显示冲突标记。

文件导入：系统文件选择器或 Android `VIEW/SEND` 文件关联 → 限量 UTF-8 读取 → `TimetableImporter` → JSON 映射与校验 → `TimetableRepository.replace` → 私有文件持久化。

文件导出：当前 `TimetableSnapshot` → `TimetableExporter` → JSON → Android 系统文件创建器。新建课表则由表单解析器生成 `NewTimetableSpec`，再交给纯领域用例 `CreateEmptyTimetable`。

周切换：Compose `HorizontalPager` 同时维护当前页和相邻页，拖动位移直接驱动页面位置；页面稳定后才把选中周同步回 `MainScreenViewModel`。

桌面组件：`TimetableRepository.timetable.value` → 可见星期/节次过滤 → `BuildRollingSchedule` → 动态 `RemoteViews` 行列 → Android Launcher。

`TimetableSnapshot` 是一次一致读取所需的聚合，负责保证节次编号和课程编号唯一，并拒绝引用不存在课程的上课安排。周界面和七日小组件分别使用 `BuildWeekSchedule` 与 `BuildRollingSchedule`，因此单双周、起止周等规则不会散落在渲染层。

## 扩展位置

- 持久化：当前使用应用私有 JSON 文件；后续切换 Room 时保持 `TimetableRepository` 契约，只修改 `AppContainer` 装配。
- 文件交换：当前支持系统选择、外部打开、覆盖导入和完整导出；后续可在写入前加入预览和合并策略。
- 课程编辑：当前使用适合快速修改的表格内对话框；需要复杂批量操作时再拆分独立 Route，并继续复用 `EditTimetable`。
- 新小组件：复用领域用例和文本/颜色策略，每种尺寸拥有独立 Provider、Updater 与 Renderer。
- 测试替身：实现内存版 `TimetableRepository` 后通过构造器传入，无需启动数据库或 Android 环境。
