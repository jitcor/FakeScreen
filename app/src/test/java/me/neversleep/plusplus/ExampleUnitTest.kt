package me.neversleep.plusplus

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        for (method in A().javaClass.methods) {
            println("method.name:" + method.name)
            println("method:" + method.genericReturnType.toString())
        }
    }

}