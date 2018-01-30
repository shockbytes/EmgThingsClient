package at.fhooe.mc.emg.client.things.ui

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import at.fhooe.mc.emg.client.things.R

/**
 * @author Martin Macheiner
 * Date: 29.01.2018.
 */

class ConfirmRebootDialogFragment: DialogFragment() {

    private var onConfirmClickListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle("Confirm reboot?")
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage("Do you really want to reboot your device?")
                .setPositiveButton("Reboot") { _, _ ->
                    onConfirmClickListener?.invoke()
                }
                .setNegativeButton("Cancel") { _, _ -> dismiss()}
                .create()
    }

    fun setOnConfirmClickListener(listener: ()-> Unit): ConfirmRebootDialogFragment {
        onConfirmClickListener = listener
        return this
    }

    companion object {

        fun newInstance(): ConfirmRebootDialogFragment {
            val fragment = ConfirmRebootDialogFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

    }

}