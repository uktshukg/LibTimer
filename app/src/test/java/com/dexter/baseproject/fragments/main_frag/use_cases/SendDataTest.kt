package com.dexter.baseproject.fragments.main_frag.use_cases

import android.content.Context
import com.dexter.baseproject.api.IApiClient
import com.dexter.base.base.Result
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.Assert
import org.junit.Test

class SendDataTest {

    private val apiclient: IApiClient = mock()
    private val context: Context = mock()
    private val sendData = SendData(apiclient, context)

    @Test
    fun `executes return amount and duration given startTime`() {
        val currentTime = System.currentTimeMillis()
        val request = SendData.Request("123", 2.0f, currentTime - 600000)
        whenever(
            apiclient.sendData(
                any()
            )
        ).doReturn(Completable.complete())
        val testObserver = sendData.execute(request).test()


        testObserver.assertValues(
            Result.Progress(0),
            Result.Success(20.0f to 10L)
        )
        testObserver.dispose()


    }

    @Test
    fun `executes fail when amount is wrongly calculated`() {
        val currentTime = System.currentTimeMillis()
        val request = SendData.Request("123", 2.0f, currentTime - 600000)
        whenever(
            apiclient.sendData(
                any()
            )
        ).doReturn(Completable.complete())
        val testObserver = sendData.execute(request).test()


        Assert.assertTrue(testObserver.values().last() != Result.Success(30.0f to 10L))

        testObserver.dispose()


    }


}