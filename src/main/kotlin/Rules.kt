enum class Rule {
    NOT_FOUND,
    BUTTER_COMMON,
    BUTTER_BINDVIEW,
    BUTTER_ONCLICK,
    BUTTER_UNBINDER,
    DAGGER_INJECT,
    FIND_VIEW,
}

val ANN_BUTTER_BINDVIEW = "@BindView"
val ANN_BUTTER_ONCLICK = "@OnClick"
val ANN_DAGGER_INJECT = "@Inject"

val SNIPPET_UNBINDER = "Unbinder"
val SNIPPET_FIND_VIEW = "findViewById"
val SNIPPET_BUTTER_COMMON = "butterknife"

fun checkRule(line: String): Rule {
    line.trimStart().run {
        if (startsWith(ANN_BUTTER_BINDVIEW)) return Rule.BUTTER_BINDVIEW
        else if (startsWith(ANN_BUTTER_ONCLICK)) return Rule.BUTTER_ONCLICK
        else if (startsWith(ANN_DAGGER_INJECT)) return Rule.DAGGER_INJECT
        else if (contains(SNIPPET_UNBINDER)) return Rule.BUTTER_UNBINDER
        else if (contains(SNIPPET_FIND_VIEW)) return Rule.FIND_VIEW
        else if (contains(SNIPPET_BUTTER_COMMON)) return Rule.BUTTER_COMMON
    }

    return Rule.NOT_FOUND
}