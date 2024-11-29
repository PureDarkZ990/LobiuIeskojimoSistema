
import com.example.lobiupaieskossistema.models.User

data class Group(
    val id: Int,
    val name: String,
    val users: List<User>
)