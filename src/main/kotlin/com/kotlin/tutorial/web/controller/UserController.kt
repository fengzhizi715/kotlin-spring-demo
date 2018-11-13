package com.kotlin.tutorial.web.controller

import com.kotlin.tutorial.user.User
import com.kotlin.tutorial.user.service.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


/**
 * Created by tony on 2018/11/13.
 */
@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userService: IUserService

    @GetMapping("/getUser")
    fun getUser(@RequestParam("name") userName: String): User {

        return userService.getUser(userName)
    }

    @GetMapping("/createUser")
    fun getUser(@RequestParam("name") userName: String,@RequestParam("password") password: String) {

        userService.createUser(userName,password)
    }
}