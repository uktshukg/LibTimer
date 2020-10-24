package com.dexter.baseproject.frag_one

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dexter.baseproject.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import io.reactivex.Observable
import java.util.*

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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    internal var lastSacnUpiResult = ""
    lateinit var barcode_scanner: DecoratedBarcodeView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
         barcode_scanner= view.findViewById<DecoratedBarcodeView>(R.id.barcode_scanner)
        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcode_scanner.initializeFromIntent(activity?.intent)
        barcode_scanner.decodeContinuous(callback)
        barcode_scanner.statusView.visibility = View.GONE
    }
    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (!result.text.isNullOrEmpty() && lastSacnUpiResult != result.text) {
                lastSacnUpiResult = result.text



                return
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }
    override fun onResume() {
        super.onResume()
        barcode_scanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragOne.
         */
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

    override fun userIntents(): Observable<UserIntent> {
      return Observable.just(FragOneModel.Intent.Load)
    }

    override fun render(state: FragOneModel.State) {

    }

    override fun handleViewEvent(event: FragOneModel.ViewEvent) {

    }
}