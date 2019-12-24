package com.ndp.server.utils

import com.ndp.server.bean.Chart

object Miscellaneous {

    fun parseChartMap(map: Map<String, Int>): Chart {
        val labels = ArrayList<String>()
        val count = ArrayList<Int>()
        for (k in map.keys) {
            labels.add(k)
            count.add(map[k] ?: 0)
        }
        return Chart(labels, count)
    }
}