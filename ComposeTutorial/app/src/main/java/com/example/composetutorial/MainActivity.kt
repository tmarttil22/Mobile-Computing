package com.example.composetutorial

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
//import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.composetutorial.data.Picture
import com.example.composetutorial.data.User
import com.example.composetutorial.data.UserDatabase
import com.example.composetutorial.data.UserRepository
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    /* val db = Room.databaseBuilder(
        applicationContext,
        UserDatabase::class.java, "database-name"
    ).build() */


    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(UserDatabase.getDataBase(this).userDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {//
        super.onCreate(savedInstanceState)

        val database = UserDatabase.getDataBase(this)
        val userDao = database.userDao()
        val repo = UserRepository(database.userDao())
        val viewModelFactory = UserViewModelFactory(repo)

        setContent {
            ComposeTutorialTheme {
                val navController = rememberNavController()

                /* https://www.youtube.com/watch?v=jt5sJEnDsSQ used as a guide, structure was followed closely but not exactly and
                adapted to fit the app structure i built. The android dev pages were also used as guidance, but less than the video */
                NavHost(
                    navController = navController,
                    startDestination = Chat
                ) {
                    composable<Profile> {
                        ProfileScreen(
                            onNavigateToChat = {
                                navController.navigate(route = Chat)
                                {
                                    popUpTo(route = Chat) {
                                        inclusive = true
                                    }
                                }
                            }, onNavigateToPrivateMessage = {
                                navController.navigate(route = PrivateMessage)
                                {
                                    popUpTo(route = PrivateMessage) {
                                        inclusive = true
                                    }
                                }
                            }, userViewModel = userViewModel
                        )
                    }
                    composable<Chat> {
                        ChatScreen(
                            onNavigateToProfile = {navController.navigate(route = Profile)
                            }, userViewModel = userViewModel

                        )
                    }
                    composable<PrivateMessage> {
                        PrivateMessageScreen(
                            onNavigateToProfile = {navController.navigate(route = Profile)
                            {
                                popUpTo(route = Profile) {
                                    inclusive = true
                                }
                            }}
                        )
                    }
            }
        }
    }
}

data class Message(val author: String?, val body: String)

@Serializable
object Profile
@Serializable
object Chat
@Serializable
object PrivateMessage

@Composable
fun ProfileScreen(onNavigateToChat: () -> Unit, onNavigateToPrivateMessage: () -> Unit, userViewModel: UserViewModel) {

    val picture by userViewModel.pictureFlow.collectAsState()
    var userString by remember { mutableStateOf("Pearson") }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            userViewModel.insertPicture(Picture(uid = 1, profileImage = uri.toString()))
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Row(modifier = Modifier.padding(all = 14.dp)) {

        AsyncImage(
            model = picture?.profileImage,
            contentDescription = "Profile picture",
            modifier = Modifier.size(100.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(14.dp))

            TextField(
                value = userString,
                onValueChange = {userString = it},
                label = { Text("Username")}
            )
            userViewModel.insertUser(User(uid = 1, username = userString))


            Spacer(modifier = Modifier.height(13.dp))

            Spacer(modifier = Modifier.height(14.dp))
            FilledTonalButton(onClick = onNavigateToPrivateMessage) {
                Text(text = "Open account settings")
            }

            Spacer(modifier = Modifier.height(14.dp))
            FilledTonalButton(onClick = {
                pickMedia.launch(PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                ))

            }
            )
            {
                Text(text = "Pick profile picture")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Button(onClick = onNavigateToChat) {
                Text(text = "Go back")
            }
        }
    }

}

@Composable
fun ChatScreen(onNavigateToProfile: () -> Unit, userViewModel: UserViewModel) {
    val user by userViewModel.userFlow.collectAsState()

    val messageList: List<Message> = listOf(Message(user?.username,"I really really like milk"),
        Message("Milk_lover_4", "I love milk WAY more than you!!!"),
        Message(user?.username, "It's not a competition man :))"),
        Message("Pepsi fanatic", "DRINK PEPSI GUYS, its clearly the most superior option since the taste is so heavenful. You might almost think that it's gods most preferred drink."),
        Message(user?.username, "Pepsi max > Pepsi"),
        Message("Milk_lover_4", "blasphemous thread, shame on you"),
        Message("EldenRingFan16", "Yo is the Bloodhound Fang a good weapon??"),
        Message(user?.username, "Wrong thread buddy boy but yeah its solid"),
        Message("FordF150", "I mean this COULD be made into an elden ring thread, why not"),
        Message("Milk_lover_4", "ABSOLUTELY NOT, THIS IS FOR MILK ONLY"),
        Message(user?.username, "I could honestly switch convo this to elden ring too"),
        Message(user?.username, "Have any of you beat Malenia?"),
        Message("EldenRingFan16", "I haven't got to that point in the game yet"),
        Message("FordF150", "Oh yeah i beat him after maybe 3 hours, that fight is TOUGH"),
        Message("Milk_Lover_4", "Is there milk in elden ring"),
        Message("Pepsi fanatic", "Is there pepsi in elden ring??"),
        Message(user?.username, "Theres different types of drinks, is it good enough??"),
        )

    Conversation(messageList, userViewModel = userViewModel)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopEnd)) {
            Button(onClick = onNavigateToProfile) {
                Text(text = "Open your profile")
            }
        }
    }
}

@Composable
fun PrivateMessageScreen(onNavigateToProfile: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopEnd)) {
            Button(onClick = onNavigateToProfile) {
                Text(text = "Return to profile")
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(text = "No implementation")
        }
    }
}

@Composable
fun MessageCard(msg: Message, userViewModel: UserViewModel) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        /* Image(
            painter = painterResource(R.drawable.milk_glass),
            contentDescription = "A mighty glass of milk",
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        ) */

        val picture by userViewModel.pictureFlow.collectAsState()

        AsyncImage(
            //model = picture?.profileImage,
            model = ImageRequest.Builder(LocalContext.current)
                .data(picture?.profileImage)
                .build(),
            contentDescription = "does this matter",
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
            placeholder = painterResource(R.drawable.milk_glass)
        )

         Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }

        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.inversePrimary
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            msg.author?.let {
                Text(text = it, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(2.dp))

            Surface(shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier.animateContentSize().padding(1.dp)) {

                Text(text = msg.body
                    , style = MaterialTheme.typography.bodySmall
                    , modifier = Modifier.padding(all = 4.dp)
                    , maxLines = if (isExpanded) Int.MAX_VALUE else 1
                )
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>, userViewModel: UserViewModel) {
    LazyColumn {
        items(messages) {
            message -> MessageCard(message, userViewModel)
        }
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)


@Composable
fun PreviewMessageCard() {
    ComposeTutorialTheme {
        Surface {
            //MessageCard(msg = Message("Lexi", "Hey, take a look at Jetpack Compose, it's great!"))
        }
    }
}
}