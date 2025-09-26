package com.israeljuarez.sikacorekmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform