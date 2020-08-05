package kr.ac.kpu.kingstagram

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