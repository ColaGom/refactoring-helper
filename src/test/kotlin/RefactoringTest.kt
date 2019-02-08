import org.junit.Test
import kotlin.test.assertEquals

class RefactoringTest
{
    @Test fun extractLayoutTest() {
        val id = "abroad_add_btn"
        assertEquals(Refactoring.extractLayoutId("@BindView(R2.id.$id)"), id)
    }

    @Test fun extractFieldNameTest() {
        val line = "internal val mainLayout: LinearLayout? = null"
        assertEquals(Refactoring.extractFieldName(line), "mainLayout")
    }

    @Test fun extractTypeTest() {
        val line = "internal var mainLayout: LinearLayout? = null"
        assertEquals(Refactoring.extractType(line), "LinearLayout")
    }

    @Test fun extractClassNameWhenGenericTest() {
        val line = "abstract class BaseSignFragment<V : BaseView, P : BasePresenter<*>> : Fragment(), SessionManager.SigninResponseListener {"
        assertEquals(Refactoring.extractClassName(line), "BaseSignFragment")
    }

    @Test fun extractClassNameWhenExtendTest() {
        val line = "abstract class BaseSignFragment : Fragment(), SessionManager.SigninResponseListener {"
        assertEquals(Refactoring.extractClassName(line), "BaseSignFragment")
    }

    @Test fun extractClassNameWhenDefaultTest() {
        val line = "abstract class BaseSignFragment {"
        assertEquals(Refactoring.extractClassName(line), "BaseSignFragment")
    }
}
