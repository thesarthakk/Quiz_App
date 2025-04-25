package com.example.myquizapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myquizapp.R
import com.example.myquizapp.adapters.QuizAdapter
import com.example.myquizapp.models.Quiz
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var adapter: QuizAdapter
    lateinit var firestore: FirebaseFirestore
    private var quizList = mutableListOf<Quiz>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setUpViews()

    }

    fun setUpViews(){
        setUpFireStore()
        setUpDrawerLayout()
        setUpRecyclerView()
        setUpDatePicker()
    }

    private fun setUpDatePicker() {
        val btn1=findViewById<FloatingActionButton>(R.id.btnDatePicker)
        btn1.setOnClickListener {
            val datePicker=MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager,"DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                Log.d("DATEPICKER",datePicker.headerText)
                val dateFormatter=SimpleDateFormat("dd-MM-yyyy")
                val date=dateFormatter.format(Date(it))
                val intent=Intent(this,QuestionActivity::class.java)
                intent.putExtra("DATE",date)
                startActivity(intent)
            }
            datePicker.addOnNegativeButtonClickListener {
                Log.d("DATEPICKER",datePicker.headerText)

            }
            datePicker.addOnCancelListener {
                Log.d("DATEPICKER","Date Picker Cancelled")
            }
        }
    }

    private fun setUpFireStore() {
        firestore=FirebaseFirestore.getInstance()
        val collectionReference=firestore.collection("quizzes")
        collectionReference.addSnapshotListener { value, error ->
            if (value==null || error!=null){
                Toast.makeText(this,"Error fetching data",Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            Log.d("DATA",value.toObjects(Quiz::class.java).toString())
            quizList.clear()
            quizList.addAll(value.toObjects(Quiz::class.java))
            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclerView() {
        adapter= QuizAdapter(this, quizList)
        findViewById<RecyclerView>(R.id.quizRecyclerView).layoutManager =GridLayoutManager(this,2)
        findViewById<RecyclerView>(R.id.quizRecyclerView).adapter=adapter
    }

    fun setUpDrawerLayout(){
        val app=findViewById<MaterialToolbar>(R.id.appBar)
        setSupportActionBar(app)
        actionBarDrawerToggle=ActionBarDrawerToggle(this,findViewById(R.id.main),
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.syncState()
        val navigation=findViewById<com.google.android.material.navigation.NavigationView>(R.id.navigationView)
        navigation.setNavigationItemSelectedListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            val de=findViewById<DrawerLayout>(R.id.main)
            de.closeDrawers()
            true

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}