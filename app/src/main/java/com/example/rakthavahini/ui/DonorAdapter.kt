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

class DonorAdapter :
    ListAdapter<Donor, DonorAdapter.DonorViewHolder>(
        DonorDiff
    ) {

    inner class DonorViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvBloodGroup: TextView =
            itemView.findViewById(R.id.tv_blood_group)

        val tvName: TextView =
            itemView.findViewById(R.id.tv_name)

        val tvLastDonated: TextView =
            itemView.findViewById(R.id.tv_last_donated)

        val btnCall: Button =
            itemView.findViewById(R.id.btn_call)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DonorViewHolder {

        return DonorViewHolder(

            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_donor,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(
        holder: DonorViewHolder,
        position: Int
    ) {

        val donor = getItem(position)

        // SET DATA

        holder.tvBloodGroup.text =
            donor.bloodGroup

        holder.tvName.text =
            donor.name

        // LAST DONATION DATE

        val lastDonated =

            if (
                donor.lastDonationDate != null &&
                donor.lastDonationDate!!.isNotEmpty()
            ) {

                "Last donated: ${donor.lastDonationDate}"

            } else {

                "Never donated — Eligible now"
            }

        holder.tvLastDonated.text =
            lastDonated

        // CALL BUTTON

        val firstName =
            donor.name.split(" ").first()

        holder.btnCall.text =
            "Call $firstName"

        holder.btnCall.setOnClickListener {

            val context =
                holder.itemView.context

            // CHECK CALL PERMISSION

            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                // DIRECT CALL

                val callIntent =
                    Intent(Intent.ACTION_CALL)

                callIntent.data =
                    Uri.parse("tel:${donor.phone}")

                context.startActivity(callIntent)

            } else {

                // DIAL PAD

                Toast.makeText(
                    context,
                    "Calling ${donor.name}...",
                    Toast.LENGTH_SHORT
                ).show()

                val dialIntent =
                    Intent(Intent.ACTION_DIAL)

                dialIntent.data =
                    Uri.parse("tel:${donor.phone}")

                context.startActivity(dialIntent)
            }
        }
    }

    object DonorDiff :
        DiffUtil.ItemCallback<Donor>() {

        override fun areItemsTheSame(
            oldItem: Donor,
            newItem: Donor
        ): Boolean {

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Donor,
            newItem: Donor
        ): Boolean {

            return oldItem == newItem
        }
    }
}