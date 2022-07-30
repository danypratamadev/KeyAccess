package com.pratama.dany.keyaccess

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.ClassCastException

class BottomSheetProfile : BottomSheetDialogFragment() {

    private lateinit var camera : LinearLayout
    private lateinit var gallery : LinearLayout

    private var mListener : BottomSheetPhotoListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.bottom_sheet_profile, container, false)

        camera = view.findViewById(R.id.camera)
        gallery = view.findViewById(R.id.gallery)

        camera.setOnClickListener {

            mListener?.onClicked(1)
            dismiss()

        }

        gallery.setOnClickListener {

            mListener?.onClicked(0)
            dismiss()

        }

        return view
    }

    interface BottomSheetPhotoListener{

       fun onClicked(code : Int)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            mListener = context as BottomSheetPhotoListener?
        } catch (e : ClassCastException){
            throw ClassCastException(context.toString()+" Please Implementation BottomSheetProfile")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation2
    }

}