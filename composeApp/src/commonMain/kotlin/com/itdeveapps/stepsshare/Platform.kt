package com.itdeveapps.stepsshare

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform