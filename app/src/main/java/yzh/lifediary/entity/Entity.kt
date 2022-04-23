package yzh.lifediary.entity



import com.yzh.myjson.SerializedName
import java.io.Serializable

data class User(
    val id: Int,
    @SerializedName("username") val account: Int=-1,
    @SerializedName("accountNonLocked") val noLock: Boolean=false,
    @SerializedName("name") var username: String,
    var iconPath: String
) : Serializable

data class UserResponse(val message: String, val code: Int, val data: User)

data class FollowListResponse(val message: String, val code: Int, val data:MutableList<UserAndIsFollowOV>)


//不关心返回来的数据
data class MessageResponse(val code: Int, val message: String)

data class DiaryImage(val diaryId: Int, val id: Int, val main: Boolean, val path: String)

data class DiaryDetailsResponse(val code: Int, val message: String, val data: DiaryDetails)

data class Like(val id: Int, val dId: Int, val uId: Int)

data class LikeResponse(val code: Int, val message: String, val data: Like)

data class Follow(val id: Int, val yid: Int, val mid: Int)

data class FollowResponse(val code: Int, val message: String, val data: Follow)

data class DiaryDetails(val diary: Diary, val images: List<DiaryImage>)

data class Diary(
    val id: Int,
    val title: String,
    val content: String,
    val userId: Int,
    val likeCount: Int,
    val date: String
)

data class DiaryResponse(val code: Int, val message: String, val data: Diary)

data class DiaryItem(
    val id: Int,
    var isLike: Boolean,
    val likeCount: Int,
    val userIcon: String,
    val mainPic: String,
    val userId: Int,
    var title: String,
    val username: String,
    val date: String,
    val content: String
) : Serializable

data class UserAndIsFollowOV(val name:String,val id: Int, val iconPath: String,var follow:Boolean):Serializable

data class ItemResponse(val message: String, val code: Int, val data: MutableList<DiaryItem>)

data class SearchResultResponse(val message: String, val code: Int, val data:SearchResult)
data class SearchResult(val userFollowOVS: List<UserAndIsFollowOV>, val diarys: List<DiaryItem>) : Serializable

data class SearchResponse(val message: String, val code: Int, val data: List<String>)


