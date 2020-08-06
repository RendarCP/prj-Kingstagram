package kr.ac.kpu.kingstagram

class SearchView {
    var profile_url: String
    var nickname: String
    var name: String

    init{
        profile_url = ""
        nickname = ""
        name = ""
    }

    constructor(profile_url: String, nickname: String, name: String) {
        this.profile_url = profile_url
        this.nickname = nickname
        this.name = name
    }
}