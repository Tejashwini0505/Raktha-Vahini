package com.example.rakthavahini.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rakthavahini.R

class DonationLogFragment : Fragment() {

    private val viewModel: DonorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_log, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etHospital = view.findViewById<EditText>(R.id.et_hospital)
        val btnLog = view.findViewById<Button>(R.id.btn_log_donation)
        val rv = view.findViewById<RecyclerView>(R.id.rv_history)

        rv.layoutManager = LinearLayoutManager(requireContext())

        // Get saved donor id
        val prefs = requireContext()
            .getSharedPreferences("donor_prefs", Context.MODE_PRIVATE)
        val donorId = prefs.getInt("donor_id", -1)

        btnLog.setOnClickListener {
            val hospital = etHospital.text.toString().trim()
            if (hospital.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please enter hospital name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (donorId == -1) {
                Toast.makeText(requireContext(),
                    "Please save your profile first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.logDonation(donorId, hospital)
            sendThankYouNotification()
            etHospital.setText("")
            Toast.makeText(requireContext(),
                "Donation logged! Thank you!", Toast.LENGTH_SHORT).show()
        }

        // Show donation history
        if (donorId != -1) {
            viewModel.getDonationHistory(donorId).observe(viewLifecycleOwner) { records ->
                val historyText = records.joinToString("\n") { record ->
                    val date = java.text.SimpleDateFormat(
                        "dd MMM yyyy", java.util.Locale.getDefault()
                    ).format(java.util.Date(record.date))
                    "• ${record.hospitalName} — $date"
                }
                // Simple display in a TextView for now
                view.findViewById<TextView>(R.id.tv_history)?.text =
                    if (records.isEmpty()) "No donations yet."
                    else historyText
            }
        }
    }

    private fun sendThankYouNotification() {
        val channelId = "donation_channel"
        val manager = requireContext()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Donations",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Thank You, Hero!")
            .setContentText("Your donation is recorded. You may have saved a life!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}