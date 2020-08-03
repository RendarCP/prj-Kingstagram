package kr.ac.kpu.kingstagram

import android.graphics.drawable.Drawable
import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PostView() {
    var comments: Map<String, String> = HashMap()
    var content: String
    var imageUrl: String
    var like: Int
    var kings: Map<String, Boolean> = HashMap()
    var tag: ArrayList<String> = arrayListOf()
    var timestamp: Timestamp
    var uid : String
    var userId: String

    init{
        this.comments = comments
        this.content = ""
        this.imageUrl = ""
        this.like = 0
        this.kings = kings
        this.timestamp = Timestamp(Date())
        this.uid = ""
        this.userId = ""
    }
    constructor(comments: Map<String, String>, content: String, image: String, like: Int, kings: Map<String, Boolean>, tag: ArrayList<String>, timestamp: Timestamp, uid: String, userId: String) : this() {
        this.comments = comments
        this.content =content
        this.imageUrl = image
        this.kings = kings
        this.like = like
        this.tag = tag
        this.timestamp = timestamp
        this.uid = uid
        this.userId = userId
    }
}