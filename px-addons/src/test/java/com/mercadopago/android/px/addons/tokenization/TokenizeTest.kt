package com.mercadopago.android.px.addons.tokenization

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mercadopago.android.px.addons.BehaviourProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TokenizeTest {

    private val tokenize = BehaviourProvider.getTokenDeviceBehaviour().getTokenize("flowId", "cardId")
        .sessionId("mySessionId")
        .showLoading(true)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun cleanup() {
        Intents.release()
    }

    @Test
    fun `when starting with activity then launch intent to dummy activity`() {
        val scenario = ActivityScenario.launch(TestActivity::class.java)

        scenario.onActivity {
            tokenize.start(it, 123)
        }

        intended(hasComponent(DummyTokenizationActivity::class.java.name))
    }

    @Test
    fun `when starting with fragment then launch intent to dummy activity`() {
        val scenario = FragmentScenario.launch(TestFragment::class.java)

        scenario.onFragment(object : FragmentScenario.FragmentAction<TestFragment> {
            override fun perform(fragment: TestFragment) {
                tokenize.start(fragment, 123)
            }
        })

        intended(hasComponent(DummyTokenizationActivity::class.java.name))
    }
}

class TestActivity : AppCompatActivity()

class TestFragment : Fragment()
