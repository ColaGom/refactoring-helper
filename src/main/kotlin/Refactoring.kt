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

            if (line.startsWith("@BindView")) {
                val layoutId = extractLayoutId(line)
                val fieldName = extractFieldName(lines.get(lineIdx + 1))
                val type = extractType(lines.get(lineIdx + 1))

                viewList.add(Pair(layoutId, fieldName))
                result.appendln("internal lateinit var $fieldName: $type")
                lineIdx++
            } else
                result.appendln(line)
        }

        result.appendln(createBindBlock(type, viewList))

        return result.toString()
    }

    fun createBindBlock(type: SourceType, viewList: List<Pair<String, String>>) : String {
        val param = if(type == SourceType.Activity || type == SourceType.Fragment) "view : View" else ""
        val prefixField = if(param.isNotEmpty()) "view." else ""
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

fun main(args: Array<String>) {
}