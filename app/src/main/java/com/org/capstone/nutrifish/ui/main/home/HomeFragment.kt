package com.org.capstone.nutrifish.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.org.capstone.nutrifish.R
import com.org.capstone.nutrifish.adapter.AllStoriesAdapter
import com.org.capstone.nutrifish.adapter.CarouselAdapter
import com.org.capstone.nutrifish.adapter.LoadingAdapter
import com.org.capstone.nutrifish.data.local.entity.FishEntity
import com.org.capstone.nutrifish.data.remote.model.UserModel
import com.org.capstone.nutrifish.data.remote.response.ListStoryItem
import com.org.capstone.nutrifish.databinding.FragmentHomeBinding
import com.org.capstone.nutrifish.utils.SettingPreferences
import com.org.capstone.nutrifish.utils.Utils
import com.org.capstone.nutrifish.utils.ViewModelFactory
import com.org.capstone.nutrifish.utils.dataStore


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var storyAdapter: AllStoriesAdapter
    private lateinit var userModel: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        setUI()
        setStory()
        setViewModel()
        return view
    }

    private fun setUI() {
        with(requireActivity()) {
            findViewById<TextView>(R.id.page_title).visibility = View.GONE
            "NutriFish".also { findViewById<TextView>(R.id.top_title).text = it }
            findViewById<FloatingActionButton>(R.id.bt_scan).visibility = View.VISIBLE
            findViewById<FloatingActionButton>(R.id.fab_postRecipe).visibility = View.VISIBLE
            findViewById<BottomNavigationView>(R.id.bottom_navbar).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.bt_back).visibility = View.GONE
        }
    }

    private fun setViewModel() {

        val dataStore = SettingPreferences.getInstance(requireContext().dataStore)
        val viewModelFactory = ViewModelFactory(requireContext().applicationContext, dataStore)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        homeViewModel.getAllFish { fishList ->
            setCarousel(fishList)
        }

        homeViewModel.getUser().observe(viewLifecycleOwner) { user ->
            userModel = user
            setHome(userModel)
        }

        homeViewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        homeViewModel.fetchStories().observe(viewLifecycleOwner) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }

    }

    private fun setHome(userModel: UserModel) {
        requireActivity().runOnUiThread {
            val username = userModel.username.toString()
            binding.homeGreetings.text =
                if (username.contains("@")) "Hello, " + username.substringBefore("@") else "Hello, $username"
        }
    }

    private fun setStory() {
        storyAdapter = AllStoriesAdapter()
        binding.postRecipeRv.apply {
            layoutManager = LinearLayoutManager(requireContext().applicationContext)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingAdapter { storyAdapter.retry() }
            )

        }

        storyAdapter.addLoadStateListener { loadState ->
            showStoriesLoading(loadState)
        }

        storyAdapter.setOnItemClickCallback(object : Utils.OnItemClickCallback {
            override fun onFishClicked(data: FishEntity) {
                // Do nothing
            }

            override fun onPostClicked(data: ListStoryItem) {
                val showBack = requireActivity().findViewById<ImageButton>(R.id.bt_back)
                showBack.visibility = View.VISIBLE
                val bundle = Bundle().apply {
                    putParcelable(RECIPE_ITEM, data)
                }
                findNavController().navigate(
                    R.id.action_navigation_home_to_navigation_detailPost, bundle
                )
            }

        })


    }

    private fun setCarousel(fishList: List<FishEntity>) {
        activity?.runOnUiThread {
            binding.carouselRv.apply {
                layoutManager = LinearLayoutManager(
                    requireContext().applicationContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                carouselAdapter = CarouselAdapter(fishList).apply {
                    setOnItemClickCallback(object : Utils.OnItemClickCallback {

                        override fun onFishClicked(data: FishEntity) {
                            val showBack = requireActivity().findViewById<ImageButton>(R.id.bt_back)
                            showBack.visibility = View.VISIBLE
                            val bundle = Bundle().apply {
                                putString(FISH_NAME, data.fishName)
                            }
                            findNavController().navigate(
                                R.id.action_navigation_home_to_navigation_detailFish,
                                bundle
                            )
                        }

                        override fun onPostClicked(data: ListStoryItem) {
                            // do nothing
                        }

                    })
                }
            }
            binding.carouselRv.adapter = carouselAdapter
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.pbCarousel.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showStoriesLoading(loading: CombinedLoadStates) {
        binding.pbStories.visibility =
            if (loading.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE
    }

    companion object {
        const val FISH_NAME = "fish_name"
        const val RECIPE_ITEM = "recipe_item"
    }


}