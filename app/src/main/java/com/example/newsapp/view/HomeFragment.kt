package com.example.newsapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.example.newsapp.model.room.HomeNews
import com.example.newsapp.viewModel.MainViewModel
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

        val adapterList: MutableList<HomeNews> = mutableListOf()
        val myAdapter = NewsRecyclerItem(adapterList, requireContext(), this)

        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = myAdapter

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
                viewModel.getAllHomeTopNews().collect {
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

        binding.refreshLayout.setOnRefreshListener {
            viewModel.updateTopNews()
        }

    }

    override fun onItemClick(position: Int) {
        if (binding.refreshLayout.isRefreshing) {
            Toast.makeText(context, "Wait until the update completes",
                Toast.LENGTH_SHORT).show()
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllHomeTopNews().collect{
                    viewModel.setNewsId(it[position].id)
                    (activity as MainActivity).setSingleNewsFragment()
                }
            }
        }



    }
}