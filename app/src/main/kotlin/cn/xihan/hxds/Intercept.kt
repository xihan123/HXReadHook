package cn.xihan.hxds

import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType

/**
 * @项目名 : HXReadHook
 * @作者 : MissYang
 * @创建时间 : 2023/2/15 19:45
 * @介绍 :
 */
/**
 * 拦截配置
 */
fun PackageParam.interceptOption(
    version: Int,
    configurations: List<OptionEntity.SelectedModel>,
) {
    if (configurations.isEmpty()) return
    configurations.filter { it.selected }.forEach { selected ->
        when (selected.title) {
            "青少年模式弹框" -> interceptQSNDialog(version)
            "校验和上传文件" -> interceptCheckAndUploadDumpFile(version)
            "初始化Bugly" -> interceptInitBugly(version)
            "初始化广告" -> interceptInitAd(version)
            "闪屏页广告" -> interceptSplashAd(version)
        }
    }
}

/**
 * 拦截青少年模式弹框
 */
fun PackageParam.interceptQSNDialog(versionCode: Int) {
    when (versionCode) {
        10062950 -> {
            findClass("com.readx.pages.main.BaseMainActivity").hook {
                injectMember {
                    method {
                        name = "showTeenagerDialog"
                        emptyParam()
                        returnType = UnitType
                    }
                    intercept()
                }
            }
        }

        else -> "拦截青少年模式弹框".printlnNotSupportVersion(versionCode)
    }
}

/**
 * 拦截校验和上传文件
 */
fun PackageParam.interceptCheckAndUploadDumpFile(versionCode: Int) {
    when (versionCode) {
        10062950 -> {
            findClass("com.readx.dump.DumpManager").hook {
                injectMember {
                    method {
                        name = "checkAndUploadDumpFile"
                        emptyParam()
                        returnType = UnitType
                    }
                    intercept()
                }
            }
        }

        else -> "拦截校验和上传文件".printlnNotSupportVersion(versionCode)
    }
}

/**
 * 拦截初始化Bugly
 */
fun PackageParam.interceptInitBugly(versionCode: Int) {
    when (versionCode) {
        10062950 -> {
            findClass("com.readx.util.FastBootUtil").hook {
                injectMember {
                    method {
                        name = "initBugly"
                        emptyParam()
                        returnType = UnitType
                    }
                    intercept()
                }
            }
        }

        else -> "拦截初始化Bugly".printlnNotSupportVersion(versionCode)
    }
}

/**
 * 拦截初始化广告
 */
fun PackageParam.interceptInitAd(versionCode: Int) {
    when (versionCode) {
        10062950 -> {
            findClass("com.readx.ads.HXADManager").hook {
                injectMember {
                    method {
                        name = "initAd"
                        paramCount(1)
                        returnType = UnitType
                    }
                    intercept()
                }
            }

            findClass("com.qq.e.comm.constants.CustomPkgConstants").hook {
                injectMember {
                    method {
                        name = "getAssetPluginName"
                        emptyParam()
                        returnType = StringClass
                    }
                    replaceTo("")
                }
            }

            findClass("com.qq.e.comm.b").hook {
                injectMember {
                    method {
                        name = "a"
                        param(ContextClass)
                        returnType = BooleanType
                    }
                    replaceToFalse()
                }
            }
        }

        else -> "拦截初始化广告".printlnNotSupportVersion(versionCode)
    }
}

/**
 * 拦截闪屏页广告
 */
fun PackageParam.interceptSplashAd(versionCode: Int) {
    when (versionCode) {
        10062950 -> {
            findClass("com.readx.pages.SplashActivity").hook {
                injectMember {
                    method {
                        name = "displaySplashScreen"
                        emptyParam()
                        returnType = UnitType
                    }
                    replaceUnit {
                        instance.current {
                            method {
                                name = "gotoMainActivity"
                                paramCount(1)
                            }.call(null)
                        }
                    }
                }
            }
        }

        else -> "拦截闪屏页广告".printlnNotSupportVersion(versionCode)
    }
}

