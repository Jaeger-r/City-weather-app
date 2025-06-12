package com.example.weatherproject.activitys

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherproject.R
import com.example.weatherproject.db.WeatherRepository
import com.example.weatherproject.models.City
import com.example.weatherproject.WeatherViewModel
import com.example.weatherproject.WeatherViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CityListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CityAdapter
    private lateinit var etSearch: EditText
    private lateinit var btnCancelSearch: Button
    private lateinit var btnExit: Button
    private lateinit var fabAdd: Button
    private lateinit var emptyState: TextView
    private lateinit var viewModel: WeatherViewModel
    private var allCities: MutableList<City> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_list)

        initViews()
        setupRecyclerView()
        setupListeners()
        loadCityWeatherFromDb()
    }
    override fun onResume() {
        super.onResume()
        loadCityWeatherFromDb()
    }
    private fun loadCityWeatherFromDb() {
        val dbRepository = WeatherRepository(this)
        viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(application)
        ).get(WeatherViewModel::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            val cityNames = dbRepository.getAllCities()
            val deferredResults = cityNames.map { cityName ->
                async { viewModel.fetchCitySummary(cityName) }
            }
            val weatherResults = deferredResults.mapNotNull { it.await() }

            withContext(Dispatchers.Main) {
                allCities.clear()
                allCities.addAll(weatherResults)
                adapter.updateData(allCities)
                checkEmptyState()
            }
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewMemos)
        etSearch = findViewById(R.id.etSearch)
        btnCancelSearch = findViewById(R.id.btnCancelSearch)
        btnExit = findViewById(R.id.exitbutton)
        fabAdd = findViewById(R.id.fabAddMemo)
        emptyState = findViewById(R.id.emptyState)
    }

    private fun setupRecyclerView() {
        adapter = CityAdapter(allCities, onItemClick = { city ->
            navigateToWeatherDetail(city)
        }, onDeleteClick = { city ->
            removeCity(city)
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CityListActivity)
            addItemDecoration(DividerItemDecoration(
                this@CityListActivity,
                LinearLayoutManager.VERTICAL
            ).apply {
                setDrawable(resources.getDrawable(android.R.drawable.divider_horizontal_textfield, theme))
            })
            adapter = this@CityListActivity.adapter
        }
    }

    private fun navigateToWeatherDetail(city: City) {
        val intent = Intent(this, WeatherActivity::class.java).apply {
            putExtra("NEW_CITY_DATA", city.position)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }

    private fun setupListeners() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }
        })

        btnCancelSearch.setOnClickListener {
            etSearch.text.clear()
            adapter.filter.filter("")
        }

        btnExit.setOnClickListener {
            finish()
        }

        fabAdd.setOnClickListener {
            addNewCity()
        }
    }

    private fun removeCity(city: City) {
        if (city.isCurrent) return

        lifecycleScope.launch(Dispatchers.IO) {
            val dbRepository = WeatherRepository(this@CityListActivity)
            dbRepository.deleteCity(city.position)  // 从数据库中删除

            withContext(Dispatchers.Main) {
                val position = allCities.indexOf(city)
                if (position != -1) {
                    allCities.removeAt(position)
                    adapter.updateData(allCities)
                    checkEmptyState()
                    Toast.makeText(this@CityListActivity, "已删除城市：${city.position}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val addCityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val city = result.data?.getSerializableExtra("NEW_CITY_DATA") as? City
                if (city != null) {
                    adapter.addCity(city)
                    Toast.makeText(this, "成功添加：${city.position}", Toast.LENGTH_SHORT).show()
                }
            }
        }


    private fun addNewCity() {
        val intent = Intent(this, AddCityActivity::class.java)
        addCityLauncher.launch(intent)
    }

    private fun checkEmptyState() {
        if (allCities.isEmpty()) {
            emptyState.text = "没有城市数据"
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    class CityAdapter(
        private var cities: MutableList<City>,
        private val onItemClick: (City) -> Unit,
        private val onDeleteClick: (City) -> Unit
    ) : RecyclerView.Adapter<CityAdapter.CityViewHolder>(), Filterable {

        private var filteredList: List<City> = cities.toList()

        inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvPosition: TextView = itemView.findViewById(R.id.position)
            val tvCleathe: TextView = itemView.findViewById(R.id.cleathe)
            val tvWendu: TextView = itemView.findViewById(R.id.wendu)
            val tvWeather: TextView = itemView.findViewById(R.id.weather)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_city, parent, false)
            return CityViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
            val city = filteredList[position]
            holder.tvPosition.text = city.position
            holder.tvCleathe.text = city.cleathe
            holder.tvWendu.text = city.wendu
            holder.tvWeather.text = city.weather

            holder.itemView.setOnClickListener {
                onItemClick(city)
            }

            holder.itemView.setOnLongClickListener {
                onDeleteClick(city)
                true
            }
        }

        override fun getItemCount(): Int = filteredList.size

        fun updateData(newCities: List<City>) {
            this.cities = newCities.toMutableList()
            this.filteredList = newCities.toList()
            notifyDataSetChanged()
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val searchString = constraint.toString().trim()
                    val results = if (searchString.isEmpty()) {
                        cities
                    } else {
                        cities.filter {
                            it.position.contains(searchString) ||
                                    it.weather.contains(searchString) ||
                                    it.cleathe.contains(searchString)
                        }
                    }

                    return FilterResults().apply {
                        values = results
                        count = results.size
                    }
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredList = results?.values as? List<City> ?: emptyList()
                    notifyDataSetChanged()
                }
            }
        }
        fun addCity(city: City) {
            cities.add(city)
            filteredList = cities.toList() // 确保 filteredList 跟着更新
            notifyItemInserted(filteredList.size - 1)
        }
    }
}