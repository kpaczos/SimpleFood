package com.example.simplefood.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.simplefood.R
import com.example.simplefood.databinding.ActivityMealBinding
import com.example.simplefood.db.MealDatabase
import com.example.simplefood.fragments.HomeFragment
import com.example.simplefood.model.Meal
import com.example.simplefood.viewmodel.MealViewModel
import com.example.simplefood.viewmodel.MealViewModelFactory

class MealActivity : AppCompatActivity() {
    private lateinit var mealId:String
    private lateinit var mealName:String
    private lateinit var mealThumb:String
    private lateinit var binding:ActivityMealBinding
    private lateinit var mealMvvm:MealViewModel
    private lateinit var ytLink:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealDatabase = MealDatabase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        mealMvvm = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]

        getMealInformationFromIntent()
        setInformationInViews()
        loadingCase()
        mealMvvm.getMealDetail(mealId)
        observeMealDetailsLiveData()
        onYTImageClick()
        onFavoriteCLick()
    }

    private fun onFavoriteCLick() {
        binding.btmFavorites.setOnClickListener{
            mealToSave?.let {
                mealMvvm.insertMeal(it)
                Toast.makeText(this,"Meal save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var mealToSave:Meal?=null

    private fun onYTImageClick() {
        binding.imgYt.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ytLink))
            startActivity(intent)
        }
    }

    private fun observeMealDetailsLiveData() {
        mealMvvm.observeMealDetailLiveData().observe(this,object : Observer<Meal>{
            override fun onChanged(t: Meal?) {
                responseCase()
                val meal = t
                mealToSave=meal
                binding.tvCategory.text = "Category : ${meal!!.strCategory}"
                binding.tvArea.text = "Area : ${meal.strArea}"
                binding.tvInstructionsDetails.text = meal.strInstructions
                ytLink = meal.strYoutube!!
            }

        })
    }

    private fun setInformationInViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imgMealDetail)
        binding.collapsingToolbar.title = mealName
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
    }

    private fun getMealInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!
    }

    private fun loadingCase(){
        binding.progressBar.visibility = View.VISIBLE
        binding.btmFavorites.visibility = View.INVISIBLE
        binding.tvInstructions.visibility = View.INVISIBLE
        binding.tvCategory.visibility = View.INVISIBLE
        binding.tvArea.visibility = View.INVISIBLE
        binding.imgYt.visibility = View.INVISIBLE
    }
    private fun responseCase(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.btmFavorites.visibility = View.VISIBLE
        binding.tvInstructions.visibility = View.VISIBLE
        binding.tvCategory.visibility = View.VISIBLE
        binding.tvArea.visibility = View.VISIBLE
        binding.imgYt.visibility = View.VISIBLE
    }
}