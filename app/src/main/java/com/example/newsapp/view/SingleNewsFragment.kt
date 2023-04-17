package com.example.newsapp.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.FragmentSingleNewsBinding
import com.example.newsapp.viewModel.MainViewModel
import kotlinx.coroutines.launch



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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getSavedNews()?.collect { article ->
                    Glide.with(requireContext()).load(article.urlToImage)
                        .into(binding.singleNewsImage)

                    binding.singleNewsTitle.text = article.title
                    binding.singleNewsAuthor.text = article.author
                    binding.singleNewsContent.text = article.description

                    if (article.url != null) {
                        binding.openArticleButtonSingleNews.visibility = View.VISIBLE
                        binding.openArticleButtonSingleNews.setOnClickListener {
                            val uriUrl: Uri = Uri.parse(article.url)
                            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
                            startActivity(launchBrowser)
                        }

                        binding.copyIbSingleNews.visibility = View.VISIBLE
                        binding.copyIbSingleNews.setOnClickListener{
                            val clipboard = requireContext()
                                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Link", article.url)
                            clipboard.setPrimaryClip(clip)

                            Toast.makeText(requireContext(), "Copied!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
}