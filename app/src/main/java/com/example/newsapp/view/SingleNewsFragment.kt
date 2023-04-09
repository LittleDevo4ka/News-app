package com.example.newsapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSingleNewsBinding
import com.example.newsapp.databinding.NewsCardBinding
import com.example.newsapp.viewModel.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SingleNewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SingleNewsFragment : Fragment() {

    private lateinit var binding: FragmentSingleNewsBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSingleNewsBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.getNewsId() == -1) {
            binding.singleNewsTitle.text = "Choose some news"
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getNewsById().collect{
                    Glide.with(requireContext()).load(it.urlToImage)
                        .into(binding.singleNewsImage)

                    binding.singleNewsTitle.text = it.title
                    binding.singleNewsAuthor.text = it.author
                    binding.singleNewsContent.text = it.description
                }
            }
        }
    }
}