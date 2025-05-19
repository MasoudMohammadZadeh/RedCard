package com.example.redcard

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redcard.data.UserDatabase
import com.example.redcard.data.UserEntity
import com.example.redcard.repository.UserRepository
import com.example.redcard.ui.theme.RedCardTheme
import com.example.redcard.viewmodel.UserViewModel
import com.example.redcard.viewmodel.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.redcard.model.Match
import com.example.redcard.model.StandingsResponse
import com.example.redcard.model.TableEntry
import com.example.redcard.viewmodel.LeagueStandingsUiState
import com.example.redcard.viewmodel.LiveScoreViewModel
import com.example.redcard.viewmodel.MatchesUiState
import com.example.redcard.viewmodel.StandingsViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}


@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val userViewModelFactory = UserViewModelFactory(UserRepository(UserDatabase.getInstance(context).userDao()))
//    val liveScoreViewModelFactory = LiveScoreViewModelFactory()
//    val standingsViewModelFactory = StandingsViewModelFactory()


    NavHost(navController = navController, startDestination = "liveScore") {
        composable("login") {
            SportsLoginCard(navController)
        }
//        composable("liveScore") {
//            LiveScoreScreen(viewModel(factory = liveScoreViewModelFactory), navController)
//        }
//        composable("explore") {
//            SearchScreen(viewModel(factory = liveScoreViewModelFactory), navController)
//        }
//        composable("standing") {
//            StandingsScreen(viewModel(factory = standingsViewModelFactory), navController)
//        }


        composable("liveScore") {
            LiveScoreScreen(viewModel() , navController) // navController
        }
        composable("explore") { SearchScreen( viewModel() ,navController) }
        composable("standing") { StandingsScreen(viewModel(),navController) }
        composable("goalDetail") { MatchDetailScreen(navController) }
        composable("standingDetail") { StandingsDetailScreen(navController) }
        composable("profile") {
            val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
            val currentUser by userViewModel.currentUser.collectAsStateWithLifecycle()
            if (currentUser == null) {
                SportsLoginCard(navController)
            } else {
                MyProfileScreen(navController, userViewModel)
            }
        }
        composable("myprofile") {
            MyProfileScreen(navController, viewModel(factory = userViewModelFactory))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsLoginCard(navController: NavController) {
    val loginSheetState = rememberModalBottomSheetState()
    val signUpSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showLoginBottomSheet by remember { mutableStateOf(false) }
    var showSignUpBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ImageContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f),
                imageModifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .padding(start = 16.dp)
            )

            Text(
                text = "Discover all\nabout sport",
                fontSize = 40.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                lineHeight = 48.sp
            )

            Text(
                text = "Search millions of jobs and get the inside scoop on companies.\nWait for what? Let’s get started!",
                fontSize = 14.sp,
                color = Color(0xFFC4C4C4),
                textAlign = TextAlign.Start
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            showLoginBottomSheet = true
                            loginSheetState.show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign in", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                TextButton(
                    onClick = {
                        scope.launch {
                            showSignUpBottomSheet = true
                            signUpSheetState.show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Sign Up", color = Color(0xFFC4C4C4), fontSize = 16.sp)
                }
            }
        }

        if (showLoginBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showLoginBottomSheet = false
                },
                sheetState = loginSheetState,
                containerColor = Color(0xFF2A2A2A),
                modifier = Modifier.fillMaxHeight(0.6f)
            ) {
                LoginForm(
                    showLoginBottomSheet = showLoginBottomSheet,
                    setShowLoginBottomSheet = { showLoginBottomSheet = it },
                    showSignUpBottomSheet = showSignUpBottomSheet,
                    setShowSignUpBottomSheet = { showSignUpBottomSheet = it },
                    loginSheetState = loginSheetState,
                    signUpSheetState = signUpSheetState,
                    scope = scope,
                    navController = navController
                )
            }
        }

        if (showSignUpBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSignUpBottomSheet = false
                },
                sheetState = signUpSheetState,
                containerColor = Color(0xFF2A2A2A),
                modifier = Modifier.fillMaxHeight(0.6f)
            ) {
                SignUpForm(navController,signUpSheetState, scope)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(
    showLoginBottomSheet: Boolean,
    setShowLoginBottomSheet: (Boolean) -> Unit,
    showSignUpBottomSheet: Boolean,
    setShowSignUpBottomSheet: (Boolean) -> Unit,
    loginSheetState: SheetState,
    signUpSheetState: SheetState,
    scope: CoroutineScope,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val userDao = remember { UserDatabase.getInstance(context).userDao() }
    val repository = remember { UserRepository(userDao) }
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }
    val userViewModel: UserViewModel =
        viewModel(factory = UserViewModelFactory(repository))

    val loginResult by userViewModel.loginResult.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()


    LaunchedEffect(loginResult) {
        loginResult?.let { user ->
            Toast.makeText(context, "Welcome, ${user.name}!", Toast.LENGTH_SHORT).show()
            scope.launch {
                loginSheetState.hide()
                navController.navigate("myprofile") {
                    popUpTo("login") { inclusive = true }

                    // popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
            userViewModel.clearMessages()
        }
    }
    errorMessage?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            userViewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color(0xFFC4C4C4)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.message),
                    contentDescription = "Email Icon",
                    tint = Color(0xFFC4C4C4),
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF222222), RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFFC4C4C4)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password),
                    contentDescription = "Password Icon",
                    tint = Color(0xFFC4C4C4),
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.show else R.drawable.hide
                        ),
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                        tint = Color(0xFFC4C4C4),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF222222), RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFA01B22),
                        uncheckedColor = Color(0xFFC4C4C4)
                    )
                )
                Text("Remember me", color = Color(0xFFC4C4C4), fontSize = 14.sp)
            }

            Text(
                text = "Forgot Password",
                color = Color(0xFFC4C4C4),
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* Handle forgot password */ }
            )
        }

        Button(
            onClick = { /* Handle sign in */
                userViewModel.login(email.trim(), password.trim())
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("SIGN IN", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have account?",
                color = Color(0xFFC4C4C4),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = " SIGN UP",
                color = Color(0xFFA01B22),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        scope.launch {
                            setShowLoginBottomSheet(false)
                            loginSheetState.hide()
                            setShowSignUpBottomSheet(true)
                            signUpSheetState.show()
                        }
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpForm(navController: NavController, signUpSheetState: SheetState, scope: CoroutineScope) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userDao = remember { UserDatabase.getInstance(context).userDao() }
    val repository = remember { UserRepository(userDao) }



    val userViewModel: UserViewModel =
        viewModel(factory = UserViewModelFactory(repository))


    val registerSuccess by userViewModel.registerSuccess.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            Toast.makeText(context, "Sign up successful! Please log in.", Toast.LENGTH_LONG).show()
            scope.launch {
                signUpSheetState.hide()
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
            userViewModel.clearMessages()
        }
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            userViewModel.clearMessages()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sign-Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color(0xFFC4C4C4)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.message),
                    contentDescription = "Email Icon",
                    tint = Color(0xFFC4C4C4),
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF222222), RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFFC4C4C4)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password),
                    contentDescription = "Password Icon",
                    tint = Color(0xFFC4C4C4),
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.show else R.drawable.hide
                        ),
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                        tint = Color(0xFFC4C4C4),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF222222), RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone number", color = Color(0xFFC4C4C4)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password),
                    contentDescription = "Phone Icon",
                    tint = Color(0xFFC4C4C4),
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF222222), RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Button(
            onClick = {
                val user = UserEntity(
                    name = email.substringBefore('@'),
                    email = email.trim(),
                    password = password.trim(),
                    phoneNumber = phone.trim()
                )
                userViewModel.registerUser(user)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

//mamad salad
@Composable
fun ImageContainer(modifier: Modifier = Modifier, imageModifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(277.dp)
                .height(277.dp)
                .clip(RoundedCornerShape(55.dp))
                .background(Color(0xFF292929))
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .align(Alignment.TopStart)
                .padding(top = 15.dp)
        )

        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .align(Alignment.BottomStart)
                .padding(top = 8.dp)
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFA01B22))
                .align(Alignment.CenterEnd)
                .padding(top = 8.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.playerr),
            contentDescription = "Player Image",
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MyProfileScreen2(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            UserRepository(UserDatabase.getInstance(LocalContext.current).userDao())
        )
    )
) {

    var selectedItem by remember { mutableStateOf(3) } // Default to "My Profile" tab
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }
    val currentUser by userViewModel.currentUser.collectAsStateWithLifecycle()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
        ) {
            // Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Center profile image and text
            ) {
                // Profile Image with Edit Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    // Profile Image (Centered)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.Gray) // Placeholder for image
                    ) {
                        // Replace with actual image if available
                        Image(painter = painterResource(id = R.drawable.profile1), contentDescription = "Profile Image", modifier = Modifier.fillMaxSize())
                    }
                    // Edit Icon with Gradient Background (Bottom Right of Profile Image)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Position at bottom-right
                            .size(20.dp)
                            .clip(CircleShape) // Circular shape for the button
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFE03F46), // #E03F46 at 40%
                                        Color(0xFFA01B22) // #A01B22 at 72%
                                    ),
                                    start = Offset(0f, 0f), // Top-left
                                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) // Bottom-right
                                )
                            )
                            .clickable(onClick = { /* Handle edit */ }), // Mimic IconButton behavior
                        // Slight offset for visibility
                        contentAlignment = Alignment.Center // Center the icon inside the Box
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit Profile",
                            tint = Color.White, // White tint for the icon to contrast with gradient
                            modifier = Modifier.size(10.dp) // Adjust icon size to fit within the button
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Space between image and text
                // Name and Tagline (Below Image)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Arash nazari",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#YWWK till the end",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                // My Profile Button
                Button(
                    onClick = { /* Handle profile action */ },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .widthIn(min = 150.dp, max = 200.dp), // Controls button width
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "My Profile",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Increased spacing for separation

            // Personal Details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Adjusted padding
                colors = CardDefaults.cardColors(containerColor = Color(0xFF292929)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column {
                    DetailRow(
                        icon = painterResource(id = R.drawable.profile),
                        label = "Name",
                        value = "Brian Imaneul",
                        onClick = { /* Handle edit name */ }
                    )
                    DetailRow(
                        icon = painterResource(id = R.drawable.message),
                        label = "Email",
                        value = "brians213@gmail.com",
                        onClick = { /* Handle edit email */ }
                    )
                    DetailRow(
                        icon = painterResource(id = R.drawable.call),
                        label = "Phone",
                        value = "+62 821 560 641",
                        onClick = { /* Handle edit phone */ }
                    )
                }
            }

            // Bottom padding to avoid overlap with navigation bar
            Spacer(modifier = Modifier.height(80.dp))
        }

        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = Color(0xFF292929),
            contentColor = Color.White
        ) {
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 0) Text(
                            "Home",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 0) Spacer(modifier = Modifier.height(8.dp))
                        if (selectedItem == 0) Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 0,
                onClick = {
                    if (currentRoute != "liveScore") {
                        navController.navigate("liveScore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 0
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 1) Text(
                            "Explore",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 1) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 1) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.discovery),
                            contentDescription = "Explore",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 1,
                onClick = {
                    if (currentRoute != "explore") {
                        navController.navigate("explore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 1
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 2) Text(
                            "Standing",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 2) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 2) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.chart),
                            contentDescription = "Standing",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 2,
                onClick = {
                    if (currentRoute != "standing") {
                        navController.navigate("standing") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 2
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 3) Text(
                            "My Profile",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 3) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 3) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "My Profile",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 3,
                onClick = {
                    if (currentRoute != "profile") {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 3
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// Reusable DetailRow composable for personal details
//user profile
@Composable
fun DetailRow(
    icon: Painter,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = "$label Icon",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Edit $label",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun StandingsScreen(standingsViewModel: StandingsViewModel = viewModel() , navController: NavHostController){
    var selectedItem by remember { mutableIntStateOf(2) } // Default to "Standing" tab

    val laLigaState by standingsViewModel.laLigaStandings.collectAsStateWithLifecycle()
    val premierLeagueState by standingsViewModel.premierLeagueStandings.collectAsStateWithLifecycle()
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 30.dp)
        ) {
            // Search Bar (remains the same)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clickable { navController.navigate("standingDetail") },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFA01B22)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.search), // Replace with your actual drawable
//                        contentDescription = "Search",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(20.dp)
//                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Standing Details",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // La Liga Section
            LeagueSection(
                leagueName = "La Liga",
                countryName = "Spain",
                flagResId = R.drawable.spain,
                uiState = laLigaState
            )
            { standingsViewModel.fetchStandingsForLeague(StandingsViewModel.LA_LIGA_CODE) } // Use ClassName.CODE


            // Premier League Section
            LeagueSection(
                leagueName = "Premier League",
                countryName = "England",
                flagResId = R.drawable.england,
                uiState = premierLeagueState
            )
            { standingsViewModel.fetchStandingsForLeague(StandingsViewModel.PREMIER_LEAGUE_CODE) } // Use ClassName.CODE

            //other Section...

            Spacer(modifier = Modifier.height(80.dp)) // Bottom padding
        }

        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = Color(0xFF292929),
            contentColor = Color.White
        ) {
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 0) Text(
                            "Home",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 0) Spacer(modifier = Modifier.height(8.dp))
                        if (selectedItem == 0) Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 0,
                onClick = {
                    if (currentRoute != "liveScore") {
                        navController.navigate("liveScore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 0
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 1) Text(
                            "Explore",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 1) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 1) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.discovery),
                            contentDescription = "Explore",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 1,
                onClick = {
                    if (currentRoute != "explore") {
                        navController.navigate("explore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 1
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 2) Text(
                            "Standing",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 2) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 2) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.chart),
                            contentDescription = "Standing",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 2,
                onClick = {
                    if (currentRoute != "standing") {
                        navController.navigate("standing") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 2
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 3) Text(
                            "My Profile",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 3) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 3) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "My Profile",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 3,
                onClick = {
                    if (currentRoute != "profile") {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 3
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


@Composable
fun StandingsDetailScreen(navController: NavHostController) {
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }
    var selectedItem by remember { mutableStateOf(2) } // Default to "Standing" tab
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section with Spain and Flags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { /* Handle back navigation */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.spain), // Placeholder for Spain flag
                        contentDescription = "Spain Flag",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Spain",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = { /* Handle forward navigation */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward",
                        tint = Color.White
                    )
                }
            }

            // La Liga Logo and Text
            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(vertical = 26.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.laliga), // Downloaded La Liga logo
                    contentDescription = "La Liga Logo",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La Liga",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // All, Home, Away Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start, // Align buttons to the left
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Handle All click */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent // Set to transparent to use gradient background
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .width(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFD0353F), // 43%
                                    Color(0xFFA01B22)  // 100%
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(0f, 100f)
                            ),
                            shape = ButtonDefaults.shape
                        )
                ) {
                    Text(
                        text = "All",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* Handle Home click */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent // Set to transparent to use gradient background
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .width(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFD0353F), // 43%
                                    Color(0xFFA01B22)  // 100%
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(0f, 100f)
                            ),
                            shape = ButtonDefaults.shape
                        )
                ) {
                    Text(
                        text = "Home",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* Handle Away click */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent // Set to transparent to use gradient background
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .width(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFD0353F), // 43%
                                    Color(0xFFA01B22)  // 100%
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(0f, 100f)
                            ),
                            shape = ButtonDefaults.shape
                        )
                ) {
                    Text(
                        text = "Away",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Standings Table
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp)
                                .padding(start = 12.dp)
                        )
                        Text(
                            text = "Team",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "D",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "L",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Ga",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Gd",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Pts",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )
                    // Scrollable Table Rows
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        StandingsRowCard(position = 1, teamName = "Atlético Madrid", logoResId = R.drawable.atletico, draws = 2, losses = 1, goalsAgainst = 6, goalDifference = 23, points = 38, cardColor = Color(0xFFFFD700))
                        StandingsRowCard(position = 2, teamName = "Real Madrid", logoResId = R.drawable.realmadrid, draws = 4, losses = 3, goalsAgainst = 7, goalDifference = 15, points = 37, cardColor = Color(0xFF234D14))
                        StandingsRowCard(position = 3, teamName = "Barcelona", logoResId = R.drawable.barcelona, draws = 4, losses = 4, goalsAgainst = 9, goalDifference = 20, points = 34, cardColor = Color(0xFF234D14))
                        StandingsRowCard(position = 4, teamName = "Villarreal", logoResId = R.drawable.villareal, draws = 8, losses = 2, goalsAgainst = 10, goalDifference = 16, points = 32, cardColor = Color(0xFF234D14))
                        StandingsRowCard(position = 5, teamName = "Real Sociedad", logoResId = R.drawable.realsocieded, draws = 6, losses = 5, goalsAgainst = 13, goalDifference = 13, points = 30, cardColor = Color(0xFF1F3C72))
                        StandingsRowCard(position = 6, teamName = "Sevilla", logoResId = R.drawable.sevilla, draws = 3, losses = 4, goalsAgainst = 15, goalDifference = 12, points = 30, cardColor = Color(0xFF1F3C72))
                        StandingsRowCard(position = 7, teamName = "Granada", logoResId = R.drawable.granada, draws = 3, losses = 7, goalsAgainst = 20, goalDifference = 11, points = 24, cardColor = Color(0xFF181829))
                        StandingsRowCard(position = 8, teamName = "Celta Vigo", logoResId = R.drawable.celta, draws = 5, losses = 7, goalsAgainst = 25, goalDifference = 8, points = 23, cardColor = Color(0xFFA01B22))
                    }
                }
            }
        }


        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = Color(0xFF292929),
            contentColor = Color.White
        ) {
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 0) Text(
                            "Home",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 0) Spacer(modifier = Modifier.height(8.dp))
                        if (selectedItem == 0) Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 0,
                onClick = {
                    if (currentRoute != "liveScore") {
                        navController.navigate("liveScore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 0
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 1) Text(
                            "Explore",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 1) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 1) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.discovery),
                            contentDescription = "Explore",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 1,
                onClick = {
                    if (currentRoute != "explore") {
                        navController.navigate("explore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 1
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 2) Text(
                            "Standing",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 2) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 2) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.chart),
                            contentDescription = "Standing",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 2,
                onClick = {
                    if (currentRoute != "standing") {
                        navController.navigate("standing") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 2
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 3) Text(
                            "My Profile",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 3) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 3) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "My Profile",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 3,
                onClick = {
                    if (currentRoute != "profile") {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 3
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun MatchDetailScreen(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(1) } // Default to "Explore" tab
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .clickable { navController.navigate("liveScore") },
                verticalAlignment = Alignment.CenterVertically ,horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {  navController.navigate("liveScore") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UEFA Champions League",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Match Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Team Logos and Names
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Team 1
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF441818)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.arsenal),
                                    contentDescription = "Arsenal Logo",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Arsenal",
                                color = Color.White,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        // Score
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "-",
                                color = Color.White,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Team 2
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF441818)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.astonvilla),
                                    contentDescription = "Aston Villa Logo",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aston Villa",
                                color = Color.White,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Date and Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Monday, 12 Feb 2021",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = ".",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "02:30 am",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Title (Moved under Match Overview with Gradient)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier

                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE03F46), // 40%
                                Color(0xFFA01B22) // 72%
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, 100f)
                        ),
                        shape = RoundedCornerShape(70)
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Match Detail",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Match Stats (Updated to Goal Details)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Goal Details for Team 1 (Arsenal)
                    Text(
                        text = "Arsenal Goals:",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GoalDetail(time = "29'", player = "David")
                    GoalDetail(time = "jack'", player = "jack")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Goal Details for Team 2 (Aston Villa)
                    Text(
                        text = "Aston Villa Goals:",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GoalDetail(time = "19'", player = "james")
                    GoalDetail(time = "58'", player = "james")
                }
            }

            // Other Matches Section
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Other Match",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See all",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* Handle see all click */ }
                )
            }

            // Other Matches Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 12.dp)
                    .clickable { /* Handle card click */ },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF292929))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .padding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF441818)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.manu),
                            contentDescription = "Man United",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF441818)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chelsea),
                            contentDescription = "Chelsea",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Man United vs Chelsea FC",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "2", color = Color.White, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(50.dp))
                            Text(text = "-", color = Color.White, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(50.dp))
                            Text(text = "3", color = Color.White, fontSize = 15.sp)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .width(47.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF441818)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FT",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { /* Handle FT click */ }
                        )
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 12.dp)
                    .clickable { /* Handle card click */ },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF292929))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .padding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF441818)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tottenham),
                            contentDescription = "Tottenham",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF441818)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.southampton),
                            contentDescription = "Southampton",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tottenham vs Southampton",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "1", color = Color.White, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(50.dp))
                            Text(text = "-", color = Color.White, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(50.dp))
                            Text(text = "0", color = Color.White, fontSize = 15.sp)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .width(47.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF441818)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FT",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { /* Handle FT click */ }
                        )
                    }
                }
            }

            // Bottom padding to avoid overlap with navigation bar
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Bottom Navigation (fixed at the bottom)
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = Color(0xFF292929),
            contentColor = Color.White
        ) {
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 0) Text(
                            "Home",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 0) Spacer(modifier = Modifier.height(8.dp))
                        if (selectedItem == 0) Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 0,
                onClick = {
                    if (currentRoute != "liveScore") {
                        navController.navigate("liveScore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 0
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 1) Text(
                            "Explore",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 1) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 1) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.discovery),
                            contentDescription = "Explore",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 1,
                onClick = {
                    if (currentRoute != "explore") {
                        navController.navigate("explore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 1
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 2) Text(
                            "Standing",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 2) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 2) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.chart),
                            contentDescription = "Standing",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 2,
                onClick = {
                    if (currentRoute != "standing") {
                        navController.navigate("standing") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 2
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 3) Text(
                            "My Profile",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 3) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 3) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "My Profile",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 3,
                onClick = {
                    if (currentRoute != "profile") {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 3
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


// Reusable composable for goal details
@Composable
fun GoalDetail(time: String, player: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = player,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}



@Composable
fun StandingsRowCard(
    position: Int,
    teamName: String,
    logoResId: Int,
    draws: Int,
    losses: Int,
    goalsAgainst: Int,
    goalDifference: Int,
    points: Int,
    cardColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Position
            Text(
                text = position.toString(),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .width(40.dp)
                    .padding(start = 12.dp)

            )

            // Team Name with Logo
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = logoResId),
                        contentDescription = "$teamName Logo",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = teamName,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Draws
            Text(
                text = draws.toString(),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )

            // Losses
            Text(
                text = losses.toString(),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )

            // Goals Against
            Text(
                text = goalsAgainst.toString(),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )

            // Goal Difference
            Text(
                text = if (goalDifference >= 0) "+$goalDifference" else goalDifference.toString(),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )

            // Points
            Text(
                text = points.toString(),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}




@Composable
fun LeagueSection(
    leagueName: String,
    countryName: String,
    flagResId: Int,
    uiState: LeagueStandingsUiState,
    onRetry: () -> Unit,
) {
    // League Header Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)) // Match screen background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = "$countryName Flag",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = leagueName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = countryName, color = Color.White, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "More",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Standings Table Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF292929)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Table Header Row (static)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Team", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(3f)) // Increased weight for team name
                Text(text = "W", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "D", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "L", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "GA", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "GD", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "Pts", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Table Content based on UI State
            when (uiState) {
                is LeagueStandingsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.White
                    )
                }
                is LeagueStandingsUiState.Success -> {
                    if (uiState.standingsTable.isEmpty()) {
                        Text(
                            "No standings data available.",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
                        )
                    } else {
                        uiState.standingsTable.forEach { teamEntry ->
                            TeamRow(teamEntry = teamEntry)
                        }
                    }
                }
                is LeagueStandingsUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Error: ${uiState.message}", color = Color.Red)
                        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22))) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamRow(teamEntry: TableEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp) ,// نویگیشن به صفحه GoalDetail
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team Logo and Name
        Row(
            modifier = Modifier.weight(3f), // Increased weight
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(teamEntry.team.crest)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground), // Add a placeholder drawable
                error = painterResource(R.drawable.spain), // Add an error drawable
                contentDescription = "${teamEntry.team.name} crest",
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = teamEntry.team.shortName ?: teamEntry.team.name, // Prefer shortName
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(text = teamEntry.won.toString(), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = teamEntry.draw.toString(), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = teamEntry.lost.toString(), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = teamEntry.goalsAgainst.toString(), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = teamEntry.goalDifference.toString(), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = teamEntry.points.toString(), color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

fun formatUtcDateToLocalTime(utcDateString: String?, desiredFormat: String = "HH:mm"): String {
    if (utcDateString == null) return "N/A"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(utcDateString)

        val outputFormat = SimpleDateFormat(desiredFormat, Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        if (date != null) outputFormat.format(date) else "N/A"
    } catch (e: ParseException) {
        Log.e("DateUtils", "Error parsing date: $utcDateString", e)
        "Invalid Date"
    }
}

fun formatUtcDateToLocalDate(utcDateString: String?, desiredFormat: String = "EEE, d MMM"): String {
    if (utcDateString == null) return "N/A"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(utcDateString)

        val outputFormat = SimpleDateFormat(desiredFormat, Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        if (date != null) outputFormat.format(date) else "N/A"
    } catch (e: ParseException) {
        Log.e("DateUtils", "Error parsing date: $utcDateString", e)
        "Invalid Date"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScoreScreen(liveScoreViewModel: LiveScoreViewModel = viewModel() , navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }
    val laLigaMatchesState by liveScoreViewModel.laLigaMatches.collectAsStateWithLifecycle()
    val premierLeagueMatchesState by liveScoreViewModel.premierLeagueMatches.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            // ==================== Top App Bar ====================
            TopAppBar(
                title = { Text("LiveScore", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: Handle search action */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { /* TODO: Handle notifications action */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.notification),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF222222)
                )
            )
            // ================= End Top App Bar ===================

            // ================= Celebration Banner ================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),// نویگیشن به صفحه GoalDetail
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFD2428), Color(0xFF5F0709)),
                                    start = Offset.Zero,
                                    end = Offset.Infinite
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Liverpool UEFA\nChampion League\nCelebration",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Yesterday, 06:30 PM",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.jamesmilner),
                    contentDescription = "Trophy Celebration",
//                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (0).dp, y = (-41.8).dp)
                        .width(170.dp)
                        .height(220.dp)
                        .clip(RoundedCornerShape(bottomEnd = 60.dp))

                )
            }
            // ============== End Celebration Banner ===============

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                // --- La Liga ---
                item {
                    LeagueHeader(leagueName = "La Liga", countryName = "Spain", flagResId = R.drawable.spain)
                }
                when (val state = laLigaMatchesState) {
                    is MatchesUiState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                color = Color(0xFFA01B22)
                            )
                        }
                    }
                    is MatchesUiState.Success -> {
                        if (state.matches.isEmpty()) {
                            item {
                                Text(
                                    "No matches found for La Liga!",
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            items(state.matches, key = { match -> match.id }) { match ->
                                MatchCardComposable(match = match, navController = navController)
                            }
                        }
                    }
                    is MatchesUiState.Error -> {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Error: ${state.message}",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { liveScoreViewModel.fetchWeeklyMatchesForLeague(LiveScoreViewModel.LA_LIGA_CODE) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22))
                                ) {
                                    Text("Retry", color = Color.White)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(30.dp)) }

                //--- PL ---
                item {
                    LeagueHeader(leagueName = "Premier League", countryName = "England", flagResId = R.drawable.england)
                }
                when (val state = premierLeagueMatchesState) {
                    is MatchesUiState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                color = Color(0xFFA01B22)
                            )
                        }
                    }
                    is MatchesUiState.Success -> {
                        if (state.matches.isEmpty()) {
                            item {
                                Text(
                                    "No matches found for Premier League!",
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            items(state.matches, key = { match -> match.id }) { match ->
                                MatchCardComposable(match = match , navController = navController)
                            }
                        }
                    }
                    is MatchesUiState.Error -> {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Error: ${state.message}",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { liveScoreViewModel.fetchWeeklyMatchesForLeague(LiveScoreViewModel.PREMIER_LEAGUE_CODE) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22))
                                ) {
                                    Text("Retry", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = Color(0xFF292929),
            contentColor = Color.White
        ) {
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 0) Text(
                            "Home",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 0) Spacer(modifier = Modifier.height(8.dp))
                        if (selectedItem == 0) Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 0,
                onClick = {
                    if (currentRoute != "liveScore") {
                        navController.navigate("liveScore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 0
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 1) Text(
                            "Explore",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 1) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 1) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.discovery),
                            contentDescription = "Explore",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 1,
                onClick = {
                    if (currentRoute != "explore") {
                        navController.navigate("explore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 1
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 2) Text(
                            "Standing",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 2) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 2) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.chart),
                            contentDescription = "Standing",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 2,
                onClick = {
                    if (currentRoute != "standing") {
                        navController.navigate("standing") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 2
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 3) Text(
                            "My Profile",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 3) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 3) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "My Profile",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 3,
                onClick = {
                    if (currentRoute != "profile") {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 3
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }

    }
}

@Composable
fun LeagueHeader(leagueName: String, countryName: String, flagResId: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = "$countryName Flag",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = leagueName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = countryName,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "More",
                tint = Color.White,
                modifier = Modifier.size(24.dp).clickable { /* TODO: Handle click */ }
            )
        }
    }
}

@Composable
fun MatchCardComposable(match: Match, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { navController.navigate("goalDetail") },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF292929))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 10.dp), // Updated padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(2.5f), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(match.homeTeam.crest).crossfade(true).build(),
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = match.homeTeam.name,
                    modifier = Modifier.size(28.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = match.homeTeam.shortName ?: match.homeTeam.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.5f)
            ) {
                if (match.status == "FINISHED" || match.status == "IN_PLAY" || match.status == "PAUSED" || match.status == "LIVE") {
                    Text(
                        text = "${match.score.fullTime.home ?: "-"} - ${match.score.fullTime.away ?: "-"}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = formatUtcDateToLocalTime(match.utcDate),
                        color = Color(0xFFA01B22),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = when (match.status) {
                        "SCHEDULED", "TIMED" -> formatUtcDateToLocalDate(match.utcDate, "d MMM")
                        "IN_PLAY" -> "${match.minute ?: ""}′"
                        "PAUSED" -> "HT"
                        "FINISHED" -> "FT"
                        "POSTPONED" -> "POSTP"
                        "SUSPENDED" -> "SUSP"
                        "CANCELED" -> "CANC"
                        else -> match.status.take(4)
                    },
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row(modifier = Modifier.weight(2.5f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                Text(
                    text = match.awayTeam.shortName ?: match.awayTeam.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(match.awayTeam.crest).crossfade(true).build(),
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = match.awayTeam.name,
                    modifier = Modifier.size(28.dp).clip(CircleShape)
                )
            }
        }
    }
}


@Composable
fun SearchScreen(liveScoreViewModel: LiveScoreViewModel = viewModel() , navController: NavHostController) { //  ViewModel
    var selectedItem by remember { mutableStateOf(1) } // Default to "Explore" tab
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }
    val upcomingMatchesState by liveScoreViewModel.upcomingMatches.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        // Title (Fixed at Top Center)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopCenter) // Position at top center
                .padding(top = 30.dp) // Match the original vertical padding
                .background(color = Color.DarkGray, shape = RoundedCornerShape(70)) // pill shape
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "All upcoming matches",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 56.dp)
        ) {
            when (val state = upcomingMatchesState) {
                is MatchesUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = Color(0xFFA01B22)
                    )
                }
                is MatchesUiState.Success -> {
                    if (state.matches.isEmpty()) {
                        Text(
                            "No upcoming matches found for selected leagues.",
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.matches, key = { match -> match.id }) { match ->
                                MatchupItem(match = match , navController = navController)
                            }
                        }
                    }
                }
                is MatchesUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Error: ${state.message}",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                liveScoreViewModel.fetchUpcomingMatchesForLeagues(
                                    listOf(
                                        LiveScoreViewModel.LA_LIGA_CODE,
                                        LiveScoreViewModel.PREMIER_LEAGUE_CODE
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22))
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
            }
        }


        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = Color(0xFF292929),
            contentColor = Color.White
        ) {
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 0) Text(
                            "Home",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 0) Spacer(modifier = Modifier.height(8.dp))
                        if (selectedItem == 0) Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 0,
                onClick = {
                    if (currentRoute != "liveScore") {
                        navController.navigate("liveScore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 0
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 1) Text(
                            "Explore",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 1) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 1) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.discovery),
                            contentDescription = "Explore",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 1,
                onClick = {
                    if (currentRoute != "explore") {
                        navController.navigate("explore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 1
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 2) Text(
                            "Standing",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 2) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 2) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.chart),
                            contentDescription = "Standing",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 2,
                onClick = {
                    if (currentRoute != "standing") {
                        navController.navigate("standing") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 2
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedItem == 3) Text(
                            "My Profile",
                            color = Color(0xFFA01B22),
                            style = TextStyle(fontSize = 12.sp)
                        )
                        if (selectedItem == 3) Spacer(modifier = Modifier.height(4.dp))
                        if (selectedItem == 3) Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFA01B22), shape = CircleShape)
                        ) else Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "My Profile",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                label = null,
                selected = selectedItem == 3,
                onClick = {
                    if (currentRoute != "profile") {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    selectedItem = 3
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


@Composable
fun MatchupItem(match: Match, onCloseClicked: () -> Unit = {} , navController: NavHostController) {
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate("goalDetail") }, // نویگیشن به صفحه GoalDetail
        colors = CardDefaults.cardColors(containerColor = Color(0xFF292929)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team Logos (Together on the Left)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1 Logo (Home Team)
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF441818)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(match.homeTeam.crest)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "${match.homeTeam.name} Logo",
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Team 2 Logo (Away Team)
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF441818)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(match.awayTeam.crest)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "${match.awayTeam.name} Logo",
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Team Names, Dot, Date, and Time
            Column(
                modifier = Modifier.weight(1f), // Take remaining space
            ) {
                // Team Names
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Limit width to prevent card growth
                ) {
                    Text(
                        text = match.homeTeam.shortName ?: match.homeTeam.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "vs",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.awayTeam.shortName ?: match.awayTeam.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Date and Time
                Row {
                    Text(
                        text = formatUtcDateToLocalDate(match.utcDate, "EEE, d MMM yyyy"),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "·",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = formatUtcDateToLocalTime(match.utcDate, "hh:mm a"),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // Close Icon (Far Right)
            if (onCloseClicked != {}) {
                IconButton(
                    onClick = { navController.navigate("goalDetail") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF65656B)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavIconWithLabel(iconRes: Int, label: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isSelected) {
            Text(
                label,
                color = Color(0xFFA01B22),
                style = TextStyle(fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(Color(0xFFA01B22), shape = CircleShape)
            )
        } else {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            UserRepository(UserDatabase.getInstance(LocalContext.current).userDao())
        )
    )
) {
    var selectedItem by remember { mutableIntStateOf(3) } // Default to "My Profile" tab
    val currentRoute by remember { derivedStateOf { navController.currentDestination?.route } }

    val currentUser by userViewModel.currentUser.collectAsStateWithLifecycle()


    LaunchedEffect(currentUser, currentRoute) {
        if (currentUser == null && currentRoute == "myprofile") {
            navController.navigate("login") {
                popUpTo("myprofile") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        if (currentUser != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 30.dp, bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(80.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Gray) // Placeholder
                        ) {
                            // TODO: In the future, the ability to upload and display user profile images from the database or server will be added.
                            Image(
                                painter = painterResource(id = R.drawable.profile1),
                                contentDescription = "Profile Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFE03F46), Color(0xFFA01B22))
                                    )
                                )
                                .clickable(onClick = { /* TODO: Handle edit profile image */ }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Edit Profile Image",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentUser!!.name.ifBlank { "User Name" },
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Someone loves football.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = { /* TODO:  */ },
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .widthIn(min = 150.dp, max = 200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA01B22)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Edit My Profile",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF292929)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column {
                        DetailRow(
                            icon = painterResource(id = R.drawable.profile),
                            label = "Name",
                            value = currentUser!!.name.ifBlank { "Not set" },
                            onClick = { /* TODO: Handle edit name */ }
                        )
                        DetailRow(
                            icon = painterResource(id = R.drawable.message),
                            label = "Email",
                            value = currentUser!!.email,
                            onClick = { /* TODO: Handle edit email */ }
                        )
                        DetailRow(
                            icon = painterResource(id = R.drawable.call),
                            label = "Phone",
                            value = currentUser!!.phoneNumber.ifBlank { "Not set" },
                            onClick = { /* TODO: Handle edit phone */ }
                        )
                        Button(
                            onClick = {
                                userViewModel.logoutUser()
                                navController.navigate("login") {
                                    popUpTo("liveScore") { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (currentRoute == "myprofile") {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .padding(top = 50.dp),
                    color = Color(0xFFA01B22)
                )
            }
        }

        // NavigationBar
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            containerColor = Color(0xFF292929),
            contentColor = Color.White
        ) {
            NavigationBarItem(
                selected = selectedItem == 0,
                onClick = {
                    if (currentRoute != "liveScore") {
                        navController.navigate("liveScore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    }
                    selectedItem = 0
                },
                icon = { NavIconWithLabel(R.drawable.home, "Home", selectedItem == 0) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = Color(0xFFA01B22), unselectedIconColor = Color.Gray)
            )
            NavigationBarItem(
                selected = selectedItem == 1,
                onClick = {
                    if (currentRoute != "explore") {
                        navController.navigate("explore") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    }
                    selectedItem = 1
                },
                icon = { NavIconWithLabel(R.drawable.discovery, "Explore", selectedItem == 1) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = Color(0xFFA01B22), unselectedIconColor = Color.Gray)
            )
            NavigationBarItem(
                selected = selectedItem == 2,
                onClick = {
                    if (currentRoute != "standing") {
                        navController.navigate("standing") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    }
                    selectedItem = 2
                },
                icon = { NavIconWithLabel(R.drawable.chart, "Standing", selectedItem == 2) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = Color(0xFFA01B22), unselectedIconColor = Color.Gray)
            )
            NavigationBarItem(
                selected = selectedItem == 3,
                onClick = {
                    if (currentUser == null) {
                        if (currentRoute != "login") {
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }

                    } else {
                        if (currentRoute != "myprofile") {
                            navController.navigate("myprofile") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                        selectedItem = 3
                    }
                },
                icon = { NavIconWithLabel(R.drawable.profile, "My Profile", selectedItem == 3 && currentUser != null) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = Color(0xFFA01B22), unselectedIconColor = Color.Gray)
            )
        }
    }
}

