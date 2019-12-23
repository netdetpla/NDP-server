package com.ndp.server.bean

data class Chart(val labels: List<String>, val data: List<Int>)

data class ChartsJson(val port: Chart, val service: Chart)