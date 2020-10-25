package com.dexter.baseproject.fragments.main_frag

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dexter.base.base.BaseFragment
import com.dexter.base.base.UserIntent
import com.dexter.baseproject.R
import com.dexter.baseproject.utilities.SharedPref
import com.dexter.baseproject.utilities.convertMILLISToStandard
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class MainFrag :
    BaseFragment<MainFragContract.State, MainFragContract.ViewEvent, MainFragContract.Intent>(R.layout.fragment_blank) {
    private var timerDisposable: Disposable? = null
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private lateinit var scanNow: Button
    private var canResubscibe: Boolean = false
    private lateinit var locationdetails: TextView
    private lateinit var duration: TextView
    private lateinit var amount: TextView
    private lateinit var price: TextView
    private lateinit var locationid: TextView
    private var time: TextView? = null
    private var scanSubject: PublishSubject<String> = PublishSubject.create()
    private var completeSession: PublishSubject<Pair<String, Float>> = PublishSubject.create()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanNow = view.findViewById<Button>(R.id.scan_now)
        locationdetails = view.findViewById<Button>(R.id.locationdetails)
        price = view.findViewById<Button>(R.id.price)
        locationid = view.findViewById<Button>(R.id.locationid)
        time = view.findViewById<Button>(R.id.time)
        duration = view.findViewById<Button>(R.id.duration)
        amount = view.findViewById<Button>(R.id.amount)
        scanNow.setOnClickListener {
            IntentIntegrator.forSupportFragment(this).initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                showErrorToast()
            } else {
                if (SharedPref.getLong(context!!, context!!.getString(R.string.start_time)) != 0L) {
                    compositeDisposable.add(Observable.timer(200, TimeUnit.MILLISECONDS).subscribe {
                        completeSession.onNext(getCurrentState().locationId!! to getCurrentState().pricePerMin!!)
                    })
                } else {
                    data?.let {
                        compositeDisposable.add(
                            Observable.timer(200, TimeUnit.MILLISECONDS).subscribe {
                                scanSubject.onNext(result.contents)
                            })

                    }
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun userIntents(): Observable<UserIntent> {
        return Observable.mergeArray(
            Observable.just(MainFragContract.Intent.Load),

            scanSubject.map {
                MainFragContract.Intent.ScanData(it)
            },
            completeSession.map {
                MainFragContract.Intent.CompleteSession(it)
            }
        )
    }

    override fun render(state: MainFragContract.State) {
        when (state.session) {
            MainFragContract.Session.ENDED -> {
                amount.visibility = View.VISIBLE
                duration.visibility = View.VISIBLE
                amount.text = getString(R.string.amount) + state.amount
                duration.text = getString(R.string.duration) + state.duration
                timerDisposable?.dispose()
                timerDisposable = null
                scanNow.text = "Scan Now"
                LocalBroadcastManager.getInstance(context!!)
                    .sendBroadcast(Intent("NOTIFICATION_FILTER"))
            }
            MainFragContract.Session.ONGOING -> {
                amount.visibility = View.GONE
                duration.visibility = View.GONE
                scanNow.text = "END"
            }
            else -> {
                amount.visibility = View.GONE
                duration.visibility = View.GONE
            }
        }
        state.locationDetails?.let {
            locationdetails.text = getString(R.string.location_details) + it
        }
        state.locationId?.let {
            locationid.text = getString(R.string.location_id) + it
        }
        state.pricePerMin?.let {
            price.text = getString(R.string.price) + it.toString()
        }
    }


    override fun onPause() {
        timerDisposable?.dispose()
        timerDisposable= null
        super.onPause()
    }

    override fun handleViewEvent(event: MainFragContract.ViewEvent) {
        when (event) {
            MainFragContract.ViewEvent.ServerErrorToast -> showErrorToast()
            MainFragContract.ViewEvent.ResumeTimer -> startTimer()
        }
    }

    private fun startTimer() {
        time?.let { tv ->
            val startTime =
                SharedPref.getLong(context, context!!.getString(R.string.start_time))
            if(timerDisposable==null) {
                timerDisposable =
                    Observable.interval(1, TimeUnit.SECONDS).observeOn(schedulerProvider)
                        .subscribe {
                            tv.text =
                                convertMILLISToStandard(System.currentTimeMillis() - startTime)
                        }
            }
        }
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    private fun showErrorToast() {
        Toast.makeText(context!!, "Something happened worng", Toast.LENGTH_LONG).show()
    }
}