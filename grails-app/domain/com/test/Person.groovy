package com.test


import groovy.transform.ToString

@ToString(includePackage = false, includes = 'username')
class Person {

    String username

    static mapping = {
        version false
        cache true //启用缓存
//        id generator: 'assigned'
    }

    static constraints = {
//        id bindable: true
    }
}
