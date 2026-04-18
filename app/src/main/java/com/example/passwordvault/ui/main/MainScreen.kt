package com.example.passwordvault.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passwordvault.ui.auth.LoginScreen
import com.example.passwordvault.ui.components.TechBackground
import com.example.passwordvault.ui.theme.TechDarkTheme

/**
 * 主屏幕容器
 * 管理应用的主要导航和界面
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 根据认证状态显示不同界面
    when (uiState) {
        is MainUiState.Unauthenticated -> {
            // 显示登录界面
            LoginScreen(
                onLoginSuccess = { viewModel.onLoginSuccess() },
                onSetupPin = { /* 处理设置PIN码 */ }
            )
        }
        
        is MainUiState.Authenticated -> {
            // 显示主应用界面
            AuthenticatedApp()
        }
        
        is MainUiState.Locked -> {
            // 显示锁定界面
            LockedScreen(
                lockDuration = (uiState as MainUiState.Locked).duration,
                onUnlock = { viewModel.unlock() }
            )
        }
    }
}

/**
 * 已认证的应用主界面
 */
@Composable
private fun AuthenticatedApp() {
    val navController = rememberNavController()
    
    TechBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = TechDarkTheme.BackgroundDark,
            contentColor = TechDarkTheme.TextPrimary,
            topBar = {
                MainAppBar(navController = navController)
            },
            bottomBar = {
                MainBottomBar(navController = navController)
            },
            floatingActionButton = {
                MainFloatingActionButton()
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MainNavigation(navController = navController)
            }
        }
    }
}

/**
 * 主应用导航
 */
@Composable
private fun MainNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = MainDestination.Passwords.route
    ) {
        // 密码列表界面
        composable(MainDestination.Passwords.route) {
            PasswordsScreen()
        }
        
        // 分类界面
        composable(MainDestination.Categories.route) {
            CategoriesScreen()
        }
        
        // 收藏界面
        composable(MainDestination.Favorites.route) {
            FavoritesScreen()
        }
        
        // 设置界面
        composable(MainDestination.Settings.route) {
            SettingsScreen()
        }
    }
}

/**
 * 主应用顶部栏
 */
@Composable
private fun MainAppBar(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "密码保险箱",
                color = TechDarkTheme.TechBlue,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = TechDarkTheme.SurfaceDark,
            scrolledContainerColor = TechDarkTheme.SurfaceDark
        ),
        navigationIcon = {
            // 菜单按钮
            IconButton(onClick = { /* 打开导航菜单 */ }) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Menu,
                    contentDescription = "菜单",
                    tint = TechDarkTheme.TextPrimary
                )
            }
        },
        actions = {
            // 搜索按钮
            IconButton(onClick = { /* 打开搜索 */ }) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = TechDarkTheme.TextPrimary
                )
            }
            
            // 更多选项
            IconButton(onClick = { /* 打开更多选项 */ }) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.MoreVert,
                    contentDescription = "更多",
                    tint = TechDarkTheme.TextPrimary
                )
            }
        }
    )
    
    // 搜索栏（可展开）
    if (searchQuery.isNotEmpty()) {
        // 显示搜索栏
    }
}

/**
 * 主应用底部导航栏
 */
@Composable
private fun MainBottomBar(navController: NavHostController) {
    val currentDestination = navController.currentDestination?.route
    
    NavigationBar(
        containerColor = TechDarkTheme.SurfaceDark,
        contentColor = TechDarkTheme.TextPrimary
    ) {
        // 密码
        NavigationBarItem(
            selected = currentDestination == MainDestination.Passwords.route,
            onClick = {
                navController.navigate(MainDestination.Passwords.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                    contentDescription = "密码"
                )
            },
            label = {
                Text(text = "密码")
            }
        )
        
        // 分类
        NavigationBarItem(
            selected = currentDestination == MainDestination.Categories.route,
            onClick = {
                navController.navigate(MainDestination.Categories.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Category,
                    contentDescription = "分类"
                )
            },
            label = {
                Text(text = "分类")
            }
        )
        
        // 收藏
        NavigationBarItem(
            selected = currentDestination == MainDestination.Favorites.route,
            onClick = {
                navController.navigate(MainDestination.Favorites.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Favorite,
                    contentDescription = "收藏"
                )
            },
            label = {
                Text(text = "收藏")
            }
        )
        
        // 设置
        NavigationBarItem(
            selected = currentDestination == MainDestination.Settings.route,
            onClick = {
                navController.navigate(MainDestination.Settings.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                    contentDescription = "设置"
                )
            },
            label = {
                Text(text = "设置")
            }
        )
    }
}

/**
 * 主浮动操作按钮
 */
@Composable
private fun MainFloatingActionButton() {
    FloatingActionButton(
        onClick = { /* 添加新密码 */ },
        containerColor = TechDarkTheme.TechBlue,
        contentColor = androidx.compose.ui.graphics.Color.White
    ) {
        androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Add,
            contentDescription = "添加"
        )
    }
}

/**
 * 密码列表界面
 */
@Composable
private fun PasswordsScreen() {
    // 这里将显示密码列表
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = "密码列表界面",
            color = TechDarkTheme.TextPrimary
        )
    }
}

/**
 * 分类界面
 */
@Composable
private fun CategoriesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = "分类界面",
            color = TechDarkTheme.TextPrimary
        )
    }
}

/**
 * 收藏界面
 */
@Composable
private fun FavoritesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = "收藏界面",
            color = TechDarkTheme.TextPrimary
        )
    }
}

/**
 * 设置界面
 */
@Composable
private fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = "设置界面",
            color = TechDarkTheme.TextPrimary
        )
    }
}

/**
 * 锁定界面
 */
@Composable
private fun LockedScreen(
    lockDuration: Long,
    onUnlock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = "🔒",
            fontSize = 64.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "应用已锁定",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = TechDarkTheme.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "请重新验证以继续使用",
            fontSize = 14.sp,
            color = TechDarkTheme.TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 解锁按钮
        Button(
            onClick = onUnlock,
            colors = ButtonDefaults.buttonColors(
                containerColor = TechDarkTheme.TechBlue,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Text(text = "解锁")
        }
    }
}

/**
 * 主界面状态
 */
sealed class MainUiState {
    data object Unauthenticated : MainUiState()
    data object Authenticated : MainUiState()
    data class Locked(val duration: Long) : MainUiState()
}

/**
 * 主导航目的地
 */
enum class MainDestination(val route: String) {
    Passwords("passwords"),
    Categories("categories"),
    Favorites("favorites"),
    Settings("settings")
}

// 导入必要的Compose组件
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign