package com.pratama.dany.keyaccess

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Bottom_Sheet_Location : BottomSheetDialogFragment() {

    private var mListener : BottomSheetLocationListener? = null

    private lateinit var enableLocation: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.bottom_sheet_location, container, false)

        enableLocation = view.findViewById(R.id.enableLocation)

        enableLocation.setOnClickListener {

            mListener?.onLocationClik()
            dismiss()

        }

        return view

    }

    interface BottomSheetLocationListener{

        fun onLocationClik()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            mListener = context as BottomSheetLocationListener?
        } catch (e : ClassCastException){
            throw ClassCastException(context.toString()+" Please Implementation BottomSheetLogin")
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation2
    }

}