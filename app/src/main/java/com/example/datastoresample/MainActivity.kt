package com.example.datastoresample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.datastoresample.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "myPref")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveBtn.setOnClickListener {
            // datastore.edit() is a suspend function, needs to be run in a coroutine
            lifecycleScope.launchWhenStarted {
                dataStore.edit { myPref ->
                    // define a key and its type to be bound to or used to retrieve a value
                    // to clear the preference -> myPref.clear()
                    val prefKey = stringPreferencesKey(binding.saveKey.text.toString())
                    // to clear specific value -> myPref[prefKey] = null
                    myPref[prefKey] = binding.saveValue.text.toString()
                }
            }
        }

        binding.loadBtn.setOnClickListener {
            val prefKey = stringPreferencesKey(binding.loadKey.text.toString())
            // data: representation of the current data state of type flow. it's automatically initiated with the data store initiation
            // map: function that returns a flow of required results (it: Preferences object that can be used to retrieve values by key)
            val flow = dataStore.data.map { myPref ->
                myPref[prefKey] ?: "no value"
            }
            // being a flow, the retrieved preferences data should be collected in a coroutine
            lifecycleScope.launchWhenStarted {
                flow.collect{
                    binding.loadValue.text = it
                }
            }
        }
    }
}