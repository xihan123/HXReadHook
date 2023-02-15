package cn.xihan.hxds

import android.os.Environment
import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import com.highcapable.yukihookapi.hook.log.loggerE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * @项目名 : HXReadHook
 * @作者 : MissYang
 * @创建时间 : 2023/2/15 19:23
 * @介绍 :
 */
/**
 * 配置模型
 * @param packageName 包名
 * @param enableHideAppIcon 是否隐藏应用图标
 * @param interceptOption 拦截配置
 */
@Keep
@Serializable
data class OptionEntity(
    @SerialName("packageName") var packageName: String = "com.hongxiu.app",
    @SerialName("enableHideAppIcon") var enableHideAppIcon: Boolean = false,
    @SerialName("interceptOption") var interceptOption: InterceptOption = InterceptOption(),
) {
    /**
     * 拦截配置
     * @param configurations 拦截配置列表
     */
    @Keep
    @Serializable
    @Immutable
    data class InterceptOption(
        @SerialName("configurations") var configurations: List<SelectedModel> = listOf(
            SelectedModel("青少年模式弹框"),
            SelectedModel("校验和上传文件"),
            SelectedModel("初始化Bugly"),
            SelectedModel("初始化广告"),
            SelectedModel("闪屏页广告"),
        )
    )

    /**
     * 选中模型
     * @param title 标题
     * @param selected 是否选中
     */
    @Keep
    @Serializable
    data class SelectedModel(
        @SerialName("title") var title: String = "",
        @SerialName("selected") var selected: Boolean = false,
    )
}

/**
 * 读取配置文件
 */
fun readOptionFile(): File? = try {
    val downloadFile = File(
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/HXReader",
        "option.json"
    )
    downloadFile.parentFile?.mkdirs()
    if (!downloadFile.exists()) {
        downloadFile.createNewFile()
        downloadFile.writeText(Json.encodeToString(OptionEntity()))
    }
    downloadFile
} catch (e: Throwable) {
    loggerE(msg = "readOptionFile: ${e.message}")
    null
}

/**
 * 写入配置文件
 */
fun writeOptionFile(optionEntity: OptionEntity): Boolean =
    try {
        readOptionFile()?.writeText(Json.encodeToString(optionEntity))
        true
    } catch (e: Exception) {
        loggerE(msg = "writeOptionFile: ${e.message}")
        false
    }

/**
 *读取配置文件模型
 */
fun readOptionEntity(): OptionEntity {
    val file = readOptionFile() ?: return OptionEntity()
    return try {
        if (file.readText().isNotEmpty()) {
            try {
                val kJson = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
                kJson.decodeFromString<OptionEntity>(file.readText()).apply {
                    val newOptionEntity = OptionEntity()
                    val interceptConfigurations = interceptOption.configurations.toMutableList()
                    val newConfiguration = newOptionEntity.interceptOption.configurations
                    if (interceptConfigurations.size != newConfiguration.size) {
                        interceptOption.configurations =
                            interceptConfigurations.updateSelectedListOptionEntity(newConfiguration)
                    }

                }
            } catch (e: Exception) {
                loggerE(msg = "readOptionFile: ${e.message}")
                OptionEntity()
            }
        } else {
            OptionEntity()
        }
    } catch (e: Exception) {
        loggerE(msg = "readOptionEntity: ${e.message}")
        OptionEntity()
    }

}

/**
 * 更新配置
 */
fun updateOptionEntity(): Boolean = writeOptionFile(HookEntry.optionEntity)

