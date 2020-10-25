package com.dexter.baseproject.frag_one

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dexter.baseproject.App
import com.dexter.baseproject.ConvertMILLISToStandard
import com.dexter.baseproject.R
import com.dexter.baseproject.SharedPref
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit




class FragOne : BaseFragment<FragOneModel.State, FragOneModel.ViewEvent, FragOneModel.Intent>(R.layout.fragment_blank ) {
    private var timerDisposable: Disposable? = null
    private lateinit var scanNow: Button
    private var canResubscibe: Boolean =false
    private lateinit var locationdetails: TextView
    private lateinit var duration: TextView
    private lateinit var amount: TextView
    private lateinit var price: TextView
    private lateinit var locationid: TextView
    private lateinit var time: TextView
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
            IntentIntegrator.forSupportFragment(this).initiateScan();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
               Log.e("utkarsh","inside 1")
            } else {
                if(SharedPref.getLong(context!!,context!!.getString(R.string.start_time))!=0L){
                    Observable.timer(200, TimeUnit.MILLISECONDS).subscribe {
                        completeSession.onNext(getCurrentState().locationId!! to getCurrentState().pricePerMin!!)
                    }
                }else {
                    data?.let {
                        Log.e("utkarsh", "inside 2 " + result.contents.toString())
                        Observable.timer(200, TimeUnit.MILLISECONDS).subscribe {
                            scanSubject.onNext(result.contents)
                        }

                    }
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    override fun userIntents(): Observable<UserIntent> {
        Log.e("utkarsh","inside user intents")
        return Observable.mergeArray(
                Observable.just(FragOneModel.Intent.Load),

                scanSubject.map {
                    Log.e("utkarsh","vlaue "+it)
                    FragOneModel.Intent.ScanData(it)
                },
            completeSession.map {
                FragOneModel.Intent.CompleteSession(it)
            }
        )
    }

    override fun render(state: FragOneModel.State) {
        if(state.pricePerMin!=null &&  canResubscibe.not() ){
            canResubscibe = false
            val startTime =
                SharedPref.getLong(context,context!!.getString(R.string.start_time))
              timerDisposable= (Observable.interval(1, TimeUnit.SECONDS).observeOn(schedulerProvider).subscribe {
                time.text = ConvertMILLISToStandard((System.currentTimeMillis()-startTime))
            })
        }
         state.locationDetails?.let {
             locationdetails.text = getString(R.string.location_details)+it
             scanNow.text = "End"
         }
        state.locationId?.let {
            locationid.text = getString(R.string.location_id)+it
        }
        state.pricePerMin?.let {
            price.text =getString(R.string.price)+ it.toString()
        }
        when(state.session){
            FragOneModel.Session.ENDED -> {
                amount.visibility= View.VISIBLE
                duration.visibility = View.VISIBLE
                amount.text = getString(R.string.amount)+state.amount
                duration.text = getString(R.string.duration)+state.duration
                timerDisposable?.dispose()
                scanNow.text = "Scan Now"
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent("abc"));
            }
            else->{
                amount.visibility= View.GONE
                duration.visibility = View.GONE
            }
        }
    }


    override fun onResume() {
        canResubscibe= false
        super.onResume()

    }

    override fun onPause() {
        timerDisposable?.dispose()
        super.onPause()
    }

    override fun handleViewEvent(event: FragOneModel.ViewEvent) {

    }
}