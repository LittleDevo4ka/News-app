package com.example.newsapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.databinding.NewsCardBinding
import com.example.newsapp.model.retrofit.news.Article
import com.example.newsapp.model.room.ShortNews
import com.example.newsapp.viewModel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import kotlin.math.ceil

class HomeFragment : Fragment(), NewsRecyclerItem.onItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel

    private val tag = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterList: MutableList<ShortNews> = mutableListOf()
        val myAdapter = NewsRecyclerItem(adapterList, requireContext(), this)

        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = myAdapter

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
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getAllArticlesShort().collect {
                    if(it.isNotEmpty()) {
                        binding.refreshLayout.isRefreshing = false
                        adapterList.clear()
                        adapterList.addAll(it)

                        myAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        binding.searchEditTextHome.setText(viewModel.getSearchQuery())
        (binding.languageTextFieldHome.editText as MaterialAutoCompleteTextView)
            .setText(viewModel.getSearchLanguage(), false)
        (binding.sortByTextFieldHome.editText as MaterialAutoCompleteTextView)
            .setText(viewModel.getSortBy(), false)

        binding.refreshLayout.setOnRefreshListener {
            if (binding.searchEditTextHome.text.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a search query",
                    Toast.LENGTH_SHORT).show()
                binding.refreshLayout.isRefreshing = false
                return@setOnRefreshListener
            }

            val searchQuery = binding.searchTextFieldHome.editText?.text.toString()
            viewModel.setSearchQuery(searchQuery)
            val language = binding.languageTextFieldHome.editText?.text.toString()
            viewModel.setSearchLanguage(language)
            val sortBy = binding.sortByTextFieldHome.editText?.text.toString()
            viewModel.setTrendingCategory(sortBy)

            viewModel.findArticles()
        }

        binding.filtersTbHome.setOnClickListener{
            if (binding.languageTextFieldHome.isVisible) {
                binding.languageTextFieldHome.visibility = View.GONE
                binding.sortByTextFieldHome.visibility = View.GONE
                (binding.filtersTbHome as MaterialButton).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_filters)
                binding.filtersTbHome.text = "Show filters"
            } else {
                binding.languageTextFieldHome.visibility = View.VISIBLE
                binding.sortByTextFieldHome.visibility = View.VISIBLE
                (binding.filtersTbHome as MaterialButton).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_filters_off)
                binding.filtersTbHome.text = "Hide filters"
            }
        }

        binding.updateTbHome.setOnClickListener{
            if (binding.refreshLayout.isRefreshing) {
                Toast.makeText(requireContext(), "Wait until the update completes",
                    Toast.LENGTH_SHORT).show()
            } else if (binding.searchEditTextHome.text.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a search query",
                    Toast.LENGTH_SHORT).show()
            } else {
                binding.refreshLayout.isRefreshing = true

                val searchQuery = binding.searchTextFieldHome.editText?.text.toString()
                viewModel.setSearchQuery(searchQuery)
                val language = binding.languageTextFieldHome.editText?.text.toString()
                viewModel.setSearchLanguage(language)
                val sortBy = binding.sortByTextFieldHome.editText?.text.toString()
                viewModel.setSortBy(sortBy)

                viewModel.findArticles()
            }
        }



    }

    override fun onItemClick(position: Int) {
        if (binding.refreshLayout.isRefreshing) {
            Toast.makeText(context, "Wait until the update completes",
                Toast.LENGTH_SHORT).show()
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllArticlesShort().collect{
                    Log.e(tag, position.toString())
                    viewModel.saveNews(true, it[position].id)
                    (activity as MainActivity).setSingleNewsFragment()
                }
            }
        }



    }
}