package com.example.rakthavahini.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rakthavahini.R

class SearchFragment : Fragment() {

    private val viewModel: DonorViewModel by activityViewModels()
    private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    private val radii = listOf("10 km", "20 km", "50 km")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinnerGroup = view.findViewById<Spinner>(R.id.spinner_blood_group)
        val spinnerRadius = view.findViewById<Spinner>(R.id.spinner_radius)
        val btnSearch = view.findViewById<Button>(R.id.btn_search)
        val rv = view.findViewById<RecyclerView>(R.id.rv_donors)
        val tvNoResults = view.findViewById<TextView>(R.id.tv_no_results)
        val tvCount = view.findViewById<TextView>(R.id.tv_donor_count)

        spinnerGroup.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            bloodGroups
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerRadius.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            radii
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val adapter = DonorAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // Auto load on open with default blood group
        viewModel.setBloodGroupFilter(bloodGroups[0])

        btnSearch.setOnClickListener {
            val selectedGroup = spinnerGroup.selectedItem.toString()
            viewModel.setBloodGroupFilter(selectedGroup)
        }

        viewModel.eligibleDonors.observe(viewLifecycleOwner) { donors ->
            adapter.submitList(donors)
            if (donors.isEmpty()) {
                tvNoResults.visibility = View.VISIBLE
                rv.visibility = View.GONE
                tvCount.text = "0 donors found"
            } else {
                tvNoResults.visibility = View.GONE
                rv.visibility = View.VISIBLE
                tvCount.text = "${donors.size} eligible donor(s) found"
            }
        }
    }
}