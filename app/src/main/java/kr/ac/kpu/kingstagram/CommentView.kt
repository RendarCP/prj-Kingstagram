package kr.ac.kpu.kingstagram

import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class CommentView {
    var nickname: String
    var comments: String

    init{
        nickname = ""
        comments = ""
    }

    constructor(nickname: String, comments: String) {
        this.nickname = nickname
        this.comments = comments
    }


}