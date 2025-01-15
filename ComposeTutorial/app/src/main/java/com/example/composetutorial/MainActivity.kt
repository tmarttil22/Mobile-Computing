package com.example.composetutorial

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
//import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.composetutorial.ui.theme.ComposeTutorialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Message("", "")
        val messageList: List<Message> = listOf(Message("Pearson","I really really like milk"),
            Message("Milk_lover_4", "I love milk WAY more than you!!!"),
            Message("Pearson", "It's not a competition man :))"),
            Message("Pepsi fanatic", "DRINK PEPSI GUYS, its clearly the most superior option since the taste is so heavenful. You might almost think that it's gods most preferred drink."),
            Message("Pearson", "Pepsi max > Pepsi"),
            Message("Milk_lover_4", "blasphemous thread, shame on you"),
            Message("EldenRingFan16", "Yo is the Bloodhound Fang a good weapon??"),
            Message("Pearson", "Wrong thread buddy boy but yeah its solid"),
            Message("FordF150", "I mean this COULD be made into an elden ring thread, why not"),
            Message("Milk_lover_4", "ABSOLUTELY NOT, THIS IS FOR MILK ONLY"),
            Message("Pearson", "I could honestly switch convo this to elden ring too"),
            Message("Pearson", "Have any of you beat Malenia?"),
            Message("EldenRingFan16", "I haven't got to that point in the game yet"),
            Message("FordF150", "Oh yeah i beat him after maybe 3 hours, that fight is TOUGH"),
            Message("Milk_Lover_4", "Is there milk in elden ring"),
            Message("Pepsi fanatic", "Is there pepsi in elden ring??"),
            Message("Pearson", "Theres different types of drinks, is it good enough??"))

        setContent {
            ComposeTutorialTheme {
                Conversation(messageList)
            }
        }
    }
}

data class Message(val author: String, val body: String)

@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.milk_glass),
            contentDescription = "A mighty glass of milk",
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }

        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.inversePrimary
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(text = msg.author
                , color = MaterialTheme.colorScheme.secondary
                , style = MaterialTheme.typography.titleSmall
            )
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
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) {
            message -> MessageCard(message)
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
            MessageCard(msg = Message("Lexi", "Hey, take a look at Jetpack Compose, it's great!"))
        }
    }
}