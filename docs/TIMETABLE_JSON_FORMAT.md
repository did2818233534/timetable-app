# 课表 JSON 格式

应用导入 UTF-8 编码的 `.json` 文件。当前格式版本为 `1`，可从 [示例模板](../samples/timetable-template.json) 复制后编辑。

## 顶层结构

- `formatVersion`：固定为 `1`。
- `term`：学期名称、第一周周一的日期和总周数。
- `periods`：节次编号及具体上下课时间。
- `courses`：课程及其一组或多组上课安排。

## 上课安排

每个 `meetings` 项包含：

- `day`：`MONDAY` 至 `SUNDAY`。
- `period`：引用 `periods` 中的节次编号。
- `classroom`：教室，可以留空。
- `startWeek`、`endWeek`：课程生效的起止周，包含首尾。
- `parity`：`EVERY_WEEK`、`ODD_WEEKS` 或 `EVEN_WEEKS`。
- `excludedWeeks`：可选，例如 `[5, 8]` 表示额外排除第 5、8 周。

课程还可以设置可选的 `teacher`、`note` 和 `color`。颜色使用 `#RRGGBB` 或 `#AARRGGBB`；省略时应用会自动分配颜色。

## 导入规则

- 导入会先完整解析并校验，成功后一次性替换当前课表。
- 无效日期、时间、节次、周数、单双周值和课程冲突会拒绝导入。
- 文件最大为 1 MB。
- 导入后的数据保存在应用私有目录，不会写回所选文件。
