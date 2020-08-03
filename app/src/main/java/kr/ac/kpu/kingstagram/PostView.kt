package kr.ac.kpu.kingstagram

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
    constructor(comments: Map<String, String>?, content: String, image: String, like: Int, kings: Map<String, Boolean>?, tag: ArrayList<String>?, timestamp: Timestamp, uid: String, userId: String) : this() {
        if (comments != null) {
            this.comments.plus(comments)
        }
        this.content =content
        this.imageUrl = image
        if (kings != null) {
            this.kings.plus(kings)
        }
        this.like = like
        if (tag != null) {
            for (i in tag)
            this.tag.add(i)
        }
        this.timestamp = timestamp
        this.uid = uid
        this.userId = userId
    }
}