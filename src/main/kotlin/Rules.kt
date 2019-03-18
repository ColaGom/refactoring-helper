enum class Rule {
    NOT_FOUND,
    BUTTER_COMMON,
    BUTTER_BINDVIEW,
    BUTTER_ONCLICK,
    BUTTER_UNBINDER,
    DAGGER_INJECT,
    RX_SUBJECT_BEHAVOIR,
    RX_SUBJECT_PUBLISH,
    FIND_VIEW
}

const val ANN_BUTTER_BINDVIEW = "@BindView"
const val ANN_BUTTER_ONCLICK = "@OnClick"
const val ANN_DAGGER_INJECT = "@Inject"

const val SNIPPET_UNBINDER = "Unbinder"
const val SNIPPET_FIND_VIEW = "findViewById"
const val SNIPPET_BUTTER_COMMON = "butterknife"

const val RX_SUBJECT_BEHAVIOR = ": BehaviorSubject<"
const val RX_SUBJECT_PUBLISH = ": PublishSubject<"

fun checkRule(line: String): Rule {
    line.trimStart().run {
        if (startsWith(ANN_BUTTER_BINDVIEW)) return Rule.BUTTER_BINDVIEW
        else if (startsWith(ANN_BUTTER_ONCLICK)) return Rule.BUTTER_ONCLICK
        else if (startsWith(ANN_DAGGER_INJECT)) return Rule.DAGGER_INJECT
        else if (contains(SNIPPET_UNBINDER,true)) return Rule.BUTTER_UNBINDER
        else if (contains(SNIPPET_FIND_VIEW)) return Rule.FIND_VIEW
        else if (contains(SNIPPET_BUTTER_COMMON, true)) return Rule.BUTTER_COMMON
        else if (contains(RX_SUBJECT_BEHAVIOR)) return Rule.RX_SUBJECT_BEHAVOIR
        else if (contains(RX_SUBJECT_PUBLISH)) return Rule.RX_SUBJECT_PUBLISH
    }

    return Rule.NOT_FOUND
}