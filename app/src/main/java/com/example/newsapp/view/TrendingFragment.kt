package com.example.newsapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentTrendingBinding
import com.example.newsapp.model.room.ShortNews
import com.example.newsapp.viewModel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch

class TrendingFragment : Fragment(), NewsRecyclerItem.onItemClickListener {

    private lateinit var binding: FragmentTrendingBinding
    private lateinit var viewModel: MainViewModel

    private val tag = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTrendingBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterList: MutableList<ShortNews> = mutableListOf()
        val myAdapter = NewsRecyclerItem(adapterList, requireContext(), this)

        binding.trendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.trendingRecyclerView.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getResponseCode().collect {
                    if (it != null) {
                        if (it != 200) {
                            Log.w(tag, "Something went wrong")
                            binding.refreshLayout.isRefreshing = false
                            Toast.makeText(context, "Oups! Something went wrong…\n" +
                                    "Check yout internet connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllTopNewsShort().collect {
                    if(it.isEmpty()) {
                        viewModel.updateTopNews()
                    } else {
                        binding.refreshLayout.isRefreshing = false
                        adapterList.clear()
                        adapterList.addAll(it)

                        myAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        binding.filtersTbTrending.setOnClickListener{
            if (binding.countryTextFieldTrending.isVisible) {
                binding.countryTextFieldTrending.visibility = View.GONE
                binding.categoryTextFieldTrending.visibility = View.GONE
                (binding.filtersTbTrending as MaterialButton).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_filters)
                binding.filtersTbTrending.text = "Show filters"
            } else {
                binding.countryTextFieldTrending.visibility = View.VISIBLE
                binding.categoryTextFieldTrending.visibility = View.VISIBLE
                (binding.filtersTbTrending as MaterialButton).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_filters_off)
                binding.filtersTbTrending.text = "Hide filters"
            }
        }

        (binding.countryTextFieldTrending.editText as MaterialAutoCompleteTextView)
            .setText(viewModel.getTrendingCountry(), false)
        (binding.categoryTextFieldTrending.editText as MaterialAutoCompleteTextView)
            .setText(viewModel.getTrendingCategory(), false)

        binding.refreshLayout.setOnRefreshListener {
            val country = binding.countryTextFieldTrending.editText?.text.toString()
            viewModel.setTrendingCountry(country)
            val category = binding.categoryTextFieldTrending.editText?.text.toString()
            viewModel.setTrendingCategory(category)
            viewModel.updateTopNews()
        }

        binding.updateTbTrending.setOnClickListener{
            if (binding.refreshLayout.isRefreshing) {
                Toast.makeText(requireContext(), "Wait until the update completes",
                Toast.LENGTH_SHORT).show()
            } else {
                binding.refreshLayout.isRefreshing = true

                val country = binding.countryTextFieldTrending.editText?.text.toString()
                viewModel.setTrendingCountry(country)
                val category = binding.categoryTextFieldTrending.editText?.text.toString()
                viewModel.setTrendingCategory(category)
                viewModel.updateTopNews()
            }
        }

    }

    override fun onItemClick(position: Int) {
        if (binding.refreshLayout.isRefreshing) {
            Toast.makeText(context, "Wait until the update completes",
                Toast.LENGTH_SHORT).show()
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllTopNewsShort().collect{
                    viewModel.saveNews(false, it[position].id)
                    (activity as MainActivity).setSingleNewsFragment()
                }
            }
        }



    }

}