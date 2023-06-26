package com.med.medicalapplication.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.med.medicalapplication.databinding.PatientAdapterBinding
import com.med.medicalapplication.mvvm.PatientModelClass

class PatientAdapter : RecyclerView.Adapter<PatientAdapter.MainViewHolder>() {

    inner class MainViewHolder(private val binding:PatientAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setView(sample: PatientModelClass) {
            binding.name.text = sample.patient_name
            binding.id.text = sample.patient_id
            binding.age.text=sample.age
            binding.gender.text=sample.gender


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            PatientAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val sample = differ.currentList[position]
        holder.setView(sample)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val callBack = object : DiffUtil.ItemCallback<PatientModelClass>() {
        override fun areItemsTheSame(oldItem: PatientModelClass, newItem: PatientModelClass): Boolean {
            return newItem.patient_name == oldItem.patient_name
        }

        override fun areContentsTheSame(oldItem: PatientModelClass, newItem: PatientModelClass): Boolean {
            return newItem == oldItem
        }

    }
    val differ = AsyncListDiffer(this, callBack)
}