package com.example.studysample.core.domain.member

import com.example.studysample.core.domain.common.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val id: Long? = null,

    @Column(name = "email")
    val email: String,

    @Column(name = "nick_name")
    val nickName: String,

    @Column(name = "profile_image")
    val profileImage: String,

    @Column(name = "fcm_token")
    var fcmToken: String
) : BaseTimeEntity() {

    constructor() : this(null, "", "", "", "")
    constructor(email: String) : this(null, email, "", "", "")

    fun updateFcmToken(fcmToken: String) {
        this.fcmToken = fcmToken
    }

    companion object {
        fun create(email: String, nickName: String, profileImage: String, fcmToken: String): Member {
            return Member(
                email = email,
                nickName = nickName,
                profileImage = profileImage,
                fcmToken = fcmToken
            )
        }
    }
}
