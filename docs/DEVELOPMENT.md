# 开发与发布

## 环境

- JDK 17。
- Android SDK 36，最低运行版本 API 26。
- 构建工具使用仓库中的 Gradle Wrapper。
- 日常调试默认使用无窗口 Android 模拟器；真机操作遵循仓库根目录 [AGENT.md](../AGENT.md)。

## 代码边界

- 一个文件只保留一个主要职责；只有紧密相关的小型值对象或私有辅助函数可以同文件存在。
- 生产 Kotlin 文件建议不超过 120 行，函数建议不超过 30 行。资源 ID 映射等纯机械声明可以例外，但不得混入逻辑。
- 公共类型使用能表达职责的名称；只在所属文件使用的实现保持 `private`，仅包内协作的实现优先 `internal`。
- 状态转换与业务计算写成纯函数或用例；Compose 和 RemoteViews 只负责展示与事件转发。
- 依赖通过构造器传入。具体仓库只在 `di` 或预览代码中创建，页面和小组件不得直接依赖示例数据、Room 或文件解析器。
- 新功能先确定所属层；若同时涉及多个层，每层使用自己的模型，并在明确的边界处转换。

## 构建与测试

- `src/test`：领域模型、用例、状态工厂和 ViewModel，优先快速的 JVM 测试。
- `src/androidTest`：Compose 语义、交互和 Android 集成行为。
- 小组件：先测试领域生成结果，再在无窗口模拟器中安装并检查 AppWidget、截图和 Logcat。

完整本地检查：

```bash
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
./gradlew testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
```

仪器测试 APK 应安装到明确指定的无窗口模拟器，再通过 `AndroidJUnitRunner` 执行，避免误操作已连接的真实手机。截图、日志和自动操作全部在后台完成；测试后关闭模拟器、ADB、Gradle 守护进程并清理临时文件。

## 文档职责

- 根目录 `README.md` 只维护当前功能和“课表图片转导入文件”的 AI 提示词。
- `PRODUCT.md` 维护当前产品行为，不记录需求讨论过程。
- `TIMETABLE_JSON_FORMAT.md` 是导入格式的唯一字段规范。
- `ARCHITECTURE.md` 维护模块边界和数据流。
- 本文维护开发、测试、发布和隐私约定。

## 版本与发布

- 用户可见版本使用语义化版本，例如 `0.0.1`；Git 标签和 GitHub Release 使用对应的 `v0.0.1`。
- Android `versionCode` 从 `1` 开始并在后续发布中单调递增；降低该值会导致已安装的更高版本无法覆盖升级。
- 发布前执行全部 JVM 测试、构建、Lint 和模拟器仪器测试。
- GitHub Release 附带可直接侧载的 APK 及其 SHA-256；正式商店发布前需改用受保护的正式签名。
- Release 说明只概括该版本能力和已知限制，不复制开发日志。

## 隐私

- 不提交用户提供的原始课表、转换后的私人 JSON、真机应用数据、临时截图或构建 APK。
- `samples/` 只能使用虚构数据。
- 真机覆盖安装前后使用 SHA-256 或可恢复备份验证课表数据未改变。
- 密码、令牌、SSH 私钥、账号信息和设备授权信息不得进入源码、文档、日志或提交历史。

## 提交前检查

1. 新逻辑有对应的 JVM 或仪器测试。
2. 没有跨层引用具体实现，`domain` 中没有 Android import。
3. 文件和函数仍保持单一职责；超出软限制时先拆分再提交。
4. 执行构建、测试、Lint 与 `git diff --check`。
5. 确认工作区没有私人课表、测试截图、日志或其他临时产物。
