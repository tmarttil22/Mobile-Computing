package com.example.composetutorial

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log

// API imports
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlinx.coroutines.runBlocking
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.composetutorial.data.Msg
import com.example.composetutorial.data.Picture
import com.example.composetutorial.data.User
import com.example.composetutorial.data.UserDatabase
import com.example.composetutorial.data.UserRepository
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var notificationService: NotificationService

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(UserDatabase.getDataBase(this).userDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {//
        super.onCreate(savedInstanceState)

        notificationService = NotificationService(this)

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
                                navController.navigate(route = SensorData)
                                {
                                    popUpTo(route = SensorData) {
                                        inclusive = true
                                    }
                                }
                            }, userViewModel = userViewModel,
                            onRequestPermission = { checkAndRequestNotificationPermission() },
                        )
                    }
                    composable<Chat> {
                        ChatScreen(
                            onNavigateToProfile = {navController.navigate(route = Profile)
                            }, userViewModel = userViewModel

                        )
                    }
                    composable<SensorData> {
                        SensorDataScreen(
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
object SensorData

@Composable
fun ProfileScreen(onNavigateToChat: () -> Unit, onNavigateToPrivateMessage: () -> Unit, userViewModel: UserViewModel,
                  onRequestPermission: () -> Unit, apiViewModel: APIViewModel = viewModel()
) {

    val picture by userViewModel.pictureFlow.collectAsState()
    val username by userViewModel.userFlow.collectAsState()
    var userString by remember { mutableStateOf(username?.username) }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        // Callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            userViewModel.insertPicture(Picture(uid = 1, profileImage = uri.toString()))
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    val dogViewModel: APIViewModel by viewModels()


    Row(modifier = Modifier.padding(all = 14.dp)) {

        AsyncImage(
            model = picture?.profileImage,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(14.dp))

            if (userString == null) {
                userString = "Pearson"
            }

            userString?.let {
                TextField(
                    value = it,
                    onValueChange = {userString = it},
                    label = { Text("Username")}
                )
            }
            userString?.let { User(uid = 1, username = it) }?.let { userViewModel.insertUser(it) }


            Spacer(modifier = Modifier.height(13.dp))

            Spacer(modifier = Modifier.height(14.dp))
            FilledTonalButton(onClick = onNavigateToPrivateMessage) {
                Text(text = "Observe sensor data")
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
            Spacer(modifier = Modifier.height(14.dp))
            FilledTonalButton(onClick = {onRequestPermission() }
            ) {
                Text(text = "Apply notifications")
            }

            Spacer(modifier = Modifier.height(14.dp))
            FilledTonalButton(onClick = {
                dogViewModel.fetchRandomDog()
            }
            ) {
                Text(text = "Get a cat picture (API)")
            }
        }
    }
    val imageUrl by dogViewModel.dogImageUrl.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Spacer(modifier = Modifier.height(180.dp))
            imageUrl?.let { url ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cat!",
                    modifier = Modifier.size(300.dp)
                )
            }
        }
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Button(onClick = onNavigateToChat) {
                Text(text = "Go back")
            }
        }
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            Button(onClick = {openMapsActivity()}) {
                Text(text = "Maps")
            }
        }
    }
}

    private fun openMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

@Composable
fun ChatScreen(onNavigateToProfile: () -> Unit, userViewModel: UserViewModel) {
    val user by userViewModel.userFlow.collectAsState()

    /*val messageList: List<Message> = listOf(
        Message(user?.username, "I really really like milk"),
        Message("Milk_lover_4", "I love milk WAY more than you!!!"),
        Message(user?.username, "It's not a competition man :))"),
        Message(
            "Pepsi fanatic",
            "DRINK PEPSI GUYS, its clearly the most superior option since the taste is so heavenful. You might almost think that it's gods most preferred drink."
        ),
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
    )*/

    val messageList by userViewModel.messageList.collectAsState()

    val mainMessageList = mutableListOf<Message>()

    for (msg in messageList) {
        if (msg != null) {
            mainMessageList.add(Message(user?.username, msg.msg))
        }
    }

    Conversation(mainMessageList, userViewModel = userViewModel)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopEnd)) {
            Button(onClick = onNavigateToProfile) {
                Text(text = "Open your profile")
            }
        }
        var text by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(value = text,
                    onValueChange = {text = it},
                    modifier = Modifier.weight(0.75f)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            userViewModel.insertMessage(Msg(msg = text))
                            text = ""
                        }
                    }, modifier = Modifier.weight(0.25f)
                ) {
                    Text("Send")
                }
            }
        }
    }
}

    @Composable
    fun SensorDataScreen(onNavigateToProfile: () -> Unit) {
        val sensorViewModel: SensorViewModel by viewModels()
        val gyroData by sensorViewModel.gyroData.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.TopEnd)) {
                Button(onClick = onNavigateToProfile) {
                    Text(text = "Return to profile")
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                if (gyroData.isNotEmpty()) {
                    Text(text = "X-Axis: " + gyroData[0].toString() + "\n"
                            + "Y-Axis: " + gyroData[1].toString() + "\n"
                            + "Z-Axis: " + gyroData[2].toString() + "\n")
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                sendNotification()
            }
        } else {
            sendNotification()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                sendNotification()
            }
        }

    private fun sendNotification() {
        notificationService.showNotification()
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
            modifier = Modifier
                .size(40.dp)
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
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)) {

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