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

    private val NEED_FIX = "!!-FIX"

    private val patternLayoutId = Pattern.compile(Pattern.quote("id.") + "(.*?)" + Pattern.quote(")"))
    private val patternFieldName = Pattern.compile("va.* (.*?)" + Pattern.quote(":"))
    private val patternType = Pattern.compile(Pattern.quote(": ") + "(.*?)" + Pattern.quote("?"))
    private val patternClassName = Pattern.compile(Pattern.quote("class ") + "(.*?)\\W")
    private val patternMethodName = Pattern.compile(Pattern.quote("fun ") + "(.*?)" + Pattern.quote("("))

    fun refactoring(file: File) = refactoring(file.readText())
    fun refactoring(origin: String): String {
        val lines = origin.lines()
        var result = StringBuilder()
        var viewList = mutableListOf<Pair<String, String>>()
        var funcList = mutableListOf<Pair<String, String>>()


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

            try {
                when (checkRule(line)) {
                    Rule.NOT_FOUND -> {
                        result.appendln(line)
                    }
                    Rule.BUTTER_UNBINDER,
                    Rule.BUTTER_COMMON -> {
                        // Skip.
                    }
                    Rule.BUTTER_BINDVIEW -> {
                        val layoutId = extractLayoutId(line)
                        val fieldName = extractFieldName(lines.get(lineIdx + 1))
                        val type = extractType(lines.get(lineIdx + 1))

                        viewList.add(Pair(fieldName, layoutId))
                        result.appendln("\tprivate lateinit var $fieldName: $type")
                        lineIdx++
                    }
                    Rule.BUTTER_ONCLICK -> {
                        val layoutId = extractLayoutId(line)
                        val funcName = extractMethodName(lines.get(lineIdx + 1))
                        result.appendln("//$line")
                        funcList.add(Pair(layoutId, funcName))
//                        result.appendln("$layoutId.setOnClickListener { $funcName() }")
                    }
                    Rule.DAGGER_INJECT -> {
                        result.appendln(line)
                        result.appendln(lines.get(lineIdx + 1).replace("internal", "internal lateinit"))
                        lineIdx++
                    }
                    Rule.FIND_VIEW -> result.appendln(replaceFindViewLine(line))
                }

            } catch (e: Exception) {
                println("line : $lineIdx Exception!!!")
                e.printStackTrace()
                result.appendln("$NEED_FIX exception : $line")
            }

            lineIdx++
        }

        if (viewList.size > 0)
            result.appendln(createBindBlock(type, viewList))

        funcList.forEach {
            result.appendln("${it.first}.setOnClickListener { ${it.second}() }")
        }


        return result.toString().trimEnd()
    }

    fun replaceFindViewPart(part: String): String {
        val idx = part.trim().indexOf("findViewById")
        val prefix = part.substring(0, idx).replace(Regex("""[()]"""), "")
        val layoutId = extractLayoutId(part)
        val suffix = if (part.contains(").")) part.substring(part.indexOf(").") + 1) else ""

        val sb = StringBuilder()

        if (prefix.isNotEmpty())
            sb.append("${prefix.replace(".", "")}.")

        sb.append(layoutId)
        sb.append(suffix)

        return sb.toString()
    }

    fun replaceFindViewLine(org: String): String {
        if (org.contains("=")) {
            val strArr = org.split("=")
            var prefix = ""
            var suffix = ""

            if (strArr[0].contains("findViewById")) {
                prefix = replaceFindViewPart(strArr[0])
                suffix = strArr[1]
            } else {
                prefix = strArr[0]
                suffix = replaceFindViewPart(strArr[1])
            }

            return "${prefix.trim()} = ${suffix.trim()}"

        } else {
            return org
        }
    }

    fun createBindBlock(type: SourceType, viewList: List<Pair<String, String>>): String {
        val param = if (type == SourceType.Activity || type == SourceType.Fragment) "" else "view : View"
        val prefixField = if (param.isNotEmpty()) "view." else ""
        val sb = StringBuilder()
        sb.appendln("private fun bindView($param) {")
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
    fun extractMethodName(line: String) = patternMethodName.grap(line)
}

val ROOT = File("##")

fun main(args: Array<String>) {
    val target = File(ROOT, "ui/login")

    target.walk().filter { it.isFile && it.extension.equals("kt") }.forEach {
        try {
            val result = Refactoring.refactoring(it)

            println("start ---- ${it.path}")

            it.printWriter().use {
                it.print(result)
            }

        } catch (e: Exception) {
            println("Expcetion : ${it.path}")
        }

    }

}

fun printAnalysis() {

}
