package com.test.bonsaisearch

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import cafe.adriel.bonsai.core.Bonsai
import cafe.adriel.bonsai.core.BonsaiStyle
import cafe.adriel.bonsai.core.node.Leaf
import cafe.adriel.bonsai.core.tree.Tree
import cafe.adriel.bonsai.core.tree.TreeScope
import com.test.bonsaisearch.ui.theme.BonsaiSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BonsaiSearchTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    CategoryLayout(SSOTModel())
                }
            }
        }
    }
}

class SSOTModel : ViewModel() { //Single Source of Truth
    private val _categories = mutableStateListOf<String>()
    private val categories: List<String> = _categories

    private val _filteredCategories = mutableStateListOf<String>()
    val filteredCategories: List<String> = _filteredCategories

    init {
        _categories.addAll(listOf("aa", "aaa", "aaaa" , "aaaaa", "aaaaaa", "aaaaaaa"))
        _filteredCategories.addAll(categories)
    }

    sealed class UIEvent {
        data class SearchChanged(val searchText: String): UIEvent()
        //object Submit: UIEvent()
    }

    fun onEvent(event: UIEvent) {
        when(event) {
            is UIEvent.SearchChanged -> {
                _filteredCategories.clear() //clear the list
                _filteredCategories.addAll(categories.filter { s -> s.lowercase().contains(event.searchText.lowercase(), ignoreCase = true) }) //add the new values
            }
        }
    }
}

@Composable
fun CategoryLayout(
    viewModel: SSOTModel
) {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    var tree = ComposeFilteredTree(viewModel.filteredCategories)

    val searchedText = textState.value.text
    if (searchedText.isNotEmpty() && searchedText.length >= 2) {
        viewModel.onEvent(SSOTModel.UIEvent.SearchChanged(searchedText))
    }

    Column {
        SearchView(textState)
        CategoryTree(tree)
    }
}

@Composable
fun ComposeFilteredTree(categories: List<String>): Tree<String> =
    Tree {
        GetLeaves(categories = categories)
    }

@Composable
fun TreeScope.GetLeaves(categories: List<String>) {
    categories.forEach {
        Leaf(
            content = it,
            customName = null,
            name = it
        )
        Log.d(
            "MyDebug",
            "composeFilteredTree:${it}"
        )
    }
}

@Composable
fun CategoryTree(tree: Tree<String>) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        Bonsai(
            tree = tree,
            style = BonsaiStyle(
                toggleIconRotationDegrees = 0f,
                toggleIconColorFilter = ColorFilter.tint(Color.Gray),
                nodeIconSize = 64.dp,
                nodeNameTextStyle = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.W800,
                )
            ),
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 4.dp)
                .padding(start = 0.dp)

        )
    }
}

@Composable
fun SearchView(state: MutableState<TextFieldValue>) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value =
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = colorResource(id = R.color.black),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}


@Composable
private fun EmojiIcon(emoji: String) {
    Text(emoji)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BonsaiSearchTheme {
        CategoryLayout(SSOTModel())
    }
}