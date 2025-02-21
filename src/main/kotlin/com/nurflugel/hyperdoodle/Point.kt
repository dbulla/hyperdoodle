package com.nurflugel.hyperdoodle


/**
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 26, 2007 Time: 5:54:38 PM To change this template use File | Settings | File Templates.
 */
data class Point(var x: Double, var y: Double) {
    constructor(xx: Int, yy: Int) : this(xx.toDouble(), yy.toDouble())
}


