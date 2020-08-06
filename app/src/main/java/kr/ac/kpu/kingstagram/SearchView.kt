package kr.ac.kpu.kingstagram

class SearchView {
    var profileUrl: String
    var nickname: String
    var name: String

    init{
        profileUrl = ""
        nickname = ""
        name = ""
    }

    constructor(profile_url: String, nickname: String, name: String) {
        this.profileUrl = profile_url
        this.nickname = nickname
        this.name = name
    }
}