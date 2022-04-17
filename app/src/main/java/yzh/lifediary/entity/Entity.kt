package yzh.lifediary.entity



import com.yzh.myjson.SerializedName
import java.io.Serializable

data class User(val id:Int,@SerializedName("username") val account:Int, @SerializedName("accountNonLocked") val noLock:Boolean,
                @SerializedName("name") var username:String, var iconPath: String) :Serializable
data class UserResponse(val message: String, val code: Int, val data: User)


data class LoginResponse(val code: Int, val message: String)

data class DiaryImage(val diaryId: Int, val id: Int, val main: Boolean, val path: String)

data class DiaryDetailsResponse(val code: Int, val message: String, val data: DiaryDetails)

data class DiaryDetails(val diary: Diary,val images:List<DiaryImage>)

data class Diary(val id: Int,val title:String,val content: String,val userId: Int,val likeCount: Int,val date: String)

data class DiaryResponse(val code: Int, val message: String, val data:Diary)

data class DiaryItem(val id: Int, val isLike:Boolean, val likeCount:Int, val userIcon:String, val mainPic:String, val userId:Int, var title: String, val username: String) :Serializable

data class ItemResponse(val message: String,val code: Int,val data: List<DiaryItem>)