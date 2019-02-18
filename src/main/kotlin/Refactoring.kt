import java.io.File
import java.lang.StringBuilder
import java.util.regex.Pattern

object Refactoring {
    enum class SourceType(val suffix: String = "") {
        Activity("activity"),
        Fragment("fragment"),
        ViewHolder("viewholder"),
        Other,
        None
    }

    val patternLayoutId = Pattern.compile(Pattern.quote("id.") + "(.*?)" + Pattern.quote(")"))
    val patternFieldName = Pattern.compile("va.* (.*?)" + Pattern.quote(":"))
    val patternType = Pattern.compile(Pattern.quote(": ") + "(.*?)" + Pattern.quote("?"))
    val patternClassName = Pattern.compile(Pattern.quote("class ") + "(.*?)\\W")

    fun refactoring(file: File) = refactoring(file.readText())
    fun refactoring(origin: String): String {
        val lines = origin.lines()
        var result = StringBuilder()
        var viewList = mutableListOf<Pair<String, String>>()


        var lineIdx = 0
        var type = SourceType.None

        while (lineIdx < lines.size) {
            val line = lines.get(lineIdx)

            if (type == SourceType.None && line.contains("class")) {
                type = SourceType.Other

                extractClassName(line).let { className ->
                    SourceType.values().forEach {
                        if (it.suffix.isNotEmpty()) {
                            if (className.contains(it.suffix, true)) {
                                type = it
                                return@let
                            }
                        }
                    }
                }
            }

            if (line.trimStart().startsWith("@BindView")) {
                val layoutId = extractLayoutId(line)
                val fieldName = extractFieldName(lines.get(lineIdx + 1))
                val type = extractType(lines.get(lineIdx + 1))

                viewList.add(Pair(fieldName, layoutId))
                result.appendln("\tprivate lateinit var $fieldName: $type")
                lineIdx++
            } else if (line.trimStart().startsWith("@Inject")) {
                result.appendln(line)
                result.appendln(lines.get(lineIdx + 1).replace("internal", ""))
                lineIdx++
            } else if (!line.contains("butterknife", true))
                result.appendln(line)

            lineIdx++
        }

        if (viewList.size > 0)
            result.appendln(createBindBlock(type, viewList))

        return result.toString()
    }

    fun createBindBlock(type: SourceType, viewList: List<Pair<String, String>>): String {
        val param = if (type == SourceType.Activity || type == SourceType.Fragment) "" else "view : View"
        val prefixField = if (param.isNotEmpty()) "view." else ""
        val sb = StringBuilder()
        sb.appendln("fun bindView($param) {")
        viewList.forEach {
            sb.appendln("\t${it.first} = $prefixField${it.second}")
        }
        sb.appendln("}")
        return sb.toString()
    }

    fun Pattern.grap(str: String) = matcher(str).apply { find() }.group(1)
    fun extractLayoutId(line: String) = patternLayoutId.grap(line)
    fun extractFieldName(line: String) = patternFieldName.grap(line)
    fun extractType(line: String) = patternType.grap(line)
    fun extractClassName(line: String) = patternClassName.grap(line)
}

val ROOT = File("/Users/gom/Android/flitto_android/flitto-android/src/main/java/com/flitto/app")

fun main(args: Array<String>) {
    val target = File(ROOT, "main")

    target.walk().filter { it.isFile && it.extension.equals("kt") }.forEach {
        val result = Refactoring.refactoring(it)
        println(it.path)
        println("-------------------------------")
        println(result)

        it.printWriter().use {
            it.print(result)
        }
    }
}