package com.example.rakthavahini.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rakthavahini.R
import com.example.rakthavahini.data.Donor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DonorAdapter : ListAdapter<Donor, DonorAdapter.DonorViewHolder>(DonorDiff) {

    inner class DonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBloodGroup: TextView = itemView.findViewById(R.id.tv_blood_group)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvLastDonated: TextView = itemView.findViewById(R.id.tv_last_donated)
        val btnCall: Button = itemView.findViewById(R.id.btn_call)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder =
        DonorViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_donor, parent, false)
        )

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = getItem(position)

        holder.tvBloodGroup.text = donor.bloodGroup
        holder.tvName.text = donor.name

        // Show last donated date
        val lastDonated = if (donor.lastDonationDate != null) {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            "Last donated: ${sdf.format(Date(donor.lastDonationDate))}"
        } else {
            "Never donated — Eligible now"
        }
        holder.tvLastDonated.text = lastDonated

        // Call button shows donor name, NOT the number
        holder.btnCall.text = "Call ${donor.name.split(" ").first()}"

        holder.btnCall.setOnClickListener {
            val context = holder.itemView.context

            // Check if CALL_PHONE permission is granted
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // ACTION_CALL — dials directly, number NOT shown on screen
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${donor.phone}")
                }
                context.startActivity(callIntent)
            } else {
                // Permission not granted — fall back to DIAL
                // Show masked number hint in toast but not the real number
                Toast.makeText(
                    context,
                    "Calling ${donor.name}...",
                    Toast.LENGTH_SHORT
                ).show()
                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${donor.phone}")
                }
                context.startActivity(callIntent)
            }
        }
    }

    object DonorDiff : DiffUtil.ItemCallback<Donor>() {
        override fun areItemsTheSame(o: Donor, n: Donor) = o.id == n.id
        override fun areContentsTheSame(o: Donor, n: Donor) = o == n
    }
}