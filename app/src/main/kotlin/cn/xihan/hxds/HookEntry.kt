package cn.xihan.hxds

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.xihan.hxds.HookEntry.Companion.optionEntity
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.log.YukiHookLogger
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit


/**
 * @项目名 : HXReadHook
 * @作者 : MissYang
 * @创建时间 : 2023/2/15 17:24
 * @介绍 :
 */
@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onInit() {
        YukiHookAPI.configs {
            YukiHookLogger.Configs.tag = "yuki"
            YukiHookLogger.Configs.isEnable = BuildConfig.DEBUG
        }
    }

    override fun onHook() = YukiHookAPI.encase {

        loadApp(name = HX_PACKAGE_NAME) {


            interceptOption(versionCode, optionEntity.interceptOption.configurations)

            /**
             * 试用模式
             */
            /*
            findClass("com.readx.probation.ProbationManager").hook {
                injectMember {
                    method {
                        name = "isProbationMode"
                        emptyParam()
                        returnType = BooleanType
                    }
                    replaceToFalse()
                }
            }

             */

            /**
             * 获取 OkHttp 所有拦截器
             */
            /*
            findClass("okhttp3.OkHttpClient").hook {
                injectMember {
                    constructor {
                        paramCount(1)
                    }
                    beforeHook {
                        val builder = args.getOrNull(0) ?: return@beforeHook
                        val firstUserInterceptorCls =
                            builder.getParam<ArrayList<*>>("interceptors")?.getOrNull(0)?.javaClass
                                ?: return@beforeHook
                        "name: ${firstUserInterceptorCls.name}".loge()

                    }
                }
            }

             */

            /*
            findClass("com.readx.util.QDHttpUtils\$1").hook {
                injectMember {
                    method {
                        name = "getHeaders"
                        paramCount(3)
                    }
                    afterHook {
                        val headers = result as? Map<*, *> ?: return@afterHook

                        "headers: $headers".loge()
                    }
                }
            }

             */

            findClass("com.readx.pages.reader.QDReaderActivity").hook {
                injectMember {
                    method {
                        name = "onCreate"
                        paramCount(1)
                        returnType = UnitType
                    }
                    afterHook {
                        instance<Activity>().registerModuleAppActivities()
                    }
                }
            }

            findClass("com.qidian.QDReader.readerengine.view.QDSuperEngineView").hook {
                injectMember {
                    method {
                        name = "showSettingDialog"
                        emptyParam()
                        returnType = UnitType
                    }
                    afterHook {
                        val relativeLayout = instance as? RelativeLayout
                        relativeLayout?.let {
                            it.context.startActivity(Intent(it.context, MainActivity::class.java))
                        }

                    }
                }
            }


        }

    }

    companion object {
        val HX_PACKAGE_NAME by lazy {
            optionEntity.packageName.ifBlank { "com.hongxiu.app" }
        }

        val versionCode by lazy { getSystemContext().getVersionCode(HX_PACKAGE_NAME) }

        val optionEntity = readOptionEntity()
    }

}
