# 开发约定

## 代码边界

- 一个文件只保留一个主要职责；只有紧密相关的小型值对象或私有辅助函数可以同文件存在。
- 生产 Kotlin 文件建议不超过 120 行，函数建议不超过 30 行。资源 ID 映射等纯机械声明可以例外，但不得混入逻辑。
- 公共类型使用能表达职责的名称；只在所属文件使用的实现保持 `private`，仅包内协作的实现优先 `internal`。
- 状态转换与业务计算写成纯函数或用例；Compose 和 RemoteViews 只负责展示与事件转发。
- 依赖通过构造器传入。具体仓库只在 `di` 或预览代码中创建，页面和小组件不得直接依赖示例数据、Room 或文件解析器。
- 新功能先确定所属层；若同时涉及多个层，每层使用自己的模型，并在明确的边界处转换。

## 测试分层

- `src/test`：领域模型、用例、状态工厂和 ViewModel，优先快速的 JVM 测试。
- `src/androidTest`：Compose 语义、交互和 Android 集成行为。
- 小组件：先测试领域生成结果，再在无窗口模拟器中安装并检查 AppWidget、截图和 Logcat。

完整本地检查：

```bash
source /etc/profile.d/android-dev.sh
./gradlew testDebugUnitTest assembleDebug lintDebug
./gradlew connectedDebugAndroidTest
```

模拟器遵循仓库根目录 [AGENT.md](../AGENT.md) 的约定：默认无窗口运行；截图、日志和自动操作在后台完成；测试后关闭模拟器、ADB、Gradle 守护进程并清理临时文件。

## 提交前检查

1. 新逻辑有对应的 JVM 或仪器测试。
2. 没有跨层引用具体实现，`domain` 中没有 Android import。
3. 文件和函数仍保持单一职责；超出软限制时先拆分再提交。
4. 执行构建、测试、Lint 与 `git diff --check`。
