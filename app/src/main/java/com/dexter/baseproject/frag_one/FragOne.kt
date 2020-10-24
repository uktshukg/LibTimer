package com.dexter.baseproject.frag_one

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dexter.baseproject.R
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragOne.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragOne : BaseFragment<FragOneModel.State, FragOneModel.ViewEvent, FragOneModel.Intent>(R.layout.fragment_blank ) {
    private lateinit var scanNow: Button

    private lateinit var locationdetails: TextView
    private lateinit var price: TextView
    private lateinit var locationid: TextView
    private lateinit var time: TextView
    var  isTimerStarted = false
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var scanSubject: PublishSubject<String> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanNow = view.findViewById<Button>(R.id.scan_now)
        locationdetails = view.findViewById<Button>(R.id.locationdetails)
        price = view.findViewById<Button>(R.id.price)
        locationid = view.findViewById<Button>(R.id.locationid)
        time = view.findViewById<Button>(R.id.time)
        scanNow.setOnClickListener {
            IntentIntegrator.forSupportFragment(this).initiateScan();
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragOne().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
               Log.e("utkarsh","inside 1")
            } else {
                data?.let {
                    Log.e("utkarsh","inside 2 "+ result.contents.toString())
                    Observable.timer(200, TimeUnit.MILLISECONDS).subscribe {
                        scanSubject.onNext(result.contents)
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
                }
        )
    }

    override fun render(state: FragOneModel.State) {
        if(state.pricePerMin!=null && isTimerStarted.not()){
            isTimerStarted = true
            disposable.add(Observable.interval(1, TimeUnit.SECONDS).observeOn(schedulerProvider).subscribe {
                time.text = ConvertSecondsToStandard(it)
            })
        }
         state.locationDetails?.let {
             locationdetails.text = it
         }
        state.locationId?.let {
            locationid.text = it
        }
        state.pricePerMin?.let {
            price.text = it.toString()
        }
    }

    private fun ConvertSecondsToStandard(seconds: Long): CharSequence? {
        val day = TimeUnit.SECONDS.toDays(seconds).toInt()
        val hours = TimeUnit.SECONDS.toHours(seconds) - day * 24
        val minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60
        val second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60

        return ("Day $day Hour $hours Minute $minute Seconds $second")
    }

    override fun handleViewEvent(event: FragOneModel.ViewEvent) {

    }
}