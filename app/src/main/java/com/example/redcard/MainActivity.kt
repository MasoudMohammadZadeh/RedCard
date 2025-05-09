package com.example.redcard
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
