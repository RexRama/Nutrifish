package com.org.capstone.nutrifish.ui.main.detail.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.org.capstone.nutrifish.R
import com.org.capstone.nutrifish.data.remote.response.ListStoryItem
import com.org.capstone.nutrifish.databinding.FragmentDetailPostBinding
import com.org.capstone.nutrifish.ui.main.home.HomeFragment

class DetailPostFragment : Fragment() {
    private var _binding: FragmentDetailPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPostBinding.inflate(inflater, container, false)
        val view = binding.root

        hideView()

        getDetail()

        return view
    }

    private fun hideView() {
        val hideFab = requireActivity().findViewById<FloatingActionButton>(R.id.fab_postRecipe)
        hideFab.visibility = View.GONE
        val pageTitle = requireActivity().findViewById<TextView>(R.id.page_title)
        pageTitle.visibility = View.VISIBLE
        "Post".also { pageTitle.text = it }
        val hideBottomNavigation =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navbar)
        hideBottomNavigation.visibility = View.GONE
        val hideScan = requireActivity().findViewById<FloatingActionButton>(R.id.bt_scan)
        hideScan.visibility = View.GONE
        val titleApp = requireActivity().findViewById<TextView>(R.id.top_title)
        "".also { titleApp.text = it }
    }

    @Suppress("DEPRECATION")
    private fun getDetail() {
        val homeData: ListStoryItem? = arguments?.getParcelable(HomeFragment.RECIPE_ITEM)
        setDetails(homeData)

    }

    private fun setDetails(homeData: ListStoryItem?) {
        requireActivity().runOnUiThread {
            if (homeData != null) {
                val recipeTitle = homeData.storyTitle
                val recipeUsername = homeData.username.toString()
                val recipeDescription = homeData.storyDescription
                val recipeDate = homeData.storyDateCreated.toString()
                val recipePhoto = homeData.storyPhotoUrl

                with(binding) {
                    tvRecipeTitle.text = recipeTitle
                    tvUsername.text =
                        if (recipeUsername.contains("@")) "Oleh: @" + recipeUsername.substringBefore("@") else "Oleh: @$recipeUsername"
                    descriptionBody.text = recipeDescription
                    datePosted.text = if (recipeDate.contains(",")) recipeDate.substringBefore(",") else recipeDate
                    Glide.with(requireContext().applicationContext)
                        .load(recipePhoto)
                        .into(ivImageRecipePlaceholder)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


