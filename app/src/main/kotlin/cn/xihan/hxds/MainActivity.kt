package cn.xihan.hxds

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.hjq.permissions.*

/**
 * @项目名 : HXReadHook
 * @作者 : MissYang
 * @创建时间 : 2023/2/15 19:19
 * @介绍 :
 */
class MainActivity : ModuleAppCompatActivity() {

    val versionCode by lazy { getVersionCode(HookEntry.HX_PACKAGE_NAME) }

    override val moduleTheme =
        com.google.android.material.R.style.Theme_Material3_DayNight_NoActionBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mdc3Theme {
                val systemUiController = rememberSystemUiController()
                val darkIcons = isSystemInDarkTheme()
                SideEffect {
                    systemUiController.apply {
                        setSystemBarsColor(
                            color = Color.Transparent, darkIcons = !darkIcons
                        )
                        setNavigationBarColor(
                            color = Color.Transparent, darkIcons = !darkIcons
                        )
                    }

                }
                ComposeContent()
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ComposeContent() {
        val permission = rememberMutableStateOf(
            value = XXPermissions.isGranted(
                this, if (this.applicationInfo.targetSdkVersion > 30) arrayOf(
                    Permission.MANAGE_EXTERNAL_STORAGE, Permission.REQUEST_INSTALL_PACKAGES
                ) else Permission.Group.STORAGE.plus(Permission.REQUEST_INSTALL_PACKAGES)
            )
        )

        val context = LocalContext.current
        Scaffold(
            modifier = M.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(text = "HXReadHook")
                }, modifier = M.fillMaxWidth(), actions = {
                    Row {
                        IconButton(onClick = {
                            restartApplication()
                        }) {
                            Icon(Icons.Filled.Refresh, null)
                        }
                        IconButton(onClick = {
                            finish()
                        }) {
                            Icon(Icons.Filled.ExitToApp, null)
                        }
                    }
                }, navigationIcon = {}, scrollBehavior = null
                )
            }
        ) { paddingValues ->
            if (permission.value) {
                Column(
                    modifier = M
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                ) {

                    Card(
                        modifier = M
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        TextSetting(title = "主设置", showRightIcon = false, bigTitle = true)

                        SwitchSetting(title = "隐藏桌面图标",
                            checked = rememberMutableStateOf(value = HookEntry.optionEntity.enableHideAppIcon),
                            onCheckedChange = {
                                HookEntry.optionEntity.enableHideAppIcon = it
                                safeRun {
                                    if (it) {
                                        context.hideAppIcon()
                                    } else {
                                        context.showAppIcon()
                                    }
                                }
                            })

                        TextSetting(title = "打赏", subTitle = "", onClick = {
                            context.openUrl("https://github.com/xihan123/QDReadHook#%E6%89%93%E8%B5%8F")
                        })

                        TextSetting(
                            title = "QDReadHook模块交流群",
                            subTitle = "727983520",
                            onClick = {
                                context.joinQQGroup("JdqL9prgQ3epIUed3weaEkJwtNgNQaWa")
                            })

                        TextSetting(title = "作者", subTitle = "希涵", onClick = {
                            context.openUrl("https://github.com/xihan123")
                        })

                        TextSetting(
                            title = "版本号",
                            subTitle = BuildConfig.VERSION_NAME,
                            showRightIcon = false,
                            onClick = {
                                context.checkModuleUpdate()
                            })
                    }


                    Card(
                        modifier = M
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {

                        TextSetting(title = "拦截设置", showRightIcon = false, bigTitle = true)

                        TextSetting(title = "拦截设置列表", modifier = M.padding(4.dp), onClick = {
                            context.multiChoiceSelector(HookEntry.optionEntity.interceptOption.configurations)
                        })

                    }

                }


            } else {
                Column(
                    modifier = M.padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "需要存储权限",
                        modifier = M.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = {
                        requestPermission(
                            onGranted = {
                                permission.value = true
                            }, onDenied = {
                                permission.value = false
                                jumpToPermission()
                            }
                        )

                    }) {
                        Text("请求权限")
                    }
                }

            }

        }

    }


}


/**
 * 文本设置模型
 * @param title 标题
 * @param subTitle 副标题
 * @param onClick 点击事件
 * @param modifier Modifier
 * @param showRightIcon 是否显示右侧图标
 * @param bigTitle 是否大标题
 */
@Composable
fun TextSetting(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String = "",
    showRightIcon: Boolean = true,
    bigTitle: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = M
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                modifier = M.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                style = if (bigTitle) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start
            )
            if (subTitle.isNotEmpty()) {

                Spacer(modifier = M.height(4.dp))

                Text(
                    text = subTitle,
                    overflow = TextOverflow.Visible,
                    modifier = M.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
        }

        if (showRightIcon) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = M
                    .padding(8.dp)
                    .size(20.dp)
            )
        }
    }
}

/**
 * Switch设置模型
 * @param title 标题
 * @param subTitle 副标题
 * @param checked 是否选中
 * @param onCheckedChange 选中状态改变事件
 * @param enabled 是否可用
 */
@Composable
fun SwitchSetting(
    title: String,
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    subTitle: String = "",
    onCheckedChange: (Boolean) -> Unit = {},
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.clickable(onClick = {
            if (enabled) {
                checked.value = !checked.value
                onCheckedChange(checked.value)
                updateOptionEntity()
            }
        }), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = M
                .weight(1f)
                .padding(8.dp),
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start
            )
            if (subTitle.isNotEmpty()) {
                Text(
                    text = subTitle,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Visible,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
        }


        Switch(
            modifier = M.scale(0.7f), checked = checked.value, onCheckedChange = {
                checked.value = it
                onCheckedChange(it)
                updateOptionEntity()
            }, enabled = enabled
        )

    }
}