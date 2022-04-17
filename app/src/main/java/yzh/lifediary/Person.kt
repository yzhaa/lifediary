package yzh.lifediary

import com.yzh.myjson.Expose
import com.yzh.myjson.Gson
import com.yzh.myjson.SerializedName
import com.yzh.myjson.TypeToken

import yzh.lifediary.entity.User
import yzh.lifediary.entity.UserResponse


data class Person(
    @com.yzh.myjson.Expose(serialize = true, deserialize = true) val name: String,
    @com.yzh.myjson.SerializedName("real-age") val age: Int,
    val list: List<Person>?
)


fun main() {
//    val s = com.yzh.myjson.Gson.getGson().toJson(
//        UserResponse(
//            "hh",
//            123,
//            User(1, 123, true, "qq",  Image(1, "/pic/test.png"))
//
//        )
//    )
//
//    val userResponse = com.yzh.myjson.Gson.getGson()
//        .fromJson<UserResponse>(s,
//            object : com.yzh.myjson.TypeToken<UserResponse>() {}.type
//        )


}