package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.github.scribejava.apis.TwitterApi
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharCount: TextView

    lateinit var client: RestClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharCount = findViewById(R.id.tvCharCount)
        client = RestApplication.getRestClient(this)

        tvCharCount.text = "280"
        etCompose.addTextChangedListener{
            var charCount = 280 - etCompose.length();
            tvCharCount.text = charCount.toString()
        }

        btnTweet.setOnClickListener{
            //grab context in edit text (etCompose)
            val tweetContent = etCompose.text.toString()
            //1. Make sure tweet isn't empty

            if (tweetContent.isEmpty()){
                Toast.makeText(this, "Empty tweets are not allowed", Toast.LENGTH_SHORT).show()
                //look into SnackBar Message
            }
            //2. Make sure tweet is under character count
            else if (tweetContent.length > 280){
                Toast.makeText(this, "Tweet is too long must be 280 characters or shorter", Toast.LENGTH_SHORT).show()
            } else {
                //Make an api call to Twitter to publish tweet
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        //Send the tweet back to TimeLineActivity
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Tweet publish failed")
                    }
                })
            }
        }
    }

    companion object{
        val TAG = "ComposeActivity"
    }
}