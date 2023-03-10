package com.example.hw4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.hw4.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likCount = 5450,
            shareCount = 1250,
            countView = 4450
        )
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            textLikes.text = Calc.intToText(post.likCount)
            textShares.text = Calc.intToShareText(post.shareCount)
            textViews.text = Calc.intToViewText(post.countView)

            root.setOnClickListener {
                Log.d("stuff", "stuff")
            }
            avatar.setOnClickListener {
                Log.d("stuff", "avatar")
            }
            likes.setOnClickListener {
                Log.d("stuff", "like")
                post.likedByMe = !post.likedByMe
                likes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24
                    else R.drawable.ic_baseline_favorite_border_24
                )
                if (post.likedByMe) post.likCount++ else post.likCount--
                textLikes.text = Calc.intToText(post.likCount)
            }
            shares.setOnClickListener {
                post.shareCount++
                textShares.text = Calc.intToShareText(post.shareCount)
            }

        }

    }
}


