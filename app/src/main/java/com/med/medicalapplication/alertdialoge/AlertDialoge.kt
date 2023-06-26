package com.med.medicalapplication.alertdialoge

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.med.medicalapplication.R
import com.med.medicalapplication.databinding.AlertPopumBinding

class AlertDialoge {

    fun customPopup(context: Context, customPatient: AlertPopumBinding) {
        val dialoge =
            MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
        dialoge.setView(customPatient.root)
        val alert = dialoge.create()
        alert.show()

    }
}