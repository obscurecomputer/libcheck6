package computer.obscure.libcheck6

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import computer.obscure.libcheck6.manager.VariaRadarManager
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VariaRadarManagerInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(appContext)
    }

    @Test
    fun testVariaRadarManagerInitialization() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = VariaRadarManager(appContext)
        assertNotNull(manager)
    }
}
