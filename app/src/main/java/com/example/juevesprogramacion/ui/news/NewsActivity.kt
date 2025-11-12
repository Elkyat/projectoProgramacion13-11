package com.example.juevesprogramacion.ui.news

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.juevesprogramacion.R
import com.example.juevesprogramacion.data.local.UserSession
import com.example.juevesprogramacion.data.model.NewsResponse
import com.example.juevesprogramacion.data.network.RetrofitClient
import com.example.juevesprogramacion.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : AppCompatActivity() {

    private val apiKey = "2f9b976bea524be8f9798e281f15f724"
    private var currentPage = 1
    private var isLoading = false
    private lateinit var container: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var loadingDots: LinearLayout
    private lateinit var session: UserSession
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        session = UserSession(this)

        val userEmailText = findViewById<TextView>(R.id.userEmailText)
        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)

        val displayName = when {
            session.isGuest() -> "Anónimo"
            session.getEmail().isNullOrEmpty() -> "Desconocido"
            else -> session.getEmail()
        }
        userEmailText.text = displayName

        logoutButton.setOnClickListener {
            logoutAndReturnToLogin()
        }

        container = findViewById(R.id.newsContainer)
        scrollView = findViewById(R.id.scrollView)
        loadingDots = findViewById(R.id.loadingDots)

        loadNews(currentPage)
        setupInfiniteScroll()
    }

    private fun logoutAndReturnToLogin() {
        session.clearSession()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finishAffinity()
    }

    private fun loadNews(page: Int) {
        isLoading = true
        startLoadingAnimation()

        RetrofitClient.instance.getTopHeadlines(
            lang = "es",
            country = "ar",
            max = 10,
            token = apiKey
        ).enqueue(object : Callback<NewsResponse> {

            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                stopLoadingAnimation()

                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    if (articles.isEmpty()) {
                        Toast.makeText(this@NewsActivity, "No hay más noticias.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    for ((index, article) in articles.withIndex()) {
                        val item = LinearLayout(this@NewsActivity).apply {
                            orientation = LinearLayout.VERTICAL
                            layoutParams = ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply { bottomMargin = 48 }
                            setPadding(32, 32, 32, 32)
                            background = ContextCompat.getDrawable(this@NewsActivity, R.drawable.news_card_bg)
                            elevation = 8f
                            alpha = 0f
                            translationY = 50f
                        }

                        val imageContainer = FrameLayout(this@NewsActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                480
                            )
                        }

                        val imageView = ImageView(this@NewsActivity).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }

                        Glide.with(this@NewsActivity)
                            .load(article.image ?: R.drawable.placeholder_image)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imageView)

                        val sourceName = article.source?.name ?: ""

                        val tagView = TextView(this@NewsActivity).apply {
                            text = sourceName
                            setPadding(24, 8, 24, 8)
                            textSize = 14f
                            setTextColor(ContextCompat.getColor(this@NewsActivity, android.R.color.white))
                            background = ContextCompat.getDrawable(this@NewsActivity, R.drawable.tag_background)

                            val color = when {
                                sourceName.contains("TN", true) -> 0xFF2196F3.toInt()       // Azul
                                sourceName.contains("Clarin", true) -> 0xFFFF1744.toInt()   // Rojo
                                sourceName.contains("Infobae", true) -> 0xFFFFC107.toInt()  // Amarillo
                                sourceName.contains("DW", true) -> 0xFF0D47A1.toInt()       // Azul oscuro
                                sourceName.contains("La Nación", true) -> 0xFF2076D2.toInt() // Azul suave
                                else -> 0x80212121.toInt()                                  // Gris translúcido
                            }
                            background.setTint(color)

                            val params = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                marginStart = 24
                                topMargin = 24
                            }
                            layoutParams = params
                            visibility = if (sourceName.isNotEmpty()) View.VISIBLE else View.GONE
                        }

                        imageContainer.addView(imageView)
                        imageContainer.addView(tagView)

                        val titleView = TextView(this@NewsActivity).apply {
                            text = article.title ?: "(Sin título)"
                            textSize = 22f
                            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                            setPadding(20, 28, 20, 16)
                            setTextColor(ContextCompat.getColor(this@NewsActivity, android.R.color.white))
                        }

                        val descView = TextView(this@NewsActivity).apply {
                            text = article.description ?: "(Sin descripción)"
                            textSize = 15f
                            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
                            setPadding(20, 0, 20, 16)
                            setTextColor(ContextCompat.getColor(this@NewsActivity, android.R.color.darker_gray))
                        }

                        item.addView(imageContainer)
                        item.addView(titleView)
                        item.addView(descView)

                        // 🔹 Abrir la noticia al tocar
                        item.setOnClickListener {
                            val url = article.url
                            if (!url.isNullOrEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@NewsActivity, "No hay enlace disponible", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // Animación de aparición
                        item.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setStartDelay((index * 80).toLong())
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(500)
                            .start()

                        container.addView(item)
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(this@NewsActivity, "Error ${response.code()}: $errorBody", Toast.LENGTH_LONG).show()
                }

                isLoading = false
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                stopLoadingAnimation()
                Toast.makeText(this@NewsActivity, "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        })
    }

    private fun setupInfiniteScroll() {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = view.bottom - (scrollView.height + scrollView.scrollY)
            if (diff <= 0 && !isLoading) {
                currentPage++
                loadNews(currentPage)
            }
        }
    }

    private fun startLoadingAnimation() {
        loadingDots.visibility = View.VISIBLE
        val dots = listOf(
            findViewById<View>(R.id.dot1),
            findViewById<View>(R.id.dot2),
            findViewById<View>(R.id.dot3)
        )

        dots.forEachIndexed { index, dot ->
            val animator = ObjectAnimator.ofPropertyValuesHolder(
                dot,
                PropertyValuesHolder.ofFloat("alpha", 0.2f, 1f)
            ).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                startDelay = (index * 200).toLong()
            }
            animator.start()
        }
    }

    private fun stopLoadingAnimation() {
        loadingDots.visibility = View.GONE
    }
}
