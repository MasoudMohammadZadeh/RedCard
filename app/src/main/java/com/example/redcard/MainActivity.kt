package com.example.redcard
import android.os.Bundle
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedCardTheme {
                SportsLoginCard()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsLoginCard() {
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
                    scope = scope
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
                SignUpForm()
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
    scope: CoroutineScope
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val userDao = remember { UserDatabase.getInstance(context).userDao() }
    val repository = remember { UserRepository(userDao) }

    val userViewModel: UserViewModel =
        viewModel(factory = UserViewModelFactory(repository))

    val loginResult by userViewModel.loginResult.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()


    loginResult?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, "Welcome, ${it.name}!", Toast.LENGTH_SHORT).show()
//            onLoginSuccess(it)
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

@Composable
fun SignUpForm() {
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

    if (registerSuccess) {
        LaunchedEffect(registerSuccess) {
            Toast.makeText(context, "sing up ✅", Toast.LENGTH_SHORT).show()
            // To Do
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
                    name = "user",
                    email = email,
                    password = password,
                    phoneNumber = phone
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SignUpFormPreview() {
    SignUpForm()
}

fun onLoginSuccess() {
    // To do and pass to profile
    // Use in the login
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen() {
    var selectedItem by remember { mutableStateOf(3) } // Default to "My Profile" tab
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
                        if (selectedItem == 0) Text("Home", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 0 },
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
                        if (selectedItem == 1) Text("Explore", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 1 },
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
                        if (selectedItem == 2) Text("Standing", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 2 },
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
                        if (selectedItem == 3) Text("My Profile", color = Color(0xFFA01B22))
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
                        // Red dot
//                        if (selectedItem == 3) Box(
//                            modifier = Modifier
//                                .align(Alignment.End)
//                                .offset(x = 8.dp)
//                                .size(6.dp)
//                                .background(Color(0xFFA01B22), shape = CircleShape)
//                        )
                    }
                },
                label = null,
                selected = selectedItem == 3,
                onClick = { selectedItem = 3 },
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
fun StandingsScreen() {
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
                .verticalScroll(rememberScrollState())
                .padding(vertical = 30.dp)
        ) {
            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF292929)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Search your competition ...",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            // La Liga Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.spain),
                            contentDescription = "Spain Flag",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "La Liga",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Spain",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

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
                    // League Header


                    Spacer(modifier = Modifier.height(16.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Team",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = "D",
                            color = Color.White,
                            fontSize = 14.sp,

                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "L",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "GA",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "GD",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Pts",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Table Rows (La Liga Teams)
                    TeamRow(
                        logo = R.drawable.atletico,
                        name = "Atlético Madrid",
                        draws = "2",
                        losses = "1",
                        goalsAgainst = "6",
                        goalDifference = "23",
                        points = "38"
                    )
                    TeamRow(
                        logo = R.drawable.realmadrid,
                        name = "Real Madrid",
                        draws = "4",
                        losses = "3",
                        goalsAgainst = "7",
                        goalDifference = "15",
                        points = "37"
                    )
                    TeamRow(
                        logo = R.drawable.barcelona,
                        name = "Barcelona",
                        draws = "4",
                        losses = "4",
                        goalsAgainst = "9",
                        goalDifference = "20",
                        points = "34"
                    )
                    TeamRow(
                        logo = R.drawable.villareal,
                        name = "Villareal",
                        draws = "8",
                        losses = "2",
                        goalsAgainst = "10",
                        goalDifference = "16",
                        points = "32"
                    )
                }
            }

            // Premier League Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.england),
                            contentDescription = "Spain Flag",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Premier League",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "England",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
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
                    // League Header


                    Spacer(modifier = Modifier.height(16.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Team",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = "D",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "L",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "GA",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "GD",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Pts",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Table Rows (Premier League Teams)
                    TeamRow(
                        logo = R.drawable.liverpool,
                        name = "Liverpool",
                        draws = "6",
                        losses = "2",
                        goalsAgainst = "22",
                        goalDifference = "16",
                        points = "33"
                    )
                    TeamRow(
                        logo = R.drawable.manu,
                        name = "Man United",
                        draws = "3",
                        losses = "3",
                        goalsAgainst = "24",
                        goalDifference = "9",
                        points = "33"
                    )
                    TeamRow(
                        logo = R.drawable.liecster,
                        name = "Leicester City",
                        draws = "2",
                        losses = "5",
                        goalsAgainst = "21",
                        goalDifference = "10",
                        points = "32"
                    )
                    TeamRow(
                        logo = R.drawable.arsenal,
                        name = "Arsenal",
                        draws = "8",
                        losses = "2",
                        goalsAgainst = "10",
                        goalDifference = "16",
                        points = "32"
                    )
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
                        if (selectedItem == 0) Text("Home", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 0 },
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
                        if (selectedItem == 1) Text("Explore", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 1 },
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
                        if (selectedItem == 2) Text("Standing", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 2 },
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
                        if (selectedItem == 3) Text("My Profile", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 3 },
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

// Reusable TeamRow composable for table rows
@Composable
fun TeamRow(
    logo: Int,
    name: String,
    draws: String,
    losses: String,
    goalsAgainst: String,
    goalDifference: String,
    points: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team Logo and Name
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = logo),
                contentDescription = name,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1, // Restrict to one line
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Draws
        Text(
            text = draws,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Losses
        Text(
            text = losses,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Goals Against
        Text(
            text = goalsAgainst,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Goal Difference
        Text(
            text = goalDifference,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Points
        Text(
            text = points,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun LiveScoreScreen() {
    var selectedItem by remember { mutableStateOf(0) }
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
                .padding(vertical = 30.dp)
        ) {
            // Top App Bar
            TopAppBar(
                title = { Text("LiveScore", color = Color.White) },
                actions = {
                    IconButton(onClick = { /* Handle search */ }) {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { /* Handle notifications */ }) {
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

            // Celebration Banner with your image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // Card with main content and fixed height
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(161.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    0.0f to Color(0xFFFD2428),
                                    0.84f to Color(0xFF5F0709)
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(vertical = 40.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.padding(end = 16.dp)) {
                                Text(
                                    text = "Liverpool UEFA\nChampion League\nCelebration",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Yesterday, 06:30 PM",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .align(Alignment.CenterEnd)
                        .offset(y = -(31.dp), x = -(0.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jamesmilner),
                        contentDescription = "Trophy",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // La Liga Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.spain),
                            contentDescription = "Spain Flag",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "La Liga",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Spain",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Match Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp)
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
                            painter = painterResource(id = R.drawable.barcelona),
                            contentDescription = "Barcelona",
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
                            painter = painterResource(id = R.drawable.realmadrid),
                            contentDescription = "Real Madrid",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Barcelona vs Real Madrid",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "2", color = Color.White, fontSize = 15.sp)
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
                            text = "HT",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { /* Handle HT click */ }
                        )
                    }
                }
            }

            // Premier League Section (repeated content, you can modify as needed)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.spain),
                            contentDescription = "Spain Flag",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Premier League",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "England",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Additional Match Cards (repeated content, you can modify as needed)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp)
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
                            painter = painterResource(id = R.drawable.barcelona),
                            contentDescription = "Barcelona",
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
                            painter = painterResource(id = R.drawable.realmadrid),
                            contentDescription = "Real Madrid",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Barcelona vs Real Madrid",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "2", color = Color.White, fontSize = 15.sp)
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
                            text = "HT",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { /* Handle HT click */ }
                        )
                    }
                }
            }
            // Add some bottom padding to ensure content doesn't overlap with the navigation bar
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
                        if (selectedItem == 0) Text("Home", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 0 },
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
                        if (selectedItem == 1) Text("Explore", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 1 },
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
                        if (selectedItem == 2) Text("Standing", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 2 },
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
                        if (selectedItem == 3) Text("My Profile", color = Color(0xFFA01B22))
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
                onClick = { selectedItem = 3 },
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