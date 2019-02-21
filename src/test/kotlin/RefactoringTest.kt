import org.junit.Test
import kotlin.test.assertEquals

class RefactoringTest {
    @Test
    fun extractLayoutTest() {
        val id = "abroad_add_btn"
        assertEquals(Refactoring.extractLayoutId("@BindView(R2.id.$id)"), id)
    }

    @Test
    fun extractFieldNameTest() {
        val line = "internal val mainLayout: LinearLayout? = null"
        assertEquals(Refactoring.extractFieldName(line), "mainLayout")
    }

    @Test
    fun extractTypeTest() {
        val line = "internal var mainLayout: LinearLayout? = null"
        assertEquals(Refactoring.extractType(line), "LinearLayout")
    }

    @Test
    fun extractClassNameWhenGenericTest() {
        val line =
            "abstract class BaseSignFragment<V : BaseView, P : BasePresenter<*>> : Fragment(), SessionManager.SigninResponseListener {"
        assertEquals(Refactoring.extractClassName(line), "BaseSignFragment")
    }

    @Test
    fun extractClassNameWhenExtendTest() {
        val line = "abstract class BaseSignFragment : Fragment(), SessionManager.SigninResponseListener {"
        assertEquals(Refactoring.extractClassName(line), "BaseSignFragment")
    }

    @Test
    fun extractClassNameWhenDefaultTest() {
        val line = "open class SignupFormView("
        assertEquals(Refactoring.extractClassName(line), "SignupFormView")
    }

    @Test
    fun replaceLayoutIdTest() {
        val line =
            "(rootView.findViewById<View>(R.id.event_user_info_title_txt) as TextView).text = LangSet.getInstance().get(\"plz_sel_sex_and_age\")"
        assertEquals(
            Refactoring.replaceFindViewLine(line),
            "rootView.event_user_info_title_txt.text = LangSet.getInstance().get(\"plz_sel_sex_and_age\")"
        )
    }

    @Test
    fun replaceLayoutIdTest2() {
        val line = "qDateTxt = findViewById<View>(R.id.askQDate) as TextView";
        assertEquals(Refactoring.replaceFindViewLine(line), "qDateTxt = askQDate")
    }


    @Test
    fun replaceUnbinderTest() {
        val actual = "  private int checkedIndex;\n" +
                "  private ProgressDialog loadingDialog;\n" +
                "  private AlertDialog alertDialog;"

        assertEquals(Refactoring.refactoring(MOCK_PART_UNBIND), actual)
    }
}

val MOCK_PART_UNBIND = "  private int checkedIndex;\n" +
        "  private ProgressDialog loadingDialog;\n" +
        "  private Unbinder unbinder;\n" +
        "  private AlertDialog alertDialog;"

val MOCK_SRC = "package com.flitto.app.ui.event.history\n" +
        "\n" +
        "import android.content.Context\n" +
        "import android.util.AttributeSet\n" +
        "import android.view.LayoutInflater\n" +
        "import android.view.View\n" +
        "import android.widget.FrameLayout\n" +
        "import android.widget.ImageView\n" +
        "import android.widget.LinearLayout\n" +
        "import android.widget.TextView\n" +
        "\n" +
        "import com.flitto.app.R\n" +
        "import com.flitto.app.network.model.EventOrigin\n" +
        "import com.flitto.app.network.model.EventTranslation\n" +
        "import com.flitto.app.ui.base.iViewUpdate\n" +
        "import com.flitto.app.util.CharUtils\n" +
        "import com.flitto.app.util.UiUtil\n" +
        "import com.flitto.app.widgets.AudioPlayerView\n" +
        "\n" +
        "class EventHistoryView @JvmOverloads constructor(\n" +
        "    context: Context,\n" +
        "    attrs: AttributeSet? = null,\n" +
        "    defStyleAttr: Int = 0\n" +
        ") : LinearLayout(context, attrs, defStyleAttr), iViewUpdate<EventOrigin> {\n" +
        "\n" +
        "  private var pointTxt: TextView? = null\n" +
        "  private var statusImg: ImageView? = null\n" +
        "  private var contentTxt: TextView? = null\n" +
        "  private var trContentTxt: TextView? = null\n" +
        "  private var audioPan: FrameLayout? = null\n" +
        "  private var audioPlayerView: AudioPlayerView? = null\n" +
        "\n" +
        "  init {\n" +
        "    initView(context)\n" +
        "  }\n" +
        "\n" +
        "  private fun initView(context: Context) {\n" +
        "    val view = LayoutInflater.from(context).inflate(R.layout.row_event_history, this, true)\n" +
        "\n" +
        "    pointTxt = view.findViewById<View>(R.id.event_history_point_txt) as TextView\n" +
        "    statusImg = view.findViewById<View>(R.id.event_history_status_img) as ImageView\n" +
        "    contentTxt = view.findViewById<View>(R.id.event_history_content_txt) as TextView\n" +
        "    trContentTxt = view.findViewById<View>(R.id.event_history_tr_content_txt) as TextView\n" +
        "    audioPan = view.findViewById<View>(R.id.event_history_tr_audio_pan) as FrameLayout\n" +
        "    setOnClickListener(null)\n" +
        "    isEnabled = false\n" +
        "  }\n" +
        "\n" +
        "  override fun updateViews(originItem: EventOrigin?) {\n" +
        "    if (originItem == null) {\n" +
        "      return\n" +
        "    }\n" +
        "\n" +
        "    pointTxt!!.text = originItem.points.toString() + resources.getString(R.string.points_unit)\n" +
        "    pointTxt!!.setBackgroundResource(UiUtil.getColorByPoints(originItem.points))\n" +
        "\n" +
        "    contentTxt!!.text = originItem.content\n" +
        "\n" +
        "    audioPan!!.removeAllViews()\n" +
        "    audioPan!!.visibility = View.GONE\n" +
        "    trContentTxt!!.visibility = View.GONE\n" +
        "    val translationItem = originItem.eventTranslationItem\n" +
        "    if (translationItem != null) {\n" +
        "      if (!CharUtils.isStringEmpty(translationItem.trContent)) {\n" +
        "        trContentTxt!!.text = translationItem.trContent\n" +
        "        trContentTxt!!.visibility = View.VISIBLE\n" +
        "      }\n" +
        "\n" +
        "      if (!CharUtils.isStringEmpty(translationItem.trContentUrl)) {\n" +
        "        audioPlayerView = AudioPlayerView(context, translationItem.trContentUrl)\n" +
        "        audioPlayerView!!.setPlayRes(R.drawable.ic_play)\n" +
        "        audioPan!!.addView(audioPlayerView)\n" +
        "        audioPan!!.visibility = View.VISIBLE\n" +
        "      }\n" +
        "\n" +
        "      statusImg!!.visibility = View.VISIBLE\n" +
        "      when (translationItem.status) {\n" +
        "        EventTranslation.STATUS.SELECTED -> statusImg!!.setImageResource(R.drawable.ic_checkbox_checked)\n" +
        "        EventTranslation.STATUS.NOT_SELECTED -> statusImg!!.setImageResource(R.drawable.icon_tr_not_selected)\n" +
        "        EventTranslation.STATUS.PENDING -> statusImg!!.setImageResource(R.drawable.ic_pending)\n" +
        "        else -> statusImg!!.setImageResource(R.drawable.ic_pending)\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "\n" +
        "  override fun reqUpdateModel() {\n" +
        "\n" +
        "  }\n" +
        "\n" +
        "  companion object {\n" +
        "    private val TAG = EventHistoryView::class.java.simpleName\n" +
        "  }\n" +
        "}\n"